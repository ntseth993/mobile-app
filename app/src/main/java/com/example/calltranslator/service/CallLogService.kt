package com.example.calltranslator.service

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.CallLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CallLogEntry(
    val id: String,
    val name: String?,
    val phoneNumber: String,
    val type: CallType,
    val timestamp: Long,
    val duration: Long,
    val date: String = formatTimestamp(timestamp)
)

enum class CallType {
    INCOMING, OUTGOING, MISSED, VOICEMAIL, REJECTED, BLOCKED
}

fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    return format.format(date)
}

class CallLogService(private val context: Context) {
    
    suspend fun getRecentCalls(limit: Int = 50): List<CallLogEntry> = withContext(Dispatchers.IO) {
        val calls = mutableListOf<CallLogEntry>()
        val contentResolver: ContentResolver = context.contentResolver
        
        val cursor: Cursor? = contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            null,
            null,
            null,
            CallLog.Calls.DATE + " DESC"
        )
        
        cursor?.use {
            val idIndex = it.getColumnIndex(CallLog.Calls._ID)
            val nameIndex = it.getColumnIndex(CallLog.Calls.CACHED_NAME)
            val numberIndex = it.getColumnIndex(CallLog.Calls.NUMBER)
            val typeIndex = it.getColumnIndex(CallLog.Calls.TYPE)
            val dateIndex = it.getColumnIndex(CallLog.Calls.DATE)
            val durationIndex = it.getColumnIndex(CallLog.Calls.DURATION)
            
            var count = 0
            while (it.moveToNext() && count < limit) {
                val id = it.getString(idIndex)
                val name = it.getString(nameIndex)
                val number = it.getString(numberIndex) ?: ""
                val type = it.getInt(typeIndex)
                val date = it.getLong(dateIndex)
                val duration = it.getLong(durationIndex)
                
                val callType = when (type) {
                    CallLog.Calls.INCOMING_TYPE -> CallType.INCOMING
                    CallLog.Calls.OUTGOING_TYPE -> CallType.OUTGOING
                    CallLog.Calls.MISSED_TYPE -> CallType.MISSED
                    CallLog.Calls.VOICEMAIL_TYPE -> CallType.VOICEMAIL
                    CallLog.Calls.REJECTED_TYPE -> CallType.REJECTED
                    CallLog.Calls.BLOCKED_TYPE -> CallType.BLOCKED
                    else -> CallType.MISSED
                }
                
                calls.add(CallLogEntry(id, name, number, callType, date, duration))
                count++
            }
        }
        
        calls
    }
    
    suspend fun searchCallLog(query: String): List<CallLogEntry> = withContext(Dispatchers.IO) {
        val calls = mutableListOf<CallLogEntry>()
        val contentResolver: ContentResolver = context.contentResolver
        
        val selection = "${CallLog.Calls.CACHED_NAME} LIKE ? OR ${CallLog.Calls.NUMBER} LIKE ?"
        val selectionArgs = arrayOf("%$query%", "%$query%")
        
        val cursor: Cursor? = contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            null,
            selection,
            selectionArgs,
            CallLog.Calls.DATE + " DESC"
        )
        
        cursor?.use {
            val idIndex = it.getColumnIndex(CallLog.Calls._ID)
            val nameIndex = it.getColumnIndex(CallLog.Calls.CACHED_NAME)
            val numberIndex = it.getColumnIndex(CallLog.Calls.NUMBER)
            val typeIndex = it.getColumnIndex(CallLog.Calls.TYPE)
            val dateIndex = it.getColumnIndex(CallLog.Calls.DATE)
            val durationIndex = it.getColumnIndex(CallLog.Calls.DURATION)
            
            while (it.moveToNext()) {
                val id = it.getString(idIndex)
                val name = it.getString(nameIndex)
                val number = it.getString(numberIndex) ?: ""
                val type = it.getInt(typeIndex)
                val date = it.getLong(dateIndex)
                val duration = it.getLong(durationIndex)
                
                val callType = when (type) {
                    CallLog.Calls.INCOMING_TYPE -> CallType.INCOMING
                    CallLog.Calls.OUTGOING_TYPE -> CallType.OUTGOING
                    CallLog.Calls.MISSED_TYPE -> CallType.MISSED
                    CallLog.Calls.VOICEMAIL_TYPE -> CallType.VOICEMAIL
                    CallLog.Calls.REJECTED_TYPE -> CallType.REJECTED
                    CallLog.Calls.BLOCKED_TYPE -> CallType.BLOCKED
                    else -> CallType.MISSED
                }
                
                calls.add(CallLogEntry(id, name, number, callType, date, duration))
            }
        }
        
        calls
    }
}

package com.example.calltranslator.service

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class Contact(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val photoUri: String? = null
)

class ContactsService(private val context: Context) {
    
    suspend fun getAllContacts(): List<Contact> = withContext(Dispatchers.IO) {
        val contacts = mutableListOf<Contact>()
        val contentResolver: ContentResolver = context.contentResolver
        
        val cursor: Cursor? = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        
        cursor?.use {
            val idIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val photoIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)
            
            while (it.moveToNext()) {
                val id = it.getString(idIndex)
                val name = it.getString(nameIndex) ?: "Unknown"
                val number = it.getString(numberIndex) ?: ""
                val photoUri = if (photoIndex >= 0) it.getString(photoIndex) else null
                
                contacts.add(Contact(id, name, number, photoUri))
            }
        }
        
        contacts
    }
    
    suspend fun searchContacts(query: String): List<Contact> = withContext(Dispatchers.IO) {
        val contacts = mutableListOf<Contact>()
        val contentResolver: ContentResolver = context.contentResolver
        
        val selection = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ? OR " +
                       "${ContactsContract.CommonDataKinds.Phone.NUMBER} LIKE ?"
        val selectionArgs = arrayOf("%$query%", "%$query%")
        
        val cursor: Cursor? = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            selection,
            selectionArgs,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        
        cursor?.use {
            val idIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val photoIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)
            
            while (it.moveToNext()) {
                val id = it.getString(idIndex)
                val name = it.getString(nameIndex) ?: "Unknown"
                val number = it.getString(numberIndex) ?: ""
                val photoUri = if (photoIndex >= 0) it.getString(photoIndex) else null
                
                contacts.add(Contact(id, name, number, photoUri))
            }
        }
        
        contacts
    }
}

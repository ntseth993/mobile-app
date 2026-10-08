package com.example.calltranslator.service

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.telecom.TelecomManager
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat

data class SimCard(
    val subscriptionId: Int,
    val displayName: String,
    val carrierName: String,
    val number: String?,
    val slotIndex: Int
)

class TelecomCallManager(private val context: Context) {

    private val telecomManager: TelecomManager? by lazy {
        context.getSystemService(Context.TELECOM_SERVICE) as? TelecomManager
    }

    private val subscriptionManager: SubscriptionManager? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as? SubscriptionManager
        } else null
    }

    private val phoneAccountHandle by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val componentName = ComponentName(context, MyConnectionService::class.java)
            android.telecom.PhoneAccountHandle(componentName, "CallTranslatorAccount")
        } else null
    }

    fun getAvailableSims(): List<SimCard> {
        val sims = mutableListOf<SimCard>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            subscriptionManager?.activeSubscriptionInfoList?.forEach { subInfo ->
                sims.add(
                    SimCard(
                        subscriptionId = subInfo.subscriptionId,
                        displayName = subInfo.displayName?.toString() ?: "SIM ${subInfo.simSlotIndex + 1}",
                        carrierName = subInfo.carrierName?.toString() ?: "Unknown",
                        number = subInfo.number,
                        slotIndex = subInfo.simSlotIndex
                    )
                )
            }
        }

        return sims
    }

    fun hasCallPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun isDefaultDialer(): Boolean {
        val telecom = telecomManager ?: return false
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            telecom.defaultDialerPackage == context.packageName
        } else {
            false
        }
    }

    fun requestDefaultDialerRole(): Intent? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER).apply {
                putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, context.packageName)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            intent
        } else {
            // Fallback for older Android versions
            val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER).apply {
                putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, context.packageName)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            intent
        }
    }

    fun placeCall(phoneNumber: String, simCard: SimCard? = null): Boolean {
        if (!hasCallPermission()) {
            return false
        }

        return try {
            if (isUSSDCode(phoneNumber)) {
                placeUSSD(phoneNumber, simCard)
            } else if (isDefaultDialer()) {
                placeCallViaTelecom(phoneNumber)
            } else {
                placeCallViaIntent(phoneNumber)
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun isUSSDCode(number: String): Boolean {
        return number.startsWith("*") && number.endsWith("#")
    }

    private fun placeUSSD(ussdCode: String, simCard: SimCard? = null): Boolean {
        return try {
            val uri = Uri.parse("tel:$ussdCode")
            val intent = Intent(Intent.ACTION_CALL, uri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 && simCard != null) {
                    putExtra("com.android.phone.extra.slot", simCard.slotIndex)
                }
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun placeCallViaTelecom(phoneNumber: String): Boolean {
        return try {
            val telecom = telecomManager ?: return false
            val uri = Uri.parse("tel:$phoneNumber")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val handle = phoneAccountHandle ?: return false
                val extras = android.os.Bundle()
                telecom.placeCall(uri, extras)
                true
            } else {
                placeCallViaIntent(phoneNumber)
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun placeCallViaIntent(phoneNumber: String): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }
}

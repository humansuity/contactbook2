package net.gas.gascontact.business

import android.app.Notification
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.annotation.RequiresApi

class NLService : NotificationListenerService() {

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
    }

    override fun onCreate() {
        super.onCreate()
        Log.e("NLService", "NLService active")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onListenerConnected() {
        super.onListenerConnected()

        Log.e("NLService", "NLSerive active")
        checkOngoingNotification()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        Log.e("NLService", "NLSerive active")
        if (sbn?.isOngoing!!) {
            checkOngoingNotification()
            return
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkOngoingNotification() {
        val activeNotifications = activeNotifications

        Log.e("NLService", "Active notifications size: ${activeNotifications.size}")

        for (item in activeNotifications) {
            val mNotification = item.notification
            val title = mNotification.extras.getCharSequence(Notification.EXTRA_TITLE)
            val text = mNotification.extras.getCharSequence(Notification.EXTRA_TEXT)

            if (!title.isNullOrBlank() && !text.isNullOrEmpty()) {
                Log.e("NLService", "Title: $title")
                Log.e("NLService", "Text: $text")

                if (item.packageName == "android"
                    && (title.toString().contains("ГазКонтакт")
                            || text.toString().contains("Tap for details on battery and data usage"))) {
                    val snoozLong = 60000L * 60L *24L *20L
                    this.snoozeNotification(item.key, snoozLong)
                    Log.e("NLService", "Snoozed notification: $title")
                }
            }
        }
    }

}
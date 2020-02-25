package net.gas.gascontact.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.example.contactbook.R

class AlarmNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val birthdayNotification =
            BirthdayNotification(context!!)
        birthdayNotification.createNotificationChannel(
            NotificationManagerCompat.IMPORTANCE_DEFAULT,
            context.getString(R.string.app_name),
            "App notification channel"
        )

        birthdayNotification.createSampleDataNotification()
    }
}
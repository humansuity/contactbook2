package net.gas.contactbook.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.contactbook.R
import net.gas.contactbook.ui.activities.MainListActivity

class BirthdayNotification(private val context: Context) {

    fun createNotificationChannel(importance: Int, name: String, description: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "${context.packageName}-$name"
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    fun createSampleDataNotification() {
        val preferences = context.getSharedPreferences(Var.APP_PREFERENCES, Context.MODE_PRIVATE)
        val text =
            if (preferences.contains(Var.APP_DATABASE_UPDATE_TIME))
                preferences.getString(Var.APP_DATABASE_UPDATE_TIME, "Не определена")!!
            else "Не определена"
        val channelId = "${context.packageName}-${context.getString(R.string.app_name)}"
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .apply {
                setSmallIcon(R.drawable.ic_gift_30)
                setContentTitle("День рождения!")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    setContentText("Нажмите для просмотра")
                setStyle(NotificationCompat.BigTextStyle().bigText(text))
                priority = NotificationCompat.PRIORITY_DEFAULT
                setAutoCancel(true)
                setDefaults(Notification.DEFAULT_LIGHTS and Notification.DEFAULT_SOUND)

                val intent = Intent(context, MainListActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
                setContentIntent(pendingIntent)
            }
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(1001, notificationBuilder.build())
    }

}


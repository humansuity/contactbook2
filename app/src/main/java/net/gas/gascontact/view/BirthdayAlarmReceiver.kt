package net.gas.gascontact.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build


class BirthdayAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationService = Intent(context, BirthdayNotificationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            context.startForegroundService(notificationService)
        else
            context.startService(notificationService)
        AlarmHelper.setupNotificationAlarmForNextDay(context)
    }
}

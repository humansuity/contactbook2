package net.gas.gascontact.business

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import net.gas.gascontact.ui.AlarmHelper


class BirthdayAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationService = Intent(context, BirthdayNotificationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            context.startForegroundService(notificationService)
        else
            context.startService(notificationService)

        AlarmHelper.setupNotificationAlarm(context)
    }
}

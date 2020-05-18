package net.gas.gascontact.business

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi


class BirthdayAlarmReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        /** FOR ANDROID >= 8.0 **/
        context.startForegroundService(Intent(context, BirthdayNotificationService::class.java))
    }
}

package net.gas.gascontact.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.intent.action.BOOT_COMPLETED") {
            if (context != null) {
                AlarmHelper.setupInitialNotificationAlarm(context)
            }
        }
    }

}
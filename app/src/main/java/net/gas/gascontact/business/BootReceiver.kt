package net.gas.gascontact.business

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import net.gas.gascontact.ui.AlarmHelper

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.intent.action.BOOT_COMPLETED") {
            if (context != null) {
                AlarmHelper.setupInitialNotificationAlarm(context)
            }
        }
    }

}
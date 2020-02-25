package net.gas.gascontact.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val intent = Intent(context, NotificationService::class.java)
        context?.startService(intent)
        Toast.makeText(context, "Booting Completed", Toast.LENGTH_LONG).show()
    }

}
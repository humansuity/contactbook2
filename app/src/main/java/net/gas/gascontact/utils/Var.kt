package net.gas.gascontact.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.gas.gascontact.business.BirthdayAlarmReceiver
import net.gas.gascontact.business.BirthdayNotificationService
import net.gas.gascontact.business.viewmodel.BranchListViewModel
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*

object Var {

    const val URL_TO_DATABASE = "http://contactbook.oblgaz/contacts.db"
    const val PENDING_INTENT_TYPE = "EVERY_DAY_AT_8_AM"
    const val PENDING_INTENT_ACTION = "ACTION"
    const val KEY_ID = "id"
    val DATABASE_NAME = URL_TO_DATABASE.split("/").last()
    const val STORAGE_PERMISSION_CODE = 1000
    const val UNIT_FRAGMENT_ID = 1
    const val DEPARTMENT_FRAGMENT_ID = 2

    const val NOTIFICATION_SERVICE_ID = 5

    const val APP_PREFERENCES = "appsettings"
    const val APP_DATABASE_SIZE = "dbsize"
    const val APP_DATABASE_UPDATE_TIME = "dbUpdateTime"
    const val APP_DATABASE_UPDATE_DATE = "dbUpdateDate"
    const val APP_NOTIFICATION_ALARM_STATE = "NOTIFICATION_ALARM_STATE"
    const val APP_NOTIFICATION_ALARM_INITIAL = "NOTIFICATION_ALARM_INITIAL_STATE"
    const val APP_NOTIFICATION_ALARM_TIME = "NOTIFICATION_ALARM_TIME"


    fun stringMD5(key: String) : String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(key.toByteArray()))
            .toString(16).padStart(32, '0')
    }

    private fun checkIfDatabaseFileExists(context: Context) = File(context.filesDir.path + "/" + DATABASE_NAME).exists()

    private fun checkIfRoomDatabaseExists(context: Context) = context.getDatabasePath(Var.DATABASE_NAME).exists()

    fun checkIfDatabaseValid(context: Context, viewModel: BranchListViewModel) : Boolean {
        return when {
            checkIfRoomDatabaseExists(context) -> {
                Log.e("Database", "Room db exists")
                viewModel.deleteDownloadedDatabase()
                true
            }
            checkIfDatabaseFileExists(context) -> {
                Log.e("Database", "Db file exists. Trying to open it by Room")
                viewModel.updateDatabase()
                true
            }
            else -> {
                Log.e("Database", "Can't open any database")
                false
            }
        }
    }

    fun checkDownloadedFile(context: Context) = checkIfDatabaseFileExists(context)


    fun setNotificationAlarm(context: Context) {
        val preferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        Log.e("Alarm", "Set repeating alarm")
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val repeatingTime = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            NOTIFICATION_SERVICE_ID,
            Intent(context, BirthdayAlarmReceiver::class.java),
            0
        )

        alarmManager?.setRepeating(
            AlarmManager.RTC_WAKEUP,
            repeatingTime.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )

        preferences.edit().putBoolean(APP_NOTIFICATION_ALARM_STATE, true).apply()
    }

    fun setNotificationAlarm(context: Context, hour: Int, minute: Int) {
        val preferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        Log.e("Alarm", "Set repeating alarm")
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val repeatingTime = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            NOTIFICATION_SERVICE_ID,
            Intent(context, BirthdayAlarmReceiver::class.java),
            0
        )

        alarmManager?.setRepeating(
            AlarmManager.RTC_WAKEUP,
            repeatingTime.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )

        preferences.edit().putBoolean(APP_NOTIFICATION_ALARM_STATE, true).apply()
    }

}
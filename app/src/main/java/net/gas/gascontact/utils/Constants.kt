package net.gas.gascontact.utils

import android.content.Context
import android.util.Log
import net.gas.gascontact.view.viewmodel.BranchListViewModel
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

object Constants {

    const val URL_TO_DATABASE = "http://contactbook.oblgaz/contacts.db"
    val DATABASE_NAME = URL_TO_DATABASE.split("/").last()
    const val STORAGE_PERMISSION_CODE = 1000
    const val NOTIFICATION_INTENT_ID = 5

    const val FOREGROUND_NOTIFICATION_SERVICE_CHANNEL = "foreground_notification_channel"
    const val FOREGROUND_NOTIFICATION_NAME = "Служба управления уведомлениями"
    const val BIRTHDAY_NOTIFICATION_SERVICE_CHANNEL = "birthday_notification-channel"
    const val BIRTHDAY_NOTIFICATION_NAME = "Дни рождения"

    const val APP_PREFERENCES = "app-settings"
    const val APP_DATABASE_SIZE = "db-size"
    const val APP_DATABASE_UPDATE_TIME = "db-update-time"

    const val APP_NOTIFICATION_ALARM_STATE = "notification-alarm-state"
    const val APP_NOTIFICATION_ALARM_INIT_STATE = "notification-alarm-init-state"

    const val WEEKDAY_NOTIFICATION_SCHEDULE_TIME = "notification-schedule-time-weekday"
    const val HOLIDAY_NOTIFICATION_SCHEDULE_TIME = "notification-schedule-time-holiday"


    fun stringMD5(key: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(key.toByteArray()))
            .toString(16).padStart(32, '0')
    }

    private fun checkIfDatabaseFileExists(context: Context) =
        File(context.filesDir.path + "/" + DATABASE_NAME).exists()

    private fun checkIfRoomDatabaseExists(context: Context) =
        context.getDatabasePath(Constants.DATABASE_NAME).exists()

    fun checkIfDatabaseValid(context: Context, viewModel: BranchListViewModel): Boolean {
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


    fun hideSpinnerOnOrientationChanged(viewModel: BranchListViewModel, orientation: Int) {
        if (viewModel.screenOrientation != orientation) {
            viewModel.screenOrientation = orientation
            viewModel.spinnerState.value = false
        }
    }


}
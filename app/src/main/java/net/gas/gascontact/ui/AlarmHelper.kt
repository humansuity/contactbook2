package net.gas.gascontact.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import net.gas.gascontact.business.BirthdayAlarmReceiver
import net.gas.gascontact.utils.Var
import org.joda.time.LocalDate
import java.util.*

class AlarmHelper {

    companion object {

        fun setupNotificationAlarm(context: Context) {
            val storedStringTime = getStoredNotificationTime(context)
            val hour = getScheduleHour(storedStringTime)
            val minute = getScheduleMinute(storedStringTime)
            setDisposableNotificationAlarm(context, hour, minute)
        }

        private fun getStoredNotificationTime(context: Context): String? {
            val preferences = context.getSharedPreferences(Var.APP_PREFERENCES, Context.MODE_PRIVATE)
            return if (LocalDate().dayOfWeek > 5)
                preferences.getString(Var.WEEKDAY_NOTIFICATION_SCHEDULE_TIME, "8:0")
            else
                preferences.getString(Var.WEEKDAY_NOTIFICATION_SCHEDULE_TIME, "10:0")
        }

        private fun setDisposableNotificationAlarm(context: Context, hour: Int, minute: Int) {
            Log.e("Alarm", "Set repeating alarm at $hour:$minute, day of week - ${getNextDayOfWeek()}")
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            val repeatingTime = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.DAY_OF_WEEK, getNextDayOfWeek())
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                Var.NOTIFICATION_INTENT_ID,
                Intent(context, BirthdayAlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            alarmManager?.setExact(
                AlarmManager.RTC_WAKEUP,
                repeatingTime.timeInMillis,
                pendingIntent
            )
        }


        private fun setupScheduleCalendar(hour: Int, minute: Int) = run {
            Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.DAY_OF_WEEK, getNextDayOfWeek())
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
            }
        }

        private fun getNextDayOfWeek() : Int {
            return if (LocalDate().dayOfWeek != 7) LocalDate().dayOfWeek + 1
            else 1
        }

        private fun getScheduleHour(storedStringTime: String?)
                = storedStringTime?.split(":")?.first()?.toInt() ?: 0

        private fun getScheduleMinute(storedStringTime: String?)
                = storedStringTime?.split(":")?.first()?.toInt() ?: 0

    }
}
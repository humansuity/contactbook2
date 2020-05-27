package net.gas.gascontact.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.edit
import net.gas.gascontact.business.BirthdayAlarmReceiver
import net.gas.gascontact.utils.Var
import org.joda.time.*

class AlarmHelper {

    companion object {

        const val WEEKDAYS = 1
        const val HOLIDAYS = 2

        fun setupNotificationAlarmForNextDay(context: Context) {
            val storedStringTime = getStoredNotificationTime(context)
            val hour = getScheduleHour(storedStringTime)
            val minute = getScheduleMinute(storedStringTime)

            val executeTime = if(getNextDay() == 1)
                DateTime(DateTimeZone.forID("Europe/Minsk"))
                    .withMonthOfYear(getNextMonth())
                    .withDayOfMonth(getNextDay())
                    .withHourOfDay(hour)
                    .withMinuteOfHour(minute)
            else
                DateTime(DateTimeZone.forID("Europe/Minsk"))
                    .withDayOfMonth(getNextDay())
                    .withHourOfDay(hour)
                    .withMinuteOfHour(minute)

            setAlarm(context, executeTime.millis)
            Log.e("Alarm", "Set repeating alarm at " +
                    "[$hour:$minute], date [${executeTime.toDate()}], day of week [${getNextDayOfWeek()}]")
        }


        fun setupInitialNotificationAlarm(context: Context) {
            val storedStringTime = getStoredNotificationTime(context)
            val hour = getScheduleHour(storedStringTime)
            val minute = getScheduleMinute(storedStringTime)

            val executeTime = DateTime(DateTimeZone.forID("Europe/Minsk"))
                .withDayOfMonth(DateTime(DateTimeZone.forID("Europe/Minsk")).dayOfMonth)
                .withHourOfDay(hour)
                .withMinuteOfHour(minute)

            setAlarm(context, executeTime.millis)
            Log.e("Alarm", "Set initial alarm at " +
                    "[$hour:$minute], date [${executeTime.toDate()}], day of week [${DateTime().dayOfWeek}]")
        }


        private fun setAlarm(context: Context, repeatingTime: Long) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                Var.NOTIFICATION_INTENT_ID,
                Intent(context, BirthdayAlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            alarmManager?.setExact(
                AlarmManager.RTC_WAKEUP,
                repeatingTime,
                pendingIntent
            )
        }


        fun cancelNotificationAlarm(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                Var.NOTIFICATION_INTENT_ID,
                Intent(context, BirthdayAlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            alarmManager?.cancel(pendingIntent)
        }


        private fun getStoredNotificationTime(context: Context): String? {
            val preferences = context.getSharedPreferences(Var.APP_PREFERENCES, Context.MODE_PRIVATE)
            val dayOfWeek = LocalDate(DateTimeZone.forID("Europe/Minsk")).dayOfWeek
            return if (dayOfWeek !in 5..6)
                preferences.getString(Var.WEEKDAY_NOTIFICATION_SCHEDULE_TIME, "8:0")
            else
                preferences.getString(Var.HOLIDAY_NOTIFICATION_SCHEDULE_TIME, "10:0")
        }


        fun setupNewNotificationScheduleTime(context: Context, stringScheduleTime: String, flag: Int) {
            val preferences = context.getSharedPreferences(Var.APP_PREFERENCES, Context.MODE_PRIVATE)
            val hour = getScheduleHour(stringScheduleTime)
            val minute = getScheduleMinute(stringScheduleTime)
            preferences.edit {
                if (flag == WEEKDAYS)
                    putString(Var.WEEKDAY_NOTIFICATION_SCHEDULE_TIME, "$hour:$minute")
                else
                    putString(Var.HOLIDAY_NOTIFICATION_SCHEDULE_TIME, "$hour:$minute")
            }
        }

        fun setupNotificationState(context: Context, state: Boolean) {
            val preferences = context.getSharedPreferences(Var.APP_PREFERENCES, Context.MODE_PRIVATE)
            preferences.edit {
                putBoolean(Var.APP_NOTIFICATION_ALARM_STATE, state)
            }
        }

        fun getNotificationState(context: Context): Boolean {
            val preferences = context.getSharedPreferences(Var.APP_PREFERENCES, Context.MODE_PRIVATE)
            return preferences.getBoolean(Var.APP_NOTIFICATION_ALARM_STATE, false)
        }

        fun setupInitNotificationState(context: Context, state: Boolean) {
            val preferences = context.getSharedPreferences(Var.APP_PREFERENCES, Context.MODE_PRIVATE)
            preferences.edit {
                putBoolean(Var.APP_NOTIFICATION_ALARM_INIT_STATE, state)
            }
        }


        private fun setupRepeatingTimeForHolidays(hour: Int, minute: Int): Long {
            return if(getNextDay() == 1)
                DateTime(DateTimeZone.forID("Europe/Minsk"))
                    .withMonthOfYear(getNextMonth())
                    .withDayOfMonth(getNextDay())
                    .withHourOfDay(hour)
                    .withMinuteOfHour(minute)
                    .millis
            else
                DateTime(DateTimeZone.forID("Europe/Minsk"))
                    .withDayOfMonth(getNextDay())
                    .withHourOfDay(hour)
                    .withMinuteOfHour(minute)
                    .millis
        }




        fun getInitNotificationState(context: Context): Boolean {
            val preferences = context.getSharedPreferences(Var.APP_PREFERENCES, Context.MODE_PRIVATE)
            return preferences.getBoolean(Var.APP_NOTIFICATION_ALARM_INIT_STATE, false)
        }


        private fun getNextDayOfWeek() = LocalDate(DateTimeZone.forID("Europe/Minsk")).plusDays(1).dayOfWeek

        private fun getNextDay() = LocalDate(DateTimeZone.forID("Europe/Minsk")).plusDays(1).dayOfMonth

        private fun getNextMonth() = LocalDate(DateTimeZone.forID("Europe/Minsk")).plusMonths(1).monthOfYear

        private fun getScheduleHour(storedStringTime: String?)
                = storedStringTime?.split(":")?.first()?.toInt() ?: 0

        private fun getScheduleMinute(storedStringTime: String?)
                = storedStringTime?.split(":")?.last()?.toInt() ?: 0

    }
}
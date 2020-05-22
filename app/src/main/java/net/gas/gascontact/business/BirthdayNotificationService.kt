package net.gas.gascontact.business

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import net.gas.gascontact.business.database.entities.Persons
import net.gas.gascontact.business.database.entities.Posts
import net.gas.gascontact.business.database.entities.Units
import net.gas.gascontact.business.model.DataModel
import net.gas.gascontact.ui.NotificationHelper
import net.gas.gascontact.utils.Var
import org.joda.time.DateTime
import java.util.*

class BirthdayNotificationService : LifecycleService() {

    private var mDataModel: DataModel? = null
    private var unitList = listOf<Units>()
    private var postList = listOf<Posts>()

    private var preferences: SharedPreferences? = null


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("Service", "Service active")

        startServiceOnForeground()
        preferences = applicationContext.getSharedPreferences(Var.APP_PREFERENCES, Context.MODE_PRIVATE)
        if (applicationContext.getDatabasePath(Var.DATABASE_NAME).exists()) {
            mDataModel = DataModel(applicationContext)
            liveData { emitSource(mDataModel!!.getUpcomingPersonWithBirthday("TODAY")) }
                .observe(this, Observer { personList ->
                    if (!personList.isNullOrEmpty()) {
                        var unitIds = emptyArray<Int>()
                        var postIds = emptyArray<Int>()
                        var unitEntry = false
                        var postEntry = false
                        personList.forEach { item ->
                            item.unitID?.let { unitIds += it }
                            item.postID?.let { postIds += it }
                        }

                        liveData { emitSource(mDataModel!!.getUnitEntitiesByIds(unitIds)) }
                            .observe(this, Observer {
                                unitEntry = true
                                unitList = it
                                if (unitEntry && postEntry)
                                    createBirthdayNotification(personList, unitList, postList)
                            })

                        liveData { emitSource(mDataModel!!.getPostEntitiesByIds(postIds)) }
                            .observe(this, Observer {
                                postEntry = true
                                postList = it
                                if (unitEntry && postEntry)
                                    createBirthdayNotification(personList, unitList, postList)
                            })
                    }
                })
        } else {
            startServiceOnForeground()
            setupAfterEnterNotificationAlarm()
            stopForeground(true)
            stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }


    private fun startServiceOnForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel("my_service", "My background service", "Background service for notifications")
            startForeground(
                1,
                Notification.Builder(applicationContext, "my_service").build())
        } else {
            startForeground(
                1,
                NotificationCompat.Builder(applicationContext, "").build())
        }
    }

    private fun createNotificationChannel(id: String, name: String, description: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH).apply {
                this.description = description
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                    .createNotificationChannel(this)
            }
        }
    }


    private fun setupAfterEnterNotificationAlarm() {
        val currentHour = DateTime().hourOfDay
        val currentMinute = DateTime().minuteOfHour
        if (currentMinute >= 55)
            setExactNotificationAlarm(currentHour + 1, 5)
        else
            setExactNotificationAlarm(currentHour, currentMinute + 5)

    }



    private fun createBirthdayNotification(persons: List<Persons>, units: List<Units>, posts: List<Posts>) {
        val notificationHelper = NotificationHelper(applicationContext, persons, units, posts)
        notificationHelper.createNotificationChannel("default_channel", "default", "some_channel")
        notificationHelper.createNotification()
    }


    override fun onDestroy() {
        Log.e("Service", "Service was destroyed")
        super.onDestroy()
    }

    private fun setExactNotificationAlarm(hour: Int, minute: Int) {
        val alarmManager =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val repeatingTime = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            Var.NOTIFICATION_SERVICE_ID + 1,
            Intent(this, BirthdayAlarmReceiver::class.java),
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        alarmManager?.setExact(
            AlarmManager.RTC_WAKEUP,
            repeatingTime.timeInMillis,
            pendingIntent
        )

        Log.e("Alarm manager", "Set alarm manager at: ${hour}:${minute}")
    }
}

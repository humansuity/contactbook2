package net.gas.gascontact.business

import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import net.gas.gascontact.R
import net.gas.gascontact.business.database.entities.Persons
import net.gas.gascontact.business.database.entities.Posts
import net.gas.gascontact.business.database.entities.Units
import net.gas.gascontact.business.model.DataModel
import net.gas.gascontact.ui.NotificationHelper
import net.gas.gascontact.ui.activities.MainListActivity
import net.gas.gascontact.utils.Var
import java.util.*

class BirthdayNotificationService : LifecycleService() {

    private var mDataModel: DataModel? = null
    private var unitList = listOf<Units>()
    private var postList = listOf<Posts>()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("Service", "Service active")
        startForeground(1, Notification.Builder(applicationContext, "default_channel").build())

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
            stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createBirthdayNotification(persons: List<Persons>, units: List<Units>, posts: List<Posts>) {
        val notificationHelper = NotificationHelper(applicationContext, persons, units, posts)
        notificationHelper.createNotificationChannel("default_channel", "default", "some_channel")
        notificationHelper.createNotification()
    }


    override fun onDestroy() {
        Log.e("Service", "Service was destroyed")
        super.onDestroy()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setNotificationAlarm(hour: Int) {
        if (!applicationContext.getSharedPreferences(Var.APP_PREFERENCES, Context.MODE_PRIVATE)
                .getBoolean(Var.APP_NOTIFICATION_ALARM_STATE, false)) {
            Log.e("Alarm", "Set repeating alarm")
            val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            val repeatingTime = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, hour)
            }
            val pendingIntent = PendingIntent.getService(
                applicationContext,
                System.currentTimeMillis().toInt(),
                Intent(this, BirthdayNotificationService::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            alarmManager?.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                repeatingTime.timeInMillis,
                pendingIntent
            )

            applicationContext.getSharedPreferences(Var.APP_PREFERENCES, Context.MODE_PRIVATE)
                .edit().putBoolean(Var.APP_NOTIFICATION_ALARM_STATE, true).apply()
        }
    }
}

package net.gas.gascontact.business

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import net.gas.gascontact.business.database.entities.Persons
import net.gas.gascontact.business.database.entities.Posts
import net.gas.gascontact.business.database.entities.Units
import net.gas.gascontact.business.model.DataModel
import net.gas.gascontact.ui.AlarmHelper
import net.gas.gascontact.ui.NotificationHelper
import net.gas.gascontact.utils.Var

class BirthdayNotificationService : LifecycleService() {

    private var mDataModel: DataModel? = null
    private var unitList = listOf<Units>()
    private var postList = listOf<Posts>()

    private var preferences: SharedPreferences? = null


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("Service", "Notification service is active")

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
                                if (unitEntry && postEntry) {
                                    createBirthdayNotification(personList, unitList, postList)
                                }
                            })

                        liveData { emitSource(mDataModel!!.getPostEntitiesByIds(postIds)) }
                            .observe(this, Observer {
                                postEntry = true
                                postList = it
                                if (unitEntry && postEntry) {
                                    createBirthdayNotification(personList, unitList, postList)
                                }
                            })
                    }
                })
        } else {
            Log.e("Service", "Notification service was closed. Database file doesn't exist")
            stopForeground(true)
            stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }


    override fun onDestroy() {
        Log.e("Service", "Service was destroyed")
        super.onDestroy()
    }


    private fun startServiceOnForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForeground(
                2,
                Notification.Builder(applicationContext, Var.FOREGROUND_NOTIFICATION_SERVICE_CHANNEL).build())
        else
            startForeground(
                2,
                NotificationCompat.Builder(applicationContext, "").build())
    }


    private fun createBirthdayNotification(persons: List<Persons>, units: List<Units>, posts: List<Posts>) {
        val notificationHelper = NotificationHelper(applicationContext, persons, units, posts)
        notificationHelper.createNotification()
        stopForeground(true)
        stopSelf()
    }

}

package net.gas.gascontact.business

import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import net.gas.gascontact.business.database.entities.Persons
import net.gas.gascontact.business.database.entities.Posts
import net.gas.gascontact.business.database.entities.Units
import net.gas.gascontact.business.model.DataModel
import net.gas.gascontact.ui.NotificationHelper
import net.gas.gascontact.utils.Var

class BirthdayNotificationService : LifecycleService() {

    private var mDataModel: DataModel? = null
    private var unitList = listOf<Units>()
    private var postList = listOf<Posts>()

    override fun onBind(intent: Intent): IBinder? {
        return super.onBind(intent)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("Service", "Service active")
        Toast.makeText(applicationContext, "Service active", Toast.LENGTH_LONG).show()

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
        } else stopSelf()
        return super.onStartCommand(intent, flags, startId)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createBirthdayNotification(persons: List<Persons>, units: List<Units>, posts: List<Posts>) {
        val notificationHelper = NotificationHelper(applicationContext, persons, units, posts)
        notificationHelper.createNotificationChannel("default_channel", "default", "some_channel")
        notificationHelper.createNotification()
        stopSelf()
    }


    override fun onDestroy() {
        Log.e("Service", "Service was destroyed")
        super.onDestroy()
    }
}

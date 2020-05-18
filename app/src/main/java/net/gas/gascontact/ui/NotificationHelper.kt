package net.gas.gascontact.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import net.gas.gascontact.R
import net.gas.gascontact.business.database.entities.Persons
import net.gas.gascontact.business.database.entities.Posts
import net.gas.gascontact.business.database.entities.Units
import net.gas.gascontact.ui.activities.MainListActivity
import org.joda.time.LocalDate
import org.joda.time.Years

class NotificationHelper(private val context: Context, private val personList: List<Persons>,
                         private val unitList: List<Units>, private val postList: List<Posts>) {

    private val notificationManager
            = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
    private var NOTIFICATION_ID = 100
    private val NOTIFICATION_GROUP_KEY = "unique_group_key"
    private var channelId = ""
    private var yearSampleOne = "лет"
    private var yearSampleTwo = "год"
    private var yearSampleThree = "года"

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(id: String, name: String, description: String) {
        val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH).apply {
            this.description = description
            enableLights(true)
            lightColor = Color.RED
            enableVibration(true)
            vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            notificationManager?.createNotificationChannel(this)
        }
        channelId = id
    }

    fun createNotification() {
        val summaryNotification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("День рождения")
            .setContentText("Upcoming birthdays")
            .setSmallIcon(R.drawable.ic_gift_30)
            .setGroup(NOTIFICATION_GROUP_KEY)
            .setGroupSummary(true)
            .build()
        notificationManager?.notify(1, summaryNotification)

        personList.forEach {
            val intentToMainActivity = Intent(context, MainListActivity::class.java)
            intentToMainActivity.putExtra("PERSON_ID", it.id)
            val pendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID++,
               intentToMainActivity, PendingIntent.FLAG_UPDATE_CURRENT)

            val notification = NotificationCompat.Builder(context, channelId)
                .setContentTitle("${it.lastName} ${it.firstName} ${it.patronymic}, ${getAge(getRawAge(it.birthday))}")
                .setSmallIcon(R.drawable.ic_gift_30)
                .setStyle(NotificationCompat.InboxStyle()
                    .addLine("${getPost(it.postID)}")
                    .addLine("${getUnit(it.unitID)}"))
                .setContentIntent(pendingIntent)
                .setGroup(NOTIFICATION_GROUP_KEY)
                .build()
            notificationManager?.notify(NOTIFICATION_ID++, notification)
        }
    }


    private fun getPost(postID: Int?) = postList.find { it.id == postID }?.name

    private fun getUnit(unitID: Int?) = unitList.find { it.id == unitID }?.name

    private fun getRawAge(birthday: String?) : String {
        val birthdaySplit = birthday?.split("-")
        return if (birthdaySplit?.size == 3) {
            Years.yearsBetween(
                LocalDate(
                    birthdaySplit[0].toInt(),
                    birthdaySplit[1].toInt(),
                    birthdaySplit[2].toInt()
                ),
                LocalDate()
            ).years.toString()
        } else {
            "Возраст не определён"
        }
    }

    private fun getAge(rawAge: String) =
        when (rawAge.toInt() % 10) {
            1 -> "$rawAge $yearSampleTwo"
            in 2..4 -> "$rawAge $yearSampleThree"
            0, in 5..9 -> "$rawAge $yearSampleOne"
            else -> "Возраст не определён"
        }

}
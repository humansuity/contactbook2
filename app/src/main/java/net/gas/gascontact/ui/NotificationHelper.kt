package net.gas.gascontact.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import net.gas.gascontact.R
import net.gas.gascontact.business.database.entities.Persons
import net.gas.gascontact.business.database.entities.Posts
import net.gas.gascontact.business.database.entities.Units
import net.gas.gascontact.ui.activities.MainListActivity
import net.gas.gascontact.utils.ORGANIZATIONUNITLIST
import net.gas.gascontact.utils.Var
import org.joda.time.LocalDate
import org.joda.time.Years

class NotificationHelper(private val context: Context, private val personList: List<Persons>,
                         private val unitList: List<Units>, private val postList: List<Posts>) {

    private val notificationManager
            = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
    private var NOTIFICATION_ID = 100
    private val NOTIFICATION_GROUP_KEY = "unique_group_key"
    private var yearSampleOne = "лет"
    private var yearSampleTwo = "год"
    private var yearSampleThree = "года"

    fun createNotificationChannel(id: String, name: String, description: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH).apply {
                this.description = description
                enableLights(true)
                lightColor = Color.WHITE
                enableVibration(true)
                vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                notificationManager?.createNotificationChannel(this)
            }
        }
    }


    fun createNotification() {
        val realm = context.getSharedPreferences(Var.APP_PREFERENCES, Context.MODE_PRIVATE).getString("REALM", "")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val summaryNotification = NotificationCompat.Builder(context, Var.BIRTHDAY_NOTIFICATION_SERVICE_CHANNEL)
                .setChannelId(Var.BIRTHDAY_NOTIFICATION_SERVICE_CHANNEL)
                .setContentTitle("День рождения")
                .setContentText(ORGANIZATIONUNITLIST.find { realm == it.code }?.name)
                .setSmallIcon(R.drawable.ic_gift_30)
                .setGroup(NOTIFICATION_GROUP_KEY)
                .setAutoCancel(true)
                .setGroupSummary(true)
                .build()
            notificationManager?.notify(1, summaryNotification)

            personList.forEach {
                val intentToMainActivity = Intent(context, MainListActivity::class.java)
                intentToMainActivity.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intentToMainActivity.putExtra("PERSON_ID", it.id)
                val pendingIntent = PendingIntent.getActivity(
                    context, NOTIFICATION_ID++,
                    intentToMainActivity, PendingIntent.FLAG_UPDATE_CURRENT
                )

                val notification = NotificationCompat.Builder(context, Var.BIRTHDAY_NOTIFICATION_SERVICE_CHANNEL)
                    .setChannelId(Var.BIRTHDAY_NOTIFICATION_SERVICE_CHANNEL)
                    .setContentTitle(
                        "${it.lastName} ${it.firstName} ${it.patronymic}, ${getAge(
                            getRawAge(it.birthday)
                        )}"
                    )
                    .setSmallIcon(R.drawable.ic_gift_30)
                    .setStyle(
                        NotificationCompat.InboxStyle()
                            .addLine("${getPost(it.postID)}")
                            .addLine("${getUnit(it.unitID)}")
                    )
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setGroup(NOTIFICATION_GROUP_KEY)
                    .build()
                notificationManager?.notify(NOTIFICATION_ID++, notification)
            }
        } else {
            personList.forEach {
                val intentToMainActivity = Intent(context, MainListActivity::class.java)
                intentToMainActivity.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intentToMainActivity.putExtra("PERSON_ID", it.id)
                val pendingIntent = PendingIntent.getActivity(
                    context, NOTIFICATION_ID++,
                    intentToMainActivity, PendingIntent.FLAG_UPDATE_CURRENT
                )

                val notification = NotificationCompat.Builder(context, Var.BIRTHDAY_NOTIFICATION_SERVICE_CHANNEL)
                    .setContentTitle(
                        "${it.lastName} " +
                        "${it.firstName} " +
                        "${it.patronymic}, " +
                         getAge(getRawAge(it.birthday))
                    )
                    .setSmallIcon(R.drawable.ic_gift_30)
                    .setStyle(
                        NotificationCompat.InboxStyle()
                            .addLine("${getPost(it.postID)}")
                            .addLine("${getUnit(it.unitID)}")
                    )
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build()
                notificationManager?.notify(NOTIFICATION_ID++, notification)
            }
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
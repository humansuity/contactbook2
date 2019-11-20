package net.gas.contactbook.ui.download

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.contactbook.R
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class DataBaseDownloadTask(private val context: Context) : AsyncTask<Void, Void, Void>() {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private var notificationBuilder: NotificationCompat.Builder? = null
    private val notificationID = 1


    override fun onPreExecute() {
        super.onPreExecute()
        //showing initial notification
        notificationBuilder = initNotificationBuilder()
        showNotification("Downloading database...", true)
    }


    override fun doInBackground(vararg params: Void?): Void? {

        val url = URL("http://contactbook.oblgaz/contacts.db")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connect()

        val path = File(context.filesDir.toURI())
        val fullPath = File(path, url.path.split("/").last())
        val fileOutputStream = FileOutputStream(fullPath)
        val inputStream = connection.inputStream
        val buffer = ByteArray(1024)


        while(true) {
            val len = inputStream.read(buffer)

            if (len == -1) {
                break
            } else {
                fileOutputStream.write(buffer, 0, len)
            }
        }

        return null


    }

    
    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        //showing final notification
        showNotification("Database downloaded!", false)
    }


    private fun showNotification(notificationText: String, downloadFlag: Boolean) {
        notificationBuilder!!.setContentTitle(notificationText).setProgress(0, 0, downloadFlag)
        notificationManager.notify(notificationID, notificationBuilder?.build())
    }


    private fun initNotificationBuilder() : NotificationCompat.Builder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelID = "DOWNLOAD_CHANNEL_ID"
            val notificationChannel = NotificationChannel(channelID, "Download channel", NotificationManager.IMPORTANCE_LOW)
            notificationChannel.apply {
                description = "Channel description"
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(notificationChannel)
            NotificationCompat.Builder(context, channelID)
                .setSmallIcon(R.drawable.database_download_ic)
                .setProgress(0, 0, true)
        } else {
            NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.database_download_ic)
                .setProgress(0, 0, true)
        }
    }


}
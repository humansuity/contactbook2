package net.gas.contactbook.ui.download

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import com.example.contactbook.R
import com.google.android.material.snackbar.Snackbar
import java.io.*
import java.lang.ref.WeakReference
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.URL
import kotlin.Exception

class DataBaseDownloadTask(private val context: Context, private val rootView: View) : AsyncTask<Void, Void, Void>() {

    private val mContextRef = WeakReference<Context>(context)
    private var notificationBuilder: NotificationCompat.Builder? = null
    private val notificationID = 1
    private var isConnected = true
    private val notificationManager = mContextRef.get()?.
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


    override fun onPreExecute() {
        super.onPreExecute()
        notificationBuilder = initNotificationBuilder()
        showNotification("Downloading database...", true)
    }


    override fun doInBackground(vararg params: Void?): Void? {
        try {
            val url = URL("http://contactbook.oblgaz/qcontacts.db")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.readTimeout = 10*1000     //set 10 seconds to get response
            connection.connect()
            downloadViaHttpConnection(connection, url)
        } catch (e: ConnectException) {
            isConnected = false
            return null
        }
        return null
    }

    
    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        val context = mContextRef.get()

        if (isConnected) {
            Snackbar.make(rootView, "Загрузка базы данных завершена", Snackbar.LENGTH_LONG).show()
            showNotification("Database downloaded!", false)
        } else {
            Snackbar.make(rootView, "Не удалось скачать базу данных", Snackbar.LENGTH_LONG).show()
            showNotification("Unable to download database", false)
        }
    }


    private fun showNotification(notificationText: String, downloadFlag: Boolean) {
        notificationBuilder!!.setContentTitle(notificationText).setProgress(0, 0, downloadFlag)
        notificationManager.notify(notificationID, notificationBuilder?.build())
    }


    private fun initNotificationBuilder() : NotificationCompat.Builder {
        val context = mContextRef.get()
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
            NotificationCompat.Builder(context!!, channelID)
                .setSmallIcon(R.drawable.database_download_ic)
                .setProgress(0, 0, true)
        } else {
            NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.database_download_ic)
                .setProgress(0, 0, true)
        }
    }


    private fun downloadViaHttpConnection(connection: HttpURLConnection, url: URL) {
        var inputStream: InputStream? = null
        var fileOutputStream: FileOutputStream? = null
        try {
            val buffer = ByteArray(1024)
            val path = File(context.filesDir.toURI())
            val fullPath = File(path, url.path.split("/").last())
            fileOutputStream = FileOutputStream(fullPath)
            inputStream = connection.inputStream

            while(true) {
                val len = inputStream.read(buffer)
                if (len == -1) break
                else fileOutputStream.write(buffer, 0, len)
            }
        } catch (e: Exception) {
            isConnected = false
            fileOutputStream?.close()
            inputStream?.close()
        }
    }


}
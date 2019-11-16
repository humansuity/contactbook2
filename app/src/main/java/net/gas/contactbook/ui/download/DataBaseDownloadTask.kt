package net.gas.contactbook.ui.download

import android.content.Context
import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class DataBaseDownloadTask(private val context: Context) : AsyncTask<Void, Void, Void>() {

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        Toast.makeText(context, "It's ok", Toast.LENGTH_SHORT).show()
    }

    override fun doInBackground(vararg params: Void?): Void? {

        val url = URL("http://contactbook.oblgaz/contacts.db")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connect()
        Log.d("connection", context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString())


        val path = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!.toURI())
        val fullPath = File(path, "base.db")
        val fos = FileOutputStream(fullPath)
        val inputStream = connection.inputStream
        val buffer = ByteArray(1024)


        while(true) {
            var len = inputStream.read(buffer)

            if (len == -1) {
                break
            } else {
                fos.write(buffer, 0, len)
            }
        }

        return null

    }

}
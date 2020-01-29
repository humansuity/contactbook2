package net.gas.contactbook.ui.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.contactbook.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_download.*
import net.gas.contactbook.business.network.DatabaseDownloadTask
import net.gas.contactbook.utils.Var
import java.io.File


class DownloadActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)

        downloadBtn.setOnClickListener {
            if (checkInternetConnection()) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED) { requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    Var.STORAGE_PERMISSION_CODE)
                } else {
                    startDownloading()
                }
            } else { Snackbar.make(constraintLayout,
                        "Проверьте подключение к интернету",
                        Snackbar.LENGTH_LONG).show() }
        }

        catalogBtn.setOnClickListener{
            val pathToDownloadedDatabase = application.filesDir.path + "/" + Var.DATABASE_NAME
            val pathToRoomDatabase = application.getDatabasePath(Var.DATABASE_NAME)
            if (File(pathToDownloadedDatabase).exists() || pathToRoomDatabase.exists()) {
                val unitActivity = Intent(this, UnitsListActivity::class.java)
                startActivity(unitActivity)
            } else {
                Snackbar.make(constraintLayout,
                    "База данных отсутствует на устройстве",
                    Snackbar.LENGTH_LONG).show()
            }
        }
    }


    private fun checkInternetConnection() : Boolean {
        val connectManager = applicationContext.
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectManager.activeNetworkInfo
        return activeNetworkInfo?.isConnected ?: false
    }


    private fun startDownloading() {
        val pathToRoomDatabase = application.getDatabasePath(Var.DATABASE_NAME)
        if (pathToRoomDatabase.exists()) {
            Snackbar.make(constraintLayout,
                "База данных уже установлена",
                Snackbar.LENGTH_LONG).show()
        } else {
            val dbManager = DatabaseDownloadTask(application, constraintLayout)
            dbManager.execute()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray)
    {
        when (requestCode) {
            Var.STORAGE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    startDownloading()
                }
                else { Toast.makeText(this, "Permission denied!",
                    Toast.LENGTH_LONG).show()
                }
            }
        }
    }

}
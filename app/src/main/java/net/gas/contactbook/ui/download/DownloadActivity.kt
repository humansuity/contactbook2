package net.gas.contactbook.ui.download

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.contactbook.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_download.*


class DownloadActivity : AppCompatActivity() {

    private val STORAGE_PERMISSION_CODE: Int = 1000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)


        downloadBtn.setOnClickListener {
            if (checkInternetConnection()) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
                } else { startDownloading() }
            } else { Snackbar.make(constraintLayout,
                        "Проверьте подключение к интернету",
                        Snackbar.LENGTH_LONG).show() }
        }
    }


    private fun checkInternetConnection() : Boolean {
        val connectManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectManager.activeNetworkInfo
        return activeNetworkInfo?.isConnected ?: false
    }


    private fun startDownloading() {

        val dbManager = DataBaseDownloadTask(this)
        dbManager.execute()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            STORAGE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    startDownloading()
                } else {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_LONG).show()
                }
            }
        }


    }
}
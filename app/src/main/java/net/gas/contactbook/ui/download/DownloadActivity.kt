package net.gas.contactbook.ui.download

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.contactbook.R
import kotlinx.android.synthetic.main.activity_download.*


class DownloadActivity : AppCompatActivity() {

    private val STORAGE_PERMISSION_CODE: Int = 1000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)


        downloadBtn.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED
                ) {
                    requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        STORAGE_PERMISSION_CODE
                    )
                } else {
                    startDownloading()
                }
            } else {
                startDownloading()
            }
        }
    }

<<<<<<< HEAD
    //Выкачивает базу через HttpURLConnection
=======

>>>>>>> 50cbfe8c9804d2706770d1d8928a8b41919f3b6f
    private fun startDownloading() {

        val dbmanager = DataBaseDownloadTask(this)
        dbmanager.execute()

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
package net.gas.contactbook.ui.download

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.contactbook.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_download.*
import net.gas.contactbook.ui.database.ContactbookDatabase
import net.gas.contactbook.ui.database.daos.UnitsDao
import net.gas.contactbook.ui.database.entities.Units
import java.io.File


class DownloadActivity : AppCompatActivity() {

    private val STORAGE_PERMISSION_CODE: Int = 1000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)

        Toast.makeText(applicationContext, applicationContext.getDatabasePath("contacts.db").toString(), Toast.LENGTH_LONG).show()


        downloadBtn.setOnClickListener {
            if (checkInternetConnection()) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
                } else { startDownloading() }
            } else { Snackbar.make(constraintLayout,
                        "Проверьте подключение к интернету",
                        Snackbar.LENGTH_LONG).show() }
        }


        catalogBtn.setOnClickListener {
            if (ContactbookDatabase.getInstance(applicationContext) != null) ContactbookDatabase.destroyInstance()
            val path = File(applicationContext.getDatabasePath("contacts.db").toURI())
            val unitsData = ContactbookDatabase.getInstance(applicationContext)?.unitsDao()
            if (path.exists()) {
                Toast.makeText(applicationContext, "Path found", Toast.LENGTH_LONG).show()
                val list = getListOfUnits(unitsData)
                Toast.makeText(applicationContext, "Name of unit is: " + list[0].name, Toast.LENGTH_LONG).show()
            } else { Toast.makeText(applicationContext,
                "База данных не обнаружена", Toast.LENGTH_LONG).show()
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
        val dbManager = DataBaseDownloadTask(application, constraintLayout)
        dbManager.execute()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray)
    {
        when (requestCode) {
            STORAGE_PERMISSION_CODE -> {
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


    fun getListOfUnits(unitsData: UnitsDao?) : List<Units> {
        var unitsList = listOf<Units>()
        if (unitsData != null) {
            unitsList = unitsData.getEntities()
        } else {
            Toast.makeText(applicationContext, "Database not created yet",
                Toast.LENGTH_LONG).show()
        }
        return unitsList
    }


}
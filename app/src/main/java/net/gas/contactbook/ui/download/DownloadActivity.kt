package net.gas.contactbook.ui.download

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.contactbook.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_download.*
import net.gas.contactbook.ui.UnitsListActivity
import net.gas.contactbook.ui.database.ContactbookDatabase
import net.gas.contactbook.ui.database.daos.UnitsDao
import net.gas.contactbook.ui.database.entities.Units


class DownloadActivity : AppCompatActivity() {

    private val STORAGE_PERMISSION_CODE: Int = 1000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)

        Toast.makeText(applicationContext, applicationContext.getDatabasePath("qcontacts.db").toString(), Toast.LENGTH_LONG).show()


        downloadBtn.setOnClickListener {
            if (checkInternetConnection()) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED) {
                    requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        STORAGE_PERMISSION_CODE)
                } else { startDownloading() }
            } else { Snackbar.make(constraintLayout,
                        "Проверьте подключение к интернету",
                        Snackbar.LENGTH_LONG).show() }
        }


        catalogBtn.setOnClickListener {
            val unitsData = ContactbookDatabase.getInstance(applicationContext)?.unitsDao()
            getListOfUnits(unitsData)
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


    fun getListOfUnits(unitsData: UnitsDao?) {
        val tmp = UnitsListTask(this).execute(unitsData)
    }


    private fun openUnitsListActivity(units: List<Units>?) {
        val unitsNameList = arrayListOf<String?>()
        units?.let {
            for(unit in it) {
                unitsNameList.add(unit.name)
            }
        }
        val intent = Intent(applicationContext, UnitsListActivity::class.java)
        intent.putExtra("ARRAY", unitsNameList)
        startActivity(intent)
    }

    companion object {
        class UnitsListTask(private val downloadActivity: DownloadActivity) : AsyncTask<UnitsDao?, Void, List<Units>>() {


            override fun doInBackground(vararg params: UnitsDao?): List<Units> {
                return params[0]!!.getEntities()
            }


            override fun onPostExecute(result: List<Units>?) {
                super.onPostExecute(result)
                //Toast.makeText(downloadActivity.applicationContext, result!![0].name, Toast.LENGTH_LONG).show()
                downloadActivity.openUnitsListActivity(result)
            }
        }
    }


}
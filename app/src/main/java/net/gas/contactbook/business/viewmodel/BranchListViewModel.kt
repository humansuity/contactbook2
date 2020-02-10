package net.gas.contactbook.business.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.*
import kotlinx.coroutines.*
import net.gas.contactbook.business.database.entities.*
import net.gas.contactbook.business.model.DataModel
import net.gas.contactbook.utils.Var
import java.io.File
import java.io.FileOutputStream
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.URL


class BranchListViewModel(application: Application)
    : AndroidViewModel(application) {

    val context: Context = application.applicationContext
    private val dataModel = DataModel(context)
    var unitList: LiveData<List<Units>> = MutableLiveData<List<Units>>()
    var departmentList: LiveData<List<Departments>> = MutableLiveData<List<Departments>>()
    var personList: LiveData<List<Persons>> = MutableLiveData<List<Persons>>()
    var personEntity: LiveData<Persons> = MutableLiveData<Persons>()
    var photoEntity: LiveData<Photos> = MutableLiveData<Photos>()
    var postEntity: LiveData<Posts> = MutableLiveData<Posts>()
    var spinnerState: MutableLiveData<Boolean> = MutableLiveData()
    var floatingButtonState: MutableLiveData<Boolean> = MutableLiveData()
    var downloadSpinnerState: MutableLiveData<Boolean> = MutableLiveData()

    var unitFragmentCallback: (() -> Unit)? = null
    var initUnitFragmentCallback: (() -> Unit)? = null
    var departmentFragmentCallback: (() -> Unit) ? = null
    var personFragmentCallBack: (() -> Unit)? = null
    var callIntentCallback: ((Intent, Int) -> Unit)? = null
    var addContactIntentCallBack: ((Intent) -> Unit)? = null
    var checkPermissionsCallBack: (() -> Unit)? = null
    var onNetworkErrorCallback: ((String) -> Unit)? = null
    var optionMenuStateCallback: ((Boolean) -> Unit)? = null
    var isUnitFragmentActive = false
    var isDatabaseDownloaded = true
    private var unitId = 0

    fun onBranchItemClick(id: Int, listType: String) {
        when (listType) {
            Units::class.java.name -> {
                spinnerState.value = true
                viewModelScope.launch(Dispatchers.Default) {
                    departmentList = liveData(Dispatchers.IO) {
                        emitSource(dataModel.getDepartmentEntitiesById(id))
                        spinnerState.postValue(false)
                    }
                }
                unitId = id
                unitFragmentCallback?.invoke()
            }
            Departments::class.java.name -> {
                spinnerState.value = true
                viewModelScope.launch(Dispatchers.Default) {
                    personList = liveData(Dispatchers.IO) {
                        emitSource(dataModel.getPersonsEntitiesByIds(unitId, id))
                        spinnerState.postValue(false)
                    }
                }
                departmentFragmentCallback?.invoke()
            }
        }
        deleteDownloadedDatabase(context)
    }


    fun downloadDatabase() {
        checkPermissionsCallBack?.invoke()
    }

    fun startDownloading() {
        downloadSpinnerState.value = true
        viewModelScope.launch(Dispatchers.Default) {
            downloadDb()
            viewModelScope.launch(Dispatchers.Main) {
                if (isDatabaseDownloaded)
                    initUnitFragmentCallback?.invoke()
                else if (!isDatabaseDownloaded && checkOpenableDatabase())
                    initUnitFragmentCallback?.invoke()
            }
        }
    }

    private fun downloadDb() {
        var connection: HttpURLConnection? = null
        try {
            val url = URL(Var.URL_TO_DATABASE)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.readTimeout = 10*1000     //set 10 seconds to get response
            connection.connect()
            downloadViaHttpConnection(connection, url)
        } catch (e: ConnectException) {
            viewModelScope.launch(Dispatchers.Main) { onNetworkErrorCallback?.invoke("DOWNLOAD_ERROR") }
            isDatabaseDownloaded = false
        } finally {
            connection?.disconnect()
        }
    }

    private fun downloadViaHttpConnection(connection: HttpURLConnection, url: URL) {
        var fileOutputStream: FileOutputStream? = null
        try {
            val buffer = ByteArray(1024)
            val pathToSaveFile = File(context.filesDir.toURI())
            val fullPath = File(pathToSaveFile, url.path.split("/").last())
            fileOutputStream = FileOutputStream(fullPath)
            while(true) {
                val len = connection.inputStream.read(buffer)
                if (len != -1) fileOutputStream.write(buffer, 0, len)
                else break
            }
        } catch (e: Exception) {
            viewModelScope.launch(Dispatchers.Main) { onNetworkErrorCallback?.invoke("DOWNLOAD_ERROR") }
            isDatabaseDownloaded = false
        } finally {
            fileOutputStream?.close()
        }
    }


    fun checkOpenableDatabase() : Boolean {
        val pathToDownloadedDatabase = context.filesDir.path + "/" + Var.DATABASE_NAME
        return when {
            File(pathToDownloadedDatabase).exists() -> isValidDatabase()
            isValidDatabase() -> true
            else -> false
        }
    }

    private fun isValidDatabase() : Boolean {
        val pathToRoomDatabase = context.getDatabasePath(Var.DATABASE_NAME)
        return if (pathToRoomDatabase.exists())
            pathToRoomDatabase.length() / (1024.0 * 1024.0) > 20.0 //check if database size greater than 20 Mb
        else false
    }

    fun onPhoneNumberClick(phoneNumber: String) {
        val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
        callIntentCallback?.invoke(callIntent, phoneNumber.length)
    }

    fun addNewContact(intent: Intent) {
        addContactIntentCallBack?.invoke(intent)
    }

    fun setupUnitList() {
        spinnerState.value = true
        unitList = liveData(Dispatchers.IO) {
            emitSource(dataModel.getUnitEntities())
            spinnerState.postValue(false)
        }
    }

    fun getPersonListByTag(sequence: String): LiveData<List<Persons>> {
        return liveData { emitSource(dataModel.getPersonListByTag(sequence)) }
    }

    fun onPersonItemClick(id: Int) {
        personEntity = dataModel.getPersonEntityById(id)
        personFragmentCallBack?.invoke()
    }

    fun setupPhotoEntity(id: Int?) {
        photoEntity = liveData { emitSource(dataModel.getPhotoEntityById(id!!)) }
    }

    fun setupPostEntity(id: Int?) {
        postEntity = liveData { emitSource(dataModel.getPostsEntityById(id!!)) }
    }

    fun getDepartmentEntity(id: Int) : LiveData<Departments>
            = liveData { emitSource(dataModel.getDepartmentEntityById(id)) }

    fun getUnitEntity(id: Int) : LiveData<Units>
            = liveData { emitSource(dataModel.getUnitEntityById(id)) }

    private fun deleteDownloadedDatabase(context: Context) {
        val pathToDownloadedDatabase = context.filesDir.path + "/" + Var.DATABASE_NAME
        if (File(pathToDownloadedDatabase).exists()) {
            File(pathToDownloadedDatabase).delete()
        }
    }
}
package net.gas.gascontact.business.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.gas.gascontact.business.database.entities.*
import net.gas.gascontact.business.model.DataModel
import net.gas.gascontact.utils.Var
import java.io.File
import java.io.FileOutputStream
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class BranchListViewModel(application: Application)
    : AndroidViewModel(application) {

    val context: Context = application.applicationContext
    private val dataModel = DataModel(context)

    var unitList: LiveData<List<Units>> = MutableLiveData<List<Units>>()
    var departmentList: LiveData<List<Departments>> = MutableLiveData<List<Departments>>()
    var personList: LiveData<List<Persons>> = MutableLiveData<List<Persons>>()
    var birthdayPersonList: LiveData<List<Persons>> = MutableLiveData<List<Persons>>()

    var personEntity: LiveData<Persons> = MutableLiveData<Persons>()
    var photoEntity: LiveData<Photos> = MutableLiveData<Photos>()
    var postEntity: LiveData<Posts> = MutableLiveData<Posts>()
    var unitEntity: LiveData<Units> = MutableLiveData<Units>()
    var departmentEntity: LiveData<Departments> = MutableLiveData<Departments>()
    var spinnerState: MutableLiveData<Boolean> = MutableLiveData()
    var floatingButtonState: MutableLiveData<Boolean> = MutableLiveData()
    var downloadSpinnerState: MutableLiveData<Boolean> = MutableLiveData()
    var dbDownloadingProgressState: MutableLiveData<Int> = MutableLiveData()

    var unitFragmentCallback: (() -> Unit)? = null
    var initUnitFragmentCallback: (() -> Unit)? = null
    var departmentFragmentCallback: (() -> Unit) ? = null
    var personFragmentCallBack: (() -> Unit)? = null
    var callIntentCallback: ((Intent) -> Unit)? = null
    var addContactIntentCallBack: ((Intent) -> Unit)? = null
    var checkPermissionsCallBack: (() -> Unit)? = null
    var onNetworkErrorCallback: ((String) -> Unit)? = null
    var optionMenuStateCallback: ((String) -> Unit)? = null
    var appToolbarStateCallback: ((String, Boolean) -> Unit)? = null
    var onReceiveDatabaseSizeCallBack: ((Long) -> Unit)? = null
    var onDatabaseUpdated: ((Boolean) -> Unit)? = null
    var isUnitFragmentActive = false
    var sharedDatabaseSize: Long = 0
    var databaseUpdateTime: String = ""
    private var currentDatabaseSize: Long = 0
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
        deleteDownloadedDatabase()
    }


    fun downloadDatabase() {
        checkPermissionsCallBack?.invoke()
    }

    fun startDownloadingDB() {
        downloadSpinnerState.value = true
        viewModelScope.launch(Dispatchers.Default) {
            downloadDb("")
            viewModelScope.launch(Dispatchers.Main) {
                downloadSpinnerState.value = false
                if (checkOpenableDatabase()) {
                    putUpdateDatabaseDateToConfig()
                    updateDatabase()
                    initUnitFragmentCallback?.invoke()
                }
            }
        }
    }

    private fun putUpdateDatabaseDateToConfig() {
        val dateFormatter = SimpleDateFormat(
            "dd.MM.yyyy",
            Locale.forLanguageTag("en")
        )
        val currentDate = dateFormatter.format(Date())
        val editor = context.getSharedPreferences(Var.APP_PREFERENCES, Context.MODE_PRIVATE).edit()
        editor.putString(Var.APP_DATABASE_UPDATE_DATE, currentDate)
        editor.apply()
    }


    fun startUpdatingDB() {
        downloadSpinnerState.value = true
        viewModelScope.launch(Dispatchers.Default) {
            downloadDb("UPDATING")
            viewModelScope.launch(Dispatchers.Main) {
                downloadSpinnerState.value = false
                if (isDownloadedFileValid(context.filesDir.path + "/" + Var.DATABASE_NAME)) {
                    deleteOldRoomFile()
                    updateDatabase()
                    isUnitFragmentActive = false
                    onDatabaseUpdated?.invoke(true)
                } else {
                    deleteDownloadedDatabase()
                    onDatabaseUpdated?.invoke(false)
                }
            }
        }
    }

    private fun deleteOldRoomFile() {
        val pathToRoomDatabase = context.getDatabasePath(Var.DATABASE_NAME)
        val last = pathToRoomDatabase.toString().split("/").last()
        val resultPath = pathToRoomDatabase.toString().removeSuffix("/$last")
        File(resultPath).deleteRecursively()
    }

    private fun downloadDb(flag: String) {
        var connection: HttpURLConnection? = null
        try {
            val url = URL(Var.URL_TO_DATABASE)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.readTimeout = 10 * 1000     //set 10 seconds to get response
            connection.connect()
            currentDatabaseSize = connection.contentLength.toLong()
            if (currentDatabaseSize > 0 && currentDatabaseSize != 125L) {
                onReceiveDatabaseSizeCallBack?.invoke(currentDatabaseSize)
            }
            downloadViaHttpConnection(connection, url, flag)
        } catch (e: ConnectException) {
            viewModelScope.launch(Dispatchers.Main) {
                if (flag == "UPDATING") {
                    onNetworkErrorCallback?.invoke("UPDATING_ERROR")
                } else onNetworkErrorCallback?.invoke("DOWNLOAD_ERROR")
            }
        } finally {
            connection?.disconnect()
        }
    }

    private fun downloadViaHttpConnection(connection: HttpURLConnection, url: URL, flag: String) {
        var fileOutputStream: FileOutputStream? = null
        try {
            val buffer = ByteArray(1024)
            val pathToSaveFile = File(context.filesDir.toURI())
            val fullPath = File(pathToSaveFile, url.path.split("/").last())
            fileOutputStream = FileOutputStream(fullPath)
            while(true) {
                val len = connection.inputStream.read(buffer)
                if (len != -1) {
                    fileOutputStream.write(buffer, 0, len)
                    val currentFileSize = fullPath.length().toFloat()
                    val currentDatabaseSize = currentDatabaseSize.toFloat()
                    val percent = currentFileSize.div(currentDatabaseSize).times(100)
                    dbDownloadingProgressState.postValue(percent.toInt())
                    Log.e("i", "$percent")
                }
                else break
            }
        } catch (e: Exception) {
            viewModelScope.launch(Dispatchers.Main) {
                if (flag == "UPDATING") {
                    onNetworkErrorCallback?.invoke("UPDATING_ERROR")
                } else onNetworkErrorCallback?.invoke("DOWNLOAD_ERROR")
            }
        } finally {
            fileOutputStream?.close()
        }
    }


    private fun isDownloadedFileValid(path: String) : Boolean {
        return when {
            sharedDatabaseSize == 0L -> {
                return when {
                    currentDatabaseSize > 0 -> File(path).length() == currentDatabaseSize
                    else -> false
                }
            }
            sharedDatabaseSize > 0 -> File(path).length() == sharedDatabaseSize
            else -> false
        }
    }


    fun makePhoneCall(intent: Intent) {
        callIntentCallback?.invoke(intent)
    }


    fun checkOpenableDatabase() : Boolean {
        val pathToDownloadedDatabase = context.filesDir.path + "/" + Var.DATABASE_NAME
        return when {
            File(pathToDownloadedDatabase).exists() -> isValidFile(pathToDownloadedDatabase)
            isValidDatabase() -> true
            else -> false
        }
    }

    private fun isValidFile(path: String) : Boolean {
        return when {
            isValidDatabase() -> true
            sharedDatabaseSize == 0L -> {
                return when {
                    currentDatabaseSize > 0 -> File(path).length() == currentDatabaseSize
                    else -> false
                }
            }
            sharedDatabaseSize > 0 -> File(path).length() == sharedDatabaseSize
            else -> false
        }
    }

    private fun isValidDatabase() : Boolean {
        val pathToRoomDatabase = context.getDatabasePath(Var.DATABASE_NAME)
        return when {
            pathToRoomDatabase.exists() -> {
                return when {
                    sharedDatabaseSize == 0L -> {
                        return when {
                            currentDatabaseSize > 0 -> pathToRoomDatabase.length() == currentDatabaseSize
                            else -> false
                        }
                    }
                    sharedDatabaseSize > 0 -> pathToRoomDatabase.length() == sharedDatabaseSize
                    else -> false
                }
            }
            else -> false
        }
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
        spinnerState.value = true
        personEntity = liveData { emitSource(dataModel.getPersonEntityById(id)) }
        personFragmentCallBack?.invoke()
    }

    fun setupPhotoEntity(id: Int?) {
        photoEntity = liveData { emitSource(dataModel.getPhotoEntityById(id!!)) }
    }

    fun setupPostEntity(id: Int?) {
        postEntity = liveData { emitSource(dataModel.getPostsEntityById(id!!)) }
    }

    fun setupDepartmentEntity(id: Int?) {
        departmentEntity = liveData { emitSource(dataModel.getDepartmentEntityById(id!!)) }
    }

    fun setupUnitEntity(id: Int?) {
        unitEntity = liveData { emitSource(dataModel.getUnitEntityById(id!!)) }
    }

    fun setupPersonListByBirth() {
        birthdayPersonList = liveData { emitSource(dataModel.getPersonEntitiesByUpcomingBirthday()) }
    }

    fun getCurrentBirthdayList() : LiveData<List<Persons>>
            = liveData { emitSource(dataModel.getPersonEntitiesByBirthday()) }

    fun getDepartmentEntity(id: Int) : LiveData<Departments>
            = liveData { emitSource(dataModel.getDepartmentEntityById(id)) }

    fun getUnitEntity(id: Int) : LiveData<Units>
            = liveData { emitSource(dataModel.getUnitEntityById(id)) }

    private fun deleteDownloadedDatabase() {
        val pathToDownloadedDatabase = context.filesDir.path + "/" + Var.DATABASE_NAME
        if (File(pathToDownloadedDatabase).exists()) {
            File(pathToDownloadedDatabase).delete()
        }
    }

    private fun updateDatabase() {
        dataModel.updateDatabase()
    }

}
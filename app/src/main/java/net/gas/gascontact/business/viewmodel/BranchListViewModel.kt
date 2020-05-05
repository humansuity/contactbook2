package net.gas.gascontact.business.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.graphics.PointF
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.gas.gascontact.business.database.entities.*
import net.gas.gascontact.business.model.DataModel
import net.gas.gascontact.model.TokenResponse
import net.gas.gascontact.network.api.KeycloackRetrofitFactory
import net.gas.gascontact.network.api.KeycloackRetrofitService
import net.gas.gascontact.network.api.MiriadaApiRetrofitFactory
import net.gas.gascontact.utils.ORGANIZATIONUNITLIST
import net.gas.gascontact.utils.Var
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
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
    val dataModel = DataModel(context)

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
    var dbDownloadingProgressState: MutableLiveData<PointF> = MutableLiveData()
    var userLoginState: MutableLiveData<String> = MutableLiveData()

    var unitFragmentCallback: (() -> Unit)? = null
    var initUnitFragmentCallback: (() -> Unit)? = null
    var departmentFragmentCallback: (() -> Unit) ? = null
    var personFragmentCallBack: (() -> Unit)? = null
    var callIntentCallback: ((Intent) -> Unit)? = null
    var sendEmailIntentCallback: ((Intent) -> Unit)? = null
    var addContactIntentCallBack: ((Intent) -> Unit)? = null
    var checkPermissionsCallBack: (() -> Unit)? = null
    var onNetworkErrorCallback: ((String) -> Unit)? = null
    var optionMenuStateCallback: ((String) -> Unit)? = null
    var appToolbarStateCallback: ((String, Boolean) -> Unit)? = null
    var onReceiveDatabaseSizeCallBack: ((Long) -> Unit)? = null
    var onDatabaseUpdated: ((Boolean) -> Unit)? = null
    var onLoginCallback: (() -> Unit)? = null
    var afterSuccessLoginCallback: (() -> Unit)? = null
    var onUnitFragmentBackPressed: (() -> Unit)? = null
    var onCreateUnitListFragment: (() -> Unit)? = null
    var isUnitFragmentActive = false
    var isPersonFragmentActive = false
    var sharedDatabaseSize: Long = 0
    lateinit var databaseUpdateTime: String
    private var currentDatabaseSize: Long = 0
    var unitId = 0
    private var i = 0

    fun onUnitItemClick(id: Int) {
        spinnerState.value = true
        unitId = id
        viewModelScope.launch(Dispatchers.Default) {
            launch(Dispatchers.Main) { initUnitFragmentCallback?.invoke() }
            unitList = withContext(Dispatchers.IO) { dataModel.getSecondaryEntities(id) }
        }
        deleteDownloadedDatabase()
    }

    fun getPostByPersonId(id: Int) : LiveData<Posts>
            = liveData { emitSource(dataModel.getPostEntityById(id)) }

    fun onDepartmentItemClick(id: Int) {
        spinnerState.value = true
        Log.e("!!!!!!!!!!!!!!!!!", "!! " +  unitId.toString() + " " + id.toString() )
        viewModelScope.launch(Dispatchers.Default) {
            personList = liveData(Dispatchers.IO) {
                emitSource(dataModel.getPersonsEntitiesByIds(unitId, id))
            }
        }
        departmentFragmentCallback?.invoke()
    }


    fun downloadDatabase() {
        checkPermissionsCallBack?.invoke()
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


    fun putRealmToConfig(realm: String) {
        val editor = context.getSharedPreferences(Var.APP_PREFERENCES, Context.MODE_PRIVATE).edit()
        editor.putString("REALM", realm)
        editor.apply()
    }

    private fun deleteOldRoomFile() {
        val pathToRoomDatabase = context.getDatabasePath(Var.DATABASE_NAME)
        val last = pathToRoomDatabase.toString().split("/").last()
        val resultPath = pathToRoomDatabase.toString().removeSuffix("/$last")
        File(resultPath).deleteRecursively()
        Log.e("Room file", "Old database was deleted")
    }

    private fun downloadDb(flag: String) {
        var connection: HttpURLConnection? = null
        try {
            val url = URL(Var.URL_TO_DATABASE)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.readTimeout = 10 * 1000     //set 10 seconds to get response
            connection.connect()
            Log.e("response", "${connection.responseCode}")
            if (connection.responseCode == 410) {
                viewModelScope.launch(Dispatchers.Main) {
                    onNetworkErrorCallback?.invoke("ACCESS_DENIED_ERROR")
                }
            } else {
                Log.e("response", "565665655464564")
                downloadSpinnerState.postValue(true)
                currentDatabaseSize = connection.contentLength.toLong()
                if (currentDatabaseSize > 0 && currentDatabaseSize != 125L) {  //dont know why size may be equal 125L
                    onReceiveDatabaseSizeCallBack?.invoke(currentDatabaseSize)
                }
                downloadViaHttpConnection(connection, url, flag)
            }
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
                    dbDownloadingProgressState.postValue(PointF(percent, percent))
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

    fun sendEmail(intent: Intent) {
        sendEmailIntentCallback?.invoke(intent)
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

    fun setupPrimaryUnitList() {
        spinnerState.value = true
        unitList = liveData(Dispatchers.IO) {
            emitSource(dataModel.getPrimaryUnitEntities())
        }
    }

    fun getPersonListByLastNameTag(sequence: String): LiveData<List<Persons>> {
        return liveData { emitSource(dataModel.getPersonListByLastNameTag(sequence)) }
    }
    fun getPersonListByNameTag(sequence: String): LiveData<List<Persons>> {
        return liveData { emitSource(dataModel.getPersonListByNameTag(sequence)) }
    }
    fun getPersonListByPatronymicTag(sequence: String): LiveData<List<Persons>> {
        return liveData { emitSource(dataModel.getPersonListByPatronymicTag(sequence)) }
    }
    fun getPersonListByMobilePhoneTag(sequence: String): LiveData<List<Persons>> {
        return liveData { emitSource(dataModel.getPersonListByMobilePhoneTag(sequence)) }
    }


    fun onPersonItemClick(id: Int) {
        spinnerState.value = true
        personEntity = liveData { emitSource(dataModel.getPersonEntityById(id)) }
        personFragmentCallBack?.invoke()
    }


    fun setupPhotoEntity(id: Int?) {
        photoEntity = liveData { emitSource(dataModel.getPhotoEntityById(id!!)) }
    }


    fun getPhotoEntity(id: Int?) : LiveData<Photos> = liveData { emitSource(dataModel.getPhotoEntityById(id!!)) }


    fun setupPostEntity(id: Int?) {
        postEntity = liveData { emitSource(dataModel.getPostEntityById(id!!)) }
    }


    fun setupDepartmentEntity(id: Int?) {
        departmentEntity = liveData { emitSource(dataModel.getDepartmentEntityById(id!!)) }
    }


    fun setupUnitEntity(id: Int?) {
        unitEntity = liveData { emitSource(dataModel.getUnitEntityById(id!!)) }
    }


    fun setUpcomingPersonsWithBirthday(period: String) {
        birthdayPersonList = liveData { emitSource(dataModel.getUpcomingPersonWithBirthday(period)) }
    }


    fun setupSecondaryUnitList(id: Int) {
        unitList = liveData { dataModel.getSecondaryEntities(id) }
    }


    private fun deleteDownloadedDatabase() {
        val pathToDownloadedDatabase = context.filesDir.path + "/" + Var.DATABASE_NAME
        if (File(pathToDownloadedDatabase).exists()) {
            File(pathToDownloadedDatabase).delete()
        }
    }


    private fun updateDatabase() {
        dataModel.updateDatabase()
    }


    fun tryToLogin(realm: String?, username: String?, password: String?) {
        Log.e("login", "$realm, $username, $password")
        var tokens: TokenResponse
        val keycloackservice: KeycloackRetrofitService = KeycloackRetrofitFactory.makeRetrofitService()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = keycloackservice.requestGrant(realm!!, "microservicegasclient", ORGANIZATIONUNITLIST.find {it.code == realm}!!.secret, "password", username!!, password!!)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            tokens = it
                            Log.e("Controller ", "CheckValidPassword success");
                            userLoginState.value = tokens.access_token
                        }
                    } else {
                        Toast.makeText(context, "Ошибка авторизации, проверьте введённые данные", Toast.LENGTH_LONG).show()
                        userLoginState.value = ""
                    }
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Ошибка авторизации, проверьте подключение к сети организации", Toast.LENGTH_LONG).show()
                }
                Log.e("Controller", "Exception in CheckValidPassword  ${e.message}")
            } catch (e: Throwable) {
                Log.e("Controller", "Ooops: Something else went wrong")
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Ошибка авторизации, проверьте подключение к сети организации", Toast.LENGTH_LONG).show()
                }

            }
        }
    }


    private fun startDownloadingDb(response: Response<ResponseBody>) {
        viewModelScope.launch(Dispatchers.Default) {
            downloadDb("", response)
            viewModelScope.launch(Dispatchers.Main) {
                if (checkOpenableDatabase()) {
                    onCreateUnitListFragment?.invoke()
                    putUpdateDatabaseDateToConfig()
                    updateDatabase()
                    setupPrimaryUnitList()
                }
            }
        }
    }


    private fun downloadDb(flag: String, response: Response<ResponseBody>) {
        try {
            val url = URL(Var.URL_TO_DATABASE)
            if (response.code() == 410) {
                viewModelScope.launch(Dispatchers.Main) {
                    onNetworkErrorCallback?.invoke("ACCESS_DENIED_ERROR")
                }
            } else {
                Log.e("response", "565665655464564")
                downloadSpinnerState.postValue(true)
                currentDatabaseSize = response.body()!!.contentLength()
                if (currentDatabaseSize > 0 && currentDatabaseSize != 125L) {  //dont know why size may be equal 125L
                    onReceiveDatabaseSizeCallBack?.invoke(currentDatabaseSize)
                }
                downloadViaHttpConnection(response.body()!!, url, flag)
            }
        } catch (e: ConnectException) {
            viewModelScope.launch(Dispatchers.Main) {
                if (flag == "UPDATING") {
                    onNetworkErrorCallback?.invoke("UPDATING_ERROR")
                } else onNetworkErrorCallback?.invoke("DOWNLOAD_ERROR")
            }
        }
    }


    private fun downloadViaHttpConnection(body: ResponseBody, url: URL, flag: String) {
        var fileOutputStream: FileOutputStream? = null
        try {
            val buffer = ByteArray(1024)
            val pathToSaveFile = File(context.filesDir.toURI())
            val fullPath = File(pathToSaveFile, url.path.split("/").last())
            fileOutputStream = FileOutputStream(fullPath)
            while(true) {
                val len = body.byteStream().read(buffer)
                if (len != -1) {
                    fileOutputStream.write(buffer, 0, len)
                    val currentFileSize = fullPath.length().toFloat()
                    val currentDatabaseSize = currentDatabaseSize.toFloat()
                    val percent = currentFileSize.div(currentDatabaseSize).times(100)
                    dbDownloadingProgressState.postValue(PointF(percent, percent))
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


    fun startUpdatingDb(response: Response<ResponseBody>) {
        viewModelScope.launch(Dispatchers.Default) {
            downloadDb("UPDATING", response)
            viewModelScope.launch(Dispatchers.Main) {
                if (isDownloadedFileValid(context.filesDir.path + "/" + Var.DATABASE_NAME)) {
                    deleteOldRoomFile()
                    putUpdateDatabaseDateToConfig()
                    isUnitFragmentActive = false
                    onDatabaseUpdated?.invoke(true)
                    Toast.makeText(context, "Updating database successful", Toast.LENGTH_SHORT).show()
                } else {
                    deleteDownloadedDatabase()
                    onDatabaseUpdated?.invoke(false)
                    Toast.makeText(context, "Updating database invalid", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    fun makeRequestToDatabase(token: String, selectedRealm: String, downloadType: String) {
        val apiService = MiriadaApiRetrofitFactory.makeRetrofitService()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                downloadSpinnerState.postValue(true)
                val response = apiService.requestGetDownloadDB(
                    ORGANIZATIONUNITLIST.find { it.name == selectedRealm }?.code!!,
                    "refresh_token", "Bearer $token"
                )

                try {
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                // Toast.makeText(context, "Response code is ${response.code()}", Toast.LENGTH_LONG).show()
                                if (downloadType == "DOWNLOAD")
                                    startDownloadingDb(response)
                                else {
                                    startUpdatingDb(response)
                                }
                            }
                            Toast.makeText(
                                context,
                                "Success! Response code is ${response.code()}",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Log.e("Controller", "DownloadDatabase Not success")
                            Toast.makeText(
                                context,
                                "Не удаётся скачать базу данных! Ошибка ${response.code()}",
                                Toast.LENGTH_LONG
                            ).show()
                            downloadSpinnerState.value = false
                            //activity?.supportFragmentManager?.popBackStack();
                        }
                    }
                } catch (e: HttpException) {
                    Log.e("Controller", "Exception in DownloadDatabase ${e.message}")
                    Toast.makeText(context, "Ошибка авторизации, проверьте введённые данные", Toast.LENGTH_LONG)
                        .show()
                } catch (e: Throwable) {
                    Log.e("Controller", "Ooops: Something else went wrong ${e.message}")
                }
            } catch (e: KotlinNullPointerException) {

            }
        }
    }


}
package net.gas.gascontact.view.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.PointF
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.gas.gascontact.database.entities.*
import net.gas.gascontact.model.DataModel
import net.gas.gascontact.model.TokenResponse
import net.gas.gascontact.network.retrofit.KeycloackRetrofitFactory
import net.gas.gascontact.network.retrofit.KeycloackRetrofitService
import net.gas.gascontact.network.retrofit.MiriadaApiRetrofitFactory
import net.gas.gascontact.utils.Constants
import net.gas.gascontact.utils.ORGANIZATIONUNITLIST
import net.gas.gascontact.view.AlarmHelper
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.net.ConnectException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class BranchListViewModel(application: Application) : AndroidViewModel(application) {

    val context: Context = application.applicationContext
    val dataModel = DataModel(context)

    var birthdayPersonList: LiveData<List<Persons>> = MutableLiveData()

    var personEntity: LiveData<Persons> = MutableLiveData()
    var photoEntity: LiveData<Photos> = MutableLiveData()
    var postEntity: LiveData<Posts> = MutableLiveData()
    var unitEntity: LiveData<Units> = MutableLiveData()
    var departmentEntity: LiveData<Departments> = MutableLiveData()
    var spinnerState: MutableLiveData<Boolean> = MutableLiveData()
    var floatingButtonState: MutableLiveData<Boolean> = MutableLiveData()
    var downloadSpinnerState: MutableLiveData<Boolean> = MutableLiveData()
    var dbDownloadingProgressState: MutableLiveData<PointF> = MutableLiveData()
    var userLoginState: MutableLiveData<String> = MutableLiveData()

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
    var parentId = -1
    var isUnitFragmentActive = false
    var isPersonFragmentActive = false
    var sharedDatabaseSize: Long = 0
    lateinit var databaseUpdateTime: String
    private var currentDatabaseSize: Long = 0

    var screenOrientation: Int = 0
    var unitIdForUnitListFragment = 0
    var isOpenedViaIntent = false

    var onUnitItemClickedCallback: ((Int, Boolean) -> Unit)? = null
    var onDepartmentItemClickedCallback: ((Int) -> Unit)? = null
    var onPersonItemClickedCallback: ((Int) -> Unit)? = null
    var onBackButtonPressed: (() -> Unit)? = null

    var isFirstEntry = true

    fun onUnitItemClick(itemId: Int, isDepartmentItem: Boolean) {
        spinnerState.value = true
        onUnitItemClickedCallback?.invoke(itemId, isDepartmentItem)
        deleteDownloadedDatabase()
    }

    fun onDepartmentItemClick(departmentID: Int) {
        spinnerState.value = true
        onDepartmentItemClickedCallback?.invoke(departmentID)
    }

    fun onPersonItemClick(personID: Int) {
        spinnerState.value = true
        onPersonItemClickedCallback?.invoke(personID)
    }

    fun getPostByPersonId(id: Int): LiveData<Posts> =
        liveData { emitSource(dataModel.getPostEntityById(id)) }
    

    fun downloadDatabase() {
        checkPermissionsCallBack?.invoke()
    }


    private fun putUpdateDatabaseDateToConfig() {
        val dateFormatter = SimpleDateFormat(
            "dd.MM.yyyy hh:mm a",
            Locale.forLanguageTag("en")
        )
        val currentDateTime = dateFormatter.format(Date())
        context.getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE)
            .edit().putString(Constants.APP_DATABASE_UPDATE_TIME, currentDateTime)
            .apply()
    }


    fun putRealmToConfig(realm: String) {
        val editor = context.getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE).edit()
        editor.putString("REALM", realm)
        editor.apply()
    }

    private fun deleteOldRoomFile() {
        val pathToRoomDatabase = context.getDatabasePath(Constants.DATABASE_NAME)
        val last = pathToRoomDatabase.toString().split("/").last()
        val resultPath = pathToRoomDatabase.toString().removeSuffix("/$last")
        File(resultPath).deleteRecursively()
        Log.e("Room file", "Old database was deleted")
    }


    fun makePhoneCall(intent: Intent) {
        callIntentCallback?.invoke(intent)
    }

    fun sendEmail(intent: Intent) {
        sendEmailIntentCallback?.invoke(intent)
    }


    fun addNewContact(intent: Intent) {
        addContactIntentCallBack?.invoke(intent)
    }


    fun getPrimaryUnitList() = liveData(Dispatchers.IO) { emitSource(dataModel.getPrimaryUnitEntities()) }

    fun getDepartmentListByUnitID(id: Int) = liveData(Dispatchers.IO) { emitSource(dataModel.getDepartmentEntitiesById(id)) }


    fun setNotificationAlarm() {
        val preferences = context.getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE)
        if (!preferences.getBoolean(Constants.APP_NOTIFICATION_ALARM_INIT_STATE, false)) {
            AlarmHelper.setupInitialNotificationAlarm(context)
            AlarmHelper.setupInitNotificationState(context, state = true)
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





    fun setupPersonInfo(id: Int) {
        spinnerState.value = true
        personEntity = liveData { emitSource(dataModel.getPersonEntityById(id)) }
    }


    fun setupPhotoEntity(id: Int?) {
        photoEntity = liveData { emitSource(dataModel.getPhotoEntityById(id!!)) }
    }


    fun getPhotoEntity(id: Int?): LiveData<Photos> =
        liveData { emitSource(dataModel.getPhotoEntityById(id!!)) }


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
        birthdayPersonList =
            liveData { emitSource(dataModel.getUpcomingPersonWithBirthday(period)) }
    }

    fun deleteDownloadedDatabase() {
        val pathToDownloadedDatabase = context.filesDir.path + "/" + Constants.DATABASE_NAME
        if (File(pathToDownloadedDatabase).exists()) {
            File(pathToDownloadedDatabase).delete()
        }
    }


    fun updateDatabase() {
        dataModel.updateDatabase()
    }


    fun tryToLogin(realm: String?, username: String?, password: String?) {
        Log.e("login", "$realm, $username, $password")
        var tokens: TokenResponse
        val keycloackservice: KeycloackRetrofitService =
            KeycloackRetrofitFactory.makeRetrofitService()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = keycloackservice.requestGrant(
                    realm!!, "microservicegasclient",
                    ORGANIZATIONUNITLIST.find { it.code == realm }!!.secret,
                    "password", username!!, password!!
                )
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            tokens = it
                            Log.e("Controller ", "CheckValidPassworddelete success")
                            userLoginState.value = tokens.access_token
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Ошибка авторизации, проверьте введённые данные",
                            Toast.LENGTH_LONG
                        ).show()
                        userLoginState.value = ""
                    }
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Ошибка авторизации, проверьте подключение к сети организации",
                        Toast.LENGTH_LONG
                    ).show()
                }
                Log.e("Controller", "Exception in CheckValidPassword  ${e.message}")
            } catch (e: Throwable) {
                Log.e("Controller", "Ooops: Something else went wrong")
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Ошибка авторизации, проверьте подключение к сети организации",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        }
    }


    private fun startDownloadingDb(response: Response<ResponseBody>) {
        viewModelScope.launch(Dispatchers.Default) {
            downloadDb("", response)
            viewModelScope.launch(Dispatchers.Main) {
                if (Constants.checkDownloadedFile(context)) {
                    onCreateUnitListFragment?.invoke()
                    putUpdateDatabaseDateToConfig()
                    updateDatabase()
                }
            }
        }
    }


    private fun downloadDb(flag: String, response: Response<ResponseBody>) {
        try {
            val url = URL(Constants.URL_TO_DATABASE)
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
            while (true) {
                val len = body.byteStream().read(buffer)
                if (len != -1) {
                    fileOutputStream.write(buffer, 0, len)
                    val currentFileSize = fullPath.length().toFloat()
                    val currentDatabaseSize = currentDatabaseSize.toFloat()
                    val percent = currentFileSize.div(currentDatabaseSize).times(100)
                    dbDownloadingProgressState.postValue(PointF(percent, percent))
                } else break
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


    private fun startUpdatingDb(response: Response<ResponseBody>) {
        viewModelScope.launch(Dispatchers.Default) {
            downloadDb("UPDATING", response)
            viewModelScope.launch(Dispatchers.Main) {
                if (Constants.checkDownloadedFile(context)) {
                    deleteOldRoomFile()
                    putUpdateDatabaseDateToConfig()
                    isUnitFragmentActive = false
                    onDatabaseUpdated?.invoke(true)
                    Toast.makeText(context, "Updating database successful", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    onDatabaseUpdated?.invoke(false)
                    Toast.makeText(context, "Updating database invalid", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    fun makeRequestToDatabase(token: String, selectedRealm: String, downloadType: String) {
        val logTag = "Authorization module"
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
                                if (downloadType == "DOWNLOAD")
                                    startDownloadingDb(response)
                                else
                                    startUpdatingDb(response)
                            }
                            Log.e(logTag, "Success! Response code is ${response.code()}")
                        } else {
                            Log.e(logTag, "Can't download database")
                            Toast.makeText(
                                context,
                                "Не удаётся скачать базу данных! Ошибка ${response.code()}",
                                Toast.LENGTH_LONG
                            ).show()
                            onDatabaseUpdated?.invoke(false)
                            spinnerState.value = false
                            downloadSpinnerState.value = false
                        }
                    }
                } catch (e: HttpException) {
                    Log.e(logTag, "Exception in $logTag, exception: ${e.message}")
                    Toast.makeText(
                        context,
                        "Ошибка авторизации, проверьте введённые данные",
                        Toast.LENGTH_LONG
                    )
                        .show()
                } catch (e: Throwable) {
                    Log.e(logTag, "Ooops: Something else went wrong ${e.message}")
                }
            } catch (e: KotlinNullPointerException) {
            }
        }
    }



}
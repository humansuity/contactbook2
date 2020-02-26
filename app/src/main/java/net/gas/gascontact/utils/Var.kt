package net.gas.gascontact.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.math.BigInteger
import java.security.MessageDigest

object Var {

    const val URL_TO_DATABASE = "http://contactbook.oblgaz/contacts.db"
    const val PENDING_INTENT_TYPE = "EVERY_DAY_AT_8_AM"
    const val PENDING_INTENT_ACTION = "ACTION"
    const val KEY_ID = "id"
    val DATABASE_NAME = URL_TO_DATABASE.split("/").last()
    const val STORAGE_PERMISSION_CODE = 1000
    const val UNIT_FRAGMENT_ID = 1
    const val DEPARTMENT_FRAGMENT_ID = 2

    const val APP_PREFERENCES = "appsettings"
    const val APP_DATABASE_SIZE = "dbsize"
    const val APP_DATABASE_UPDATE_TIME = "dbUpdateTime"
    const val APP_DATABASE_UPDATE_DATE = "dbUpdateDate"


    inline fun <VM : ViewModel> viewModelFactory(crossinline f: () -> VM) =
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(aClass: Class<T>):T = f() as T
        }


    fun stringMD5(key: String) : String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(key.toByteArray())).toString(16).padStart(32, '0')
    }

}
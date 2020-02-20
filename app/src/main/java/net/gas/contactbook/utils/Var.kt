package net.gas.contactbook.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

object Var {

    val URL_TO_DATABASE = "http://contactbook.oblgaz/contacts.db"
    val PENDING_INTENT_TYPE = "EVERY_DAY_AT_8_AM"
    val PENDING_INTENT_ACTION = "ACTION"
    val KEY_ID = "id"
    val DATABASE_NAME = URL_TO_DATABASE.split("/").last()
    val STORAGE_PERMISSION_CODE = 1000
    val UNIT_FRAGMENT_ID = 1
    val DEPARTMENT_FRAGMENT_ID = 2

    val APP_PREFERENCES = "appsettings"
    val APP_DATABASE_SIZE = "dbsize"
    val APP_DATABASE_UPDATE_TIME = "dbUpdateTime"


    inline fun <VM : ViewModel> viewModelFactory(crossinline f: () -> VM) =
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(aClass: Class<T>):T = f() as T
        }

}
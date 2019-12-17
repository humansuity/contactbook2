package net.gas.contactbook.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

object Var {

    val DATABASE_NAME = "qcontacts.db"
    val URL_TO_DATABASE = "http://contactbook.oblgaz/qcontacts.db"
    val STORAGE_PERMISSION_CODE = 1000
    val UNIT_FRAGMENT_ID = 1
    val DEPARTMENT_FRAGMENT_ID = 2


    inline fun <VM : ViewModel> viewModelFactory(crossinline f: () -> VM) =
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(aClass: Class<T>):T = f() as T
        }

}
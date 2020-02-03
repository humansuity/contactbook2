package net.gas.contactbook.utils

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.util.concurrent.TimeUnit

object Var {

    val URL_TO_DATABASE = "http://contactbook.oblgaz/contacts.db"
    val DATABASE_NAME = URL_TO_DATABASE.split("/").last()
    val STORAGE_PERMISSION_CODE = 1000
    val UNIT_FRAGMENT_ID = 1
    val DEPARTMENT_FRAGMENT_ID = 2


    inline fun <VM : ViewModel> viewModelFactory(crossinline f: () -> VM) =
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(aClass: Class<T>):T = f() as T
        }

    inline fun <T> measureExecution(logMessage: String, logLevel: Int = Log.DEBUG, function: () -> T): T {
        val startTime = System.nanoTime()
        return function.invoke().also {
            val difference = System.nanoTime() - startTime
            Log.e("Time", "$logMessage; took ${TimeUnit.NANOSECONDS.toMillis(difference)}ms")
        }
    }

}
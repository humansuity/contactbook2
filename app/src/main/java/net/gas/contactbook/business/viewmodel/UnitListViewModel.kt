package net.gas.contactbook.business.viewmodel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import net.gas.contactbook.business.database.entities.Units
import net.gas.contactbook.business.model.UnitListModel
import net.gas.contactbook.utils.Var
import java.io.File


class UnitListViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val unitListModel = UnitListModel(context)
    var unitList: LiveData<List<Units>>

    init {
        unitList = unitListModel.unitList
    }

    fun onItemClick(id: Int) {
        Toast.makeText(context, "You tap on:$id", Toast.LENGTH_SHORT).show()
        deleteDownloadedDatabase(context)
    }

    private fun deleteDownloadedDatabase(context: Context) {
        val pathToDownloadedDatabase = context.filesDir.path + "/" + Var.DATABASE_NAME
        if (File(pathToDownloadedDatabase).exists()) {
            val isDeleted = File(pathToDownloadedDatabase).delete()
            Toast.makeText(context, if (isDeleted) "okay" else "not okay", Toast.LENGTH_SHORT)
                .show()
        }
    }


}
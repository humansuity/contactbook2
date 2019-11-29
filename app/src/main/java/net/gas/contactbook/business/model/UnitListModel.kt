package net.gas.contactbook.business.model

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import net.gas.contactbook.business.database.cores.ContactbookDatabase
import net.gas.contactbook.business.database.entities.Units
import net.gas.contactbook.utils.Var
import java.io.File

class UnitListModel(context: Context) {

    private val database = ContactbookDatabase.getInstance(context)
    var unitList: LiveData<List<Units>>

    init {
        unitList = database?.unitsDao()!!.getEntities()
    }


    private fun deleteDownloadedDatabase(context: Context) {
        val pathToDownloadedDatabase = context.filesDir.path + "/" + Var.DATABASE_NAME
        val isDeleted = File(pathToDownloadedDatabase).delete()
        Toast.makeText(context, if (isDeleted) "okay" else "not okay", Toast.LENGTH_SHORT).show()
    }


}
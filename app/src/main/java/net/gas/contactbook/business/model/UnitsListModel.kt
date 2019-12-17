package net.gas.contactbook.business.model

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import net.gas.contactbook.business.database.cores.ContactbookDatabase
import net.gas.contactbook.business.database.entities.Departments
import net.gas.contactbook.business.database.entities.Units
import net.gas.contactbook.utils.Var
import java.io.File

class UnitsListModel(context: Context) {

    private val database = ContactbookDatabase.getInstance(context)

    fun getUnitEntities() : LiveData<List<Units>> = database?.unitsDao()!!.getEntities()

    fun getDepartmentEntitiesById(id: Int) : LiveData<List<Departments>>
            = database?.departmentsDao()!!.getEntitiesById(id)

}
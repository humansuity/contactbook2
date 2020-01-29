package net.gas.contactbook.business.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.gas.contactbook.business.database.entities.Departments
import net.gas.contactbook.business.database.entities.Persons
import net.gas.contactbook.business.database.entities.Units
import net.gas.contactbook.business.model.DataModel
import net.gas.contactbook.utils.FragmentManagerHelper
import net.gas.contactbook.utils.Var
import java.io.File


class BranchListViewModel(val context: Context,
                          private var fragmentManagerHelper: FragmentManagerHelper)
    : ViewModel() {

    private val dataModel = DataModel(context)
    var unitList: LiveData<List<Units>> = MutableLiveData<List<Units>>()
    var departmentList: LiveData<List<Departments>> = MutableLiveData<List<Departments>>()
    var personList: LiveData<List<Persons>> = MutableLiveData<List<Persons>>()

    var unitFragmentCallback: (() -> Unit)? = null
    var departmentFragmentCallback: (() -> Unit) ? = null
    var isUnitFragmentActive = false
    private var unitId = 0


    init {
        unitList = dataModel.getUnitEntities()
    }


    fun onItemClick(id: Int, listType: String) {
        when (listType) {
            Units::class.java.name -> {
                departmentList = dataModel.getDepartmentEntitiesById(id)
                unitFragmentCallback?.invoke()
                unitId = id
            }
            Departments::class.java.name -> {
                personList = dataModel.getPersonsEntitiesByIds(unitId, id)
                departmentFragmentCallback?.invoke()
                Toast.makeText(context, "unit id is: $unitId, departments is: $id", Toast.LENGTH_SHORT).show()
            }
        }
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
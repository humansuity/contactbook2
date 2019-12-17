package net.gas.contactbook.business.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.contactbook.R
import net.gas.contactbook.business.database.entities.Departments
import net.gas.contactbook.business.database.entities.Units
import net.gas.contactbook.business.model.UnitsListModel
import net.gas.contactbook.ui.fragments.UnitListFragment
import net.gas.contactbook.utils.FragmentManagerHelper
import net.gas.contactbook.utils.Var
import java.io.File


class UnitsListViewModel(val context: Context,
                         private var fragmentManagerHelper: FragmentManagerHelper)
    : ViewModel() {


    private val unitListModel = UnitsListModel(context)
    var unitList: LiveData<List<Units>>
    var departmentList: LiveData<List<Departments>> = MutableLiveData<List<Departments>>()


    init {
        unitList = unitListModel.getUnitEntities()
    }


    fun onItemClick(id: Int, listType: String) {
        when (listType) {
            Units::class.java.name -> {

            }
            Departments::class.java.name -> {

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


    fun createUnitListFragment() {

    }



}
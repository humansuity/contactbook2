package net.gas.contactbook.business.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import net.gas.contactbook.business.database.entities.*
import net.gas.contactbook.business.model.DataModel
import net.gas.contactbook.utils.Var
import java.io.File


class BranchListViewModel(application: Application)
    : AndroidViewModel(application) {

    val context: Context = application.applicationContext
    private val dataModel = DataModel(context)
    var unitList: LiveData<List<Units>> = MutableLiveData<List<Units>>()
    var departmentList: LiveData<List<Departments>> = MutableLiveData<List<Departments>>()
    var personList: LiveData<List<Persons>> = MutableLiveData<List<Persons>>()
    var personEntity: LiveData<Persons> = MutableLiveData<Persons>()
    var photoEntity: LiveData<Photos> = MutableLiveData<Photos>()
    var postEntity: LiveData<Posts> = MutableLiveData<Posts>()

    var unitFragmentCallback: (() -> Unit)? = null
    var departmentFragmentCallback: (() -> Unit) ? = null
    var personFragmentCallBack: (() -> Unit)? = null
    var isUnitFragmentActive = false
    private var unitId = 0

    init { GlobalScope.launch(Dispatchers.IO) { unitList = dataModel.getUnitEntities() } }


    fun onBranchItemClick(id: Int, listType: String) {
        when (listType) {
            Units::class.java.name -> {
                GlobalScope.launch(Dispatchers.IO) {
                    departmentList = dataModel.getDepartmentEntitiesById(id)
                    unitId = id
                    unitFragmentCallback?.invoke()
                }
            }
            Departments::class.java.name -> {
                GlobalScope.launch(Dispatchers.IO) { personList = dataModel.getPersonsEntitiesByIds(unitId, id) }
                departmentFragmentCallback?.invoke()
            }
        }
        deleteDownloadedDatabase(context)
    }

    fun onPersonItemClick(id: Int) {
        personEntity = dataModel.getPersonEntityById(id)
        personFragmentCallBack?.invoke()
    }

    fun setupPhotoEntity(id: Int?) {
        photoEntity = dataModel.getPhotoEntityById(id!!)
    }

    fun setupPostEntity(id: Int?) {
        postEntity = dataModel.getPostsEntityById(id!!)
    }

    fun getDepartmentEntity(id: Int) : LiveData<Departments>
            = dataModel.getDepartmentEntityById(id)

    fun getUnitEntity(id: Int) : LiveData<Units>
            = dataModel.getUnitEntityById(id)


    private fun deleteDownloadedDatabase(context: Context) {
        val pathToDownloadedDatabase = context.filesDir.path + "/" + Var.DATABASE_NAME
        if (File(pathToDownloadedDatabase).exists()) {
            val isDeleted = File(pathToDownloadedDatabase).delete()
            Toast.makeText(context, if (isDeleted) "okay" else "not okay", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun getDepartmentList(id: Int) {

    }

}
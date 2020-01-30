package net.gas.contactbook.business.viewmodel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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


    init { unitList = dataModel.getUnitEntities() }


    fun onBranchItemClick(id: Int, listType: String) {
        when (listType) {
            Units::class.java.name -> {
                departmentList = dataModel.getDepartmentEntitiesById(id)
                unitFragmentCallback?.invoke()
                unitId = id
            }
            Departments::class.java.name -> {
                personList = dataModel.getPersonsEntitiesByIds(unitId, id)
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

    fun setupPostsEntity(id: Int?) {
        postEntity = dataModel.getPostsEntityById(id!!)
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
package net.gas.contactbook.business.viewmodel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    var spinnerState: MutableLiveData<Boolean> = MutableLiveData()
    var toolbarTitle: MutableLiveData<String> = MutableLiveData()
    var departmentEntity: LiveData<Departments> = MutableLiveData()
    var personListMapping: LiveData<List<Persons>> = MutableLiveData()
    var unitEntity: LiveData<Units> = MutableLiveData()

    var unitFragmentCallback: (() -> Unit)? = null
    var departmentFragmentCallback: (() -> Unit) ? = null
    var personFragmentCallBack: (() -> Unit)? = null
    var isUnitFragmentActive = false
    private var unitId = 0

    init {
        viewModelScope.launch(Dispatchers.IO) {
            spinnerState.postValue(true)
            unitList = dataModel.getUnitEntities()
            spinnerState.postValue(false)
        }
    }


    fun onBranchItemClick(id: Int, listType: String) {
        when (listType) {
            Units::class.java.name -> {
                viewModelScope.launch(Dispatchers.IO) {
                    spinnerState.postValue(true)
                    departmentList = dataModel.getDepartmentEntitiesById(id)
                    unitEntity = dataModel.getUnitEntityByIdAsync(id)
                    spinnerState.postValue(false)
                }
                unitId = id
                unitFragmentCallback?.invoke()
            }
            Departments::class.java.name -> {
                viewModelScope.launch(Dispatchers.IO) {
                    spinnerState.postValue(true)
                    personList = dataModel.getPersonsEntitiesByIds(unitId, id)
                    departmentEntity = dataModel.getDepartmentEntityByIdAsync(id)
                    spinnerState.postValue(false)
                }
                departmentFragmentCallback?.invoke()
            }
        }
        deleteDownloadedDatabase(context)
    }

    fun findPersonsByTag(sequence: String): LiveData<List<Persons>> {
        return liveData { emitSource(dataModel.getPersonListByTag(sequence)) }
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
}
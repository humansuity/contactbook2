package net.gas.contactbook.business.model

import android.content.Context
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.gas.contactbook.business.database.cores.ContactbookDatabase
import net.gas.contactbook.business.database.entities.*

class DataModel(context: Context) {

    private val database = ContactbookDatabase.getInstance(context)

    fun getUnitEntities() : LiveData<List<Units>>
            = database?.unitsDao()!!.getEntities()

    fun getDepartmentEntitiesById(id: Int) : LiveData<List<Departments>>
            = database?.departmentsDao()!!.getEntitiesById(id)

    fun getPersonsEntitiesByIds(unitId: Int, departmentId: Int) : LiveData<List<Persons>>
            = database?.personsDao()!!.getEntitiesByIds(unitId, departmentId)

    fun getPersonEntityById(id: Int) : LiveData<Persons>
            = database?.personsDao()!!.getEntityById(id)

    fun getPhotoEntityById(id: Int) : LiveData<Photos>
            = database?.photosDao()!!.getEntityById(id)

    fun getPostsEntityById(id: Int) : LiveData<Posts>
            = database?.postsDao()!!.getEntityById(id)

    fun getDepartmentEntityById(id: Int) : LiveData<Departments>
            = database?.departmentsDao()!!.getEntityById(id)


    fun getUnitEntityById(id: Int) : LiveData<Units>
            = database?.unitsDao()!!.getEntityById(id)

    suspend fun getPersonListByTag(tag: String) : LiveData<List<Persons>>
            = withContext(Dispatchers.IO) { database?.personsDao()!!.getEntitiesByTag(tag) }

    suspend fun getUnitEntityByIdAsync(id: Int) : LiveData<Units>
            = withContext(Dispatchers.IO) { database?.unitsDao()!!.getEntityById(id) }

    suspend fun getDepartmentEntityByIdAsync(id: Int) : LiveData<Departments>
            = withContext(Dispatchers.IO) { database?.departmentsDao()!!.getEntityById(id) }



}
package net.gas.contactbook.business.model

import android.content.Context
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.gas.contactbook.business.database.cores.ContactbookDatabase
import net.gas.contactbook.business.database.entities.*

class DataModel(context: Context) {

    private val database = ContactbookDatabase.getInstance(context)

    suspend fun getUnitEntities() : LiveData<List<Units>>
            = withContext(Dispatchers.IO) { database?.unitsDao()!!.getEntities() }

    suspend fun getDepartmentEntitiesById(id: Int) : LiveData<List<Departments>>
            = withContext(Dispatchers.IO) { database?.departmentsDao()!!.getEntitiesById(id) }

    suspend fun getPersonsEntitiesByIds(unitId: Int, departmentId: Int) : LiveData<List<Persons>>
            = withContext(Dispatchers.IO) { database?.personsDao()!!.getEntitiesByIds(unitId, departmentId) }

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



}
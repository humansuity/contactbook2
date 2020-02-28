package net.gas.gascontact.business.model

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.gas.gascontact.business.database.cores.ContactbookDatabase
import net.gas.gascontact.business.database.entities.*
import net.gas.gascontact.utils.Var

class DataModel(private val context: Context) {

    private var database = ContactbookDatabase
        .getInstance(context, Var.stringMD5(getCurrentDbUpdateDate()!!))

    fun updateDatabase() {
        ContactbookDatabase.destroyInstance()
        database = ContactbookDatabase
            .getInstance(context, key = Var.stringMD5(getCurrentDbUpdateDate()!!))
    }

    private fun getCurrentDbUpdateDate() : String? {
        val sharedPreferences = context.getSharedPreferences(Var.APP_PREFERENCES, Context.MODE_PRIVATE)
        return if (sharedPreferences.contains(Var.APP_DATABASE_UPDATE_DATE)) {
            sharedPreferences.getString(Var.APP_DATABASE_UPDATE_DATE, "")
        } else ""

    }

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

    fun getPostEntityById(id: Int) : LiveData<Posts>
            = database?.postsDao()!!.getEntityById(id)

    fun getDepartmentEntityById(id: Int) : LiveData<Departments>
            = database?.departmentsDao()!!.getEntityById(id)


    fun getUnitEntityById(id: Int) : LiveData<Units>
            = database?.unitsDao()!!.getEntityById(id)

    fun getPostsEntities() : LiveData<List<Posts>> = database?.postsDao()!!.getEntities()
    fun getPersonsEntities() : LiveData<List<Persons>> = database?.personsDao()!!.getEntities()

    suspend fun getPersonListByTag(tag: String) : LiveData<List<Persons>>
            = withContext(Dispatchers.IO) { database?.personsDao()!!.getEntitiesByTag(tag) }

    fun getPersonEntitiesByUpcomingBirthday() : LiveData<List<Persons>>
            = database?.personsDao()!!.getEntitiesByUpcomingBirth()

    fun getPersonEntitiesByBirthday() : LiveData<List<Persons>>
            = database?.personsDao()!!.getEntitiesByCurrentBirth()

    suspend fun getUnitEntityByIdAsync(id: Int) : LiveData<Units>
            = withContext(Dispatchers.IO) { database?.unitsDao()!!.getEntityById(id) }



    suspend fun getDepartmentEntityByIdAsync(id: Int) : LiveData<Departments>
            = withContext(Dispatchers.IO) { database?.departmentsDao()!!.getEntityById(id) }



}
package net.gas.gascontact.business.model

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.gas.gascontact.business.database.cores.ContactbookDatabase
import net.gas.gascontact.business.database.entities.*
import net.gas.gascontact.utils.Var

class DataModel(private val context: Context) {

    private var database = ContactbookDatabase
        .getInstance(context, Var.stringMD5(getGlobalKey() + getRealmFromConfig()))

    fun updateDatabase() {
        ContactbookDatabase.destroyInstance()
        database = ContactbookDatabase
            .getInstance(context, key = Var.stringMD5(getGlobalKey() + getRealmFromConfig()))
    }

    private fun getRealmFromConfig() : String? {
        val sharedPreferences = context.getSharedPreferences(Var.APP_PREFERENCES, Context.MODE_PRIVATE)
        return if (sharedPreferences.contains("REALM")) {
            sharedPreferences.getString("REALM", "")
        } else ""

    }

    init {
        Log.e("KEY", Var.stringMD5(getGlobalKey() + getRealmFromConfig()))
    }


    private fun getGlobalKey() : String = "velikoborecami"

    fun getPrimaryUnitEntities() : LiveData<List<Units>>
           = database?.unitsDao()!!.getPrimaryEntities()

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

    fun getUnitEntitiesByIds(ids: Array<Int>) : LiveData<List<Units>>
            = database?.unitsDao()!!.getEntitiesByIds(ids)

    fun getPostEntitiesByIds(ids: Array<Int>) : LiveData<List<Posts>>
            = database?.postsDao()!!.getEntitiesByIds(ids)

    suspend fun getPersonListByLastNameTag(tag: String) : LiveData<List<Persons>>
            = withContext(Dispatchers.IO) { database?.personsDao()!!.getEntitiesByLastNameTag(tag) }
    suspend fun getPersonListByNameTag(tag: String) : LiveData<List<Persons>>
            = withContext(Dispatchers.IO) { database?.personsDao()!!.getEntitiesByNameTag(tag) }
    suspend fun getPersonListByPatronymicTag(tag: String) : LiveData<List<Persons>>
            = withContext(Dispatchers.IO) { database?.personsDao()!!.getEntitiesByPatronymicTag(tag) }
    suspend fun getPersonListByMobilePhoneTag(tag: String) : LiveData<List<Persons>>
            = withContext(Dispatchers.IO) { database?.personsDao()!!.getEntitiesByMobilePhoneTag(tag) }

    fun getSecondaryEntities(parentId: Int) : LiveData<List<Units>>
            = database?.unitsDao()!!.getSecondaryEntities(parentId)

    fun getUnitEntitiesByParentId(parentId: Int) : LiveData<List<Units>>
            = database?.unitsDao()!!.getEntitiesByParentId(parentId)

    fun getUnitEntitiesByParentByDepartmentId(departmentId: Int) : LiveData<List<Units>>
            = database?.departmentsDao()!!.getUnitEntitiesByParentByDepartmentId(departmentId)


    fun getUpcomingPersonWithBirthday(period: String) : LiveData<List<Persons>> {
        return when(period) {
            "TODAY" -> {
                database?.personsDao()!!.getEntitiesByTodayBirth()
            }
            "TOMORROW" -> {
                database?.personsDao()!!.getEntitiesByTomorrowBirth()
            }
            "DAY_AFTER_TOMORROW" -> {
                database?.personsDao()!!.getEntitiesByDayAfterTomorrowBirth()
            }
            "IN_A_WEEK" -> {
                database?.personsDao()!!.getEntitiesByInAWeekBirth()
            }
            else -> { liveData {}}
        }
    }
}
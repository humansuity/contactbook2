package net.gas.gascontact.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.gas.gascontact.database.ContactbookDatabase
import net.gas.gascontact.database.entities.*
import net.gas.gascontact.utils.Constants

class DataModel(private val context: Context) {

    private var database = ContactbookDatabase
        .getInstance(context, Constants.stringMD5(getGlobalKey() + getRealmFromConfig()))

    fun updateDatabase() {
        ContactbookDatabase.destroyInstance()
        database = ContactbookDatabase
            .getInstance(context, key = Constants.stringMD5(getGlobalKey() + getRealmFromConfig()))
    }

    private fun getRealmFromConfig(): String? {
        val sharedPreferences =
            context.getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE)
        return if (sharedPreferences.contains("REALM")) {
            sharedPreferences.getString("REALM", "")
        } else ""

    }

    init {
        Log.e("KEY", Constants.stringMD5(getGlobalKey() + getRealmFromConfig()))
    }


    private fun getGlobalKey(): String = "velikoborecami"

    fun getPrimaryUnitEntities(): LiveData<List<Units>> =
        database?.unitsDao()!!.getPrimaryEntities()

    fun getDepartmentEntitiesById(id: Int): LiveData<List<Departments>> =
        database?.departmentsDao()!!.getEntitiesById(id)

    fun getPersonsEntitiesByIds(unitId: Int, departmentId: Int): LiveData<List<Persons>> =
        database?.personsDao()!!.getEntitiesByIds(unitId, departmentId)

    fun getPersonEntityById(id: Int): LiveData<Persons> = database?.personsDao()!!.getEntityById(id)

    fun getPhotoEntityById(id: Int): LiveData<Photos> = database?.photosDao()!!.getEntityById(id)

    fun getPostEntityById(id: Int): LiveData<Posts> = database?.postsDao()!!.getEntityById(id)

    fun getDepartmentEntityById(id: Int): LiveData<Departments> =
        database?.departmentsDao()!!.getEntityById(id)

    fun getUnitEntityById(id: Int): LiveData<Units> = database?.unitsDao()!!.getEntityById(id)

    fun getUnitEntitiesByIds(ids: Array<Int>): LiveData<List<Units>> =
        database?.unitsDao()!!.getEntitiesByIds(ids)

    fun getPostEntitiesByIds(ids: Array<Int>): LiveData<List<Posts>> =
        database?.postsDao()!!.getEntitiesByIds(ids)

    suspend fun getPersonListByLastNameTag(tag: String): LiveData<List<Persons>> =
        withContext(Dispatchers.IO) { database?.personsDao()!!.getEntitiesByLastNameTag(tag) }

    suspend fun getPersonListByNameTag(tag: String): LiveData<List<Persons>> =
        withContext(Dispatchers.IO) { database?.personsDao()!!.getEntitiesByNameTag(tag) }

    suspend fun getPersonListByPatronymicTag(tag: String): LiveData<List<Persons>> =
        withContext(Dispatchers.IO) { database?.personsDao()!!.getEntitiesByPatronymicTag(tag) }

    suspend fun getPersonListByMobilePhoneTag(tag: String): LiveData<List<Persons>> =
        withContext(Dispatchers.IO) { database?.personsDao()!!.getEntitiesByMobilePhoneTag(tag) }

    fun getUnitSecondaryEntities(parentId: Int): LiveData<List<Units>> =
        database?.unitsDao()!!.getSecondaryEntities(parentId)

    fun getDepartmentSecondaryEntities(parentId: Int): LiveData<List<Departments>> =
        database?.departmentsDao()!!.getSecondaryEntities(parentId)


    fun getPersonsByUnitId(unitId: Int) = database?.personsDao()!!.getEntitiesByUnitId(unitId)

    fun getUnitIdByDepartmentId(departmentId: Int) = database?.unitsDao()!!.getEntityIdByDepartmentId(departmentId)


    fun getUpcomingPersonWithBirthday(period: String): LiveData<List<Persons>> {
        return when (period) {
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
            else -> {
                liveData {}
            }
        }
    }
}
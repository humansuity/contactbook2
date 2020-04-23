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

    private fun getCurrentDbUpdateDate() : String? {
        val sharedPreferences = context.getSharedPreferences(Var.APP_PREFERENCES, Context.MODE_PRIVATE)
        return if (sharedPreferences.contains(Var.APP_DATABASE_UPDATE_DATE)) {
            sharedPreferences.getString(Var.APP_DATABASE_UPDATE_DATE, "")
        } else ""
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


    fun getUnitEntities() : LiveData<List<Units>> {
        return try {
            database?.unitsDao()!!.getEntities()
        } catch (e: Exception) {
            Toast.makeText(context, "Smth went wrong", Toast.LENGTH_SHORT).show()
            liveData {  }
        }
    }

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

    suspend fun getPersonListByLastNameTag(tag: String) : LiveData<List<Persons>>
            = withContext(Dispatchers.IO) { database?.personsDao()!!.getEntitiesByLastNameTag(tag) }
    suspend fun getPersonListByNameTag(tag: String) : LiveData<List<Persons>>
            = withContext(Dispatchers.IO) { database?.personsDao()!!.getEntitiesByNameTag(tag) }
    suspend fun getPersonListByPatronymicTag(tag: String) : LiveData<List<Persons>>
            = withContext(Dispatchers.IO) { database?.personsDao()!!.getEntitiesByPatronymicTag(tag) }
    suspend fun getPersonListByMobilePhoneTag(tag: String) : LiveData<List<Persons>>
            = withContext(Dispatchers.IO) { database?.personsDao()!!.getEntitiesByMobilePhoneTag(tag) }


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
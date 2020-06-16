package net.gas.gascontact.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import net.gas.gascontact.database.entities.Persons

@Dao
interface PersonsDao {

    @Query("select persons.* from persons, posts where unit_id = :unitId and department_id = :departmentId and persons.post_id = posts.id order by rangir, lastname")
    fun getEntitiesByIds(unitId: Int, departmentId: Int): LiveData<List<Persons>>

    @Query("select persons.* from persons, posts where lastname glob :tag and persons.post_id = posts.id order by rangir, lastname")
    fun getEntitiesByLastNameTag(tag: String): LiveData<List<Persons>>

    @Query("select persons.* from persons, posts where persons.firstname glob :tag and persons.post_id = posts.id order by rangir, lastname")
    fun getEntitiesByNameTag(tag: String): LiveData<List<Persons>>

    @Query("select persons.* from persons, posts where patronymic glob :tag and persons.post_id = posts.id order by rangir, lastname")
    fun getEntitiesByPatronymicTag(tag: String): LiveData<List<Persons>>

    @Query("select persons.* from persons, posts where mobile_phone like :tag || '%' and persons.post_id = posts.id order by rangir, lastname")
    fun getEntitiesByMobilePhoneTag(tag: String): LiveData<List<Persons>>

    @Query("select * from persons where id = :id")
    fun getEntityById(id: Int): LiveData<Persons>

    @Query("select * from persons")
    fun getEntities(): LiveData<List<Persons>>


    @Query("select persons.* from persons, posts where persons.post_id = posts.id and strftime('%m-%d', date(birthday)) = strftime('%m-%d', date('now')) order by rangir, lastname")
    fun getEntitiesByTodayBirth(): LiveData<List<Persons>>

    @Query("select persons.* from persons, posts where persons.post_id = posts.id and strftime('%m-%d', date(birthday)) = strftime('%m-%d', date('now', '+1 day')) order by rangir, lastname")
    fun getEntitiesByTomorrowBirth(): LiveData<List<Persons>>

    @Query("select persons.* from persons, posts where persons.post_id = posts.id and strftime('%m-%d', date(birthday)) = strftime('%m-%d', date('now', '+2 day')) order by rangir, lastname")
    fun getEntitiesByDayAfterTomorrowBirth(): LiveData<List<Persons>>

    @Query("select persons.* from persons, posts where persons.post_id = posts.id and strftime('%m-%d', date(birthday)) between strftime('%m-%d', date('now', '+3 day')) and strftime('%m-%d', date('now', '+7 day')) order by strftime('%m-%d', date(birthday)), rangir, lastname")
    fun getEntitiesByInAWeekBirth(): LiveData<List<Persons>>

}
package net.gas.gascontact.business.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import net.gas.gascontact.business.database.entities.Persons

@Dao
interface PersonsDao {

    @Query("select persons.* from persons, posts where unit_id = :unitId and department_id = :departmentId and persons.post_id = posts.id order by rangir, lastname")
    fun getEntitiesByIds(unitId: Int, departmentId: Int) : LiveData<List<Persons>>

    @Query("select persons.* from persons, posts where lastname glob :tag and persons.post_id = posts.id order by rangir, lastname")
    fun getEntitiesByTag(tag: String) : LiveData<List<Persons>>

    @Query("select * from persons where id = :id")
    fun getEntityById(id: Int) : LiveData<Persons>

    @Query("select * from persons")
    fun getEntities() : LiveData<List<Persons>>

    @Query("select persons.* from persons, posts where persons.post_id = posts.id and strftime('%m-%d', date(birthday)) between strftime('%m-%d', date('now')) and strftime('%m-%d', date('now', '+7 day')) order by strftime('%m-%d', date(birthday)), rangir, lastname")
    fun getEntitiesByUpcomingBirth() : LiveData<List<Persons>>


    @Query("select persons.* from persons, posts where persons.post_id = posts.id and strftime('%m-%d', date(birthday)) = strftime('%m-%d', date('now')) order by rangir, lastname")
    fun getEntitiesByCurrentBirth() : LiveData<List<Persons>>

}
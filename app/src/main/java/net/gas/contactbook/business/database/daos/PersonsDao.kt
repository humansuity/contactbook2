package net.gas.contactbook.business.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import net.gas.contactbook.business.database.entities.Persons

@Dao
interface PersonsDao {

    @Query("select * from persons where unit_id = :unitId and department_id = :departmentId")
    fun getEntitiesByIds(unitId: Int, departmentId: Int) : LiveData<List<Persons>>

    @Query("select * from persons where id = :id")
    fun getEntityById(id: Int) : LiveData<Persons>

}
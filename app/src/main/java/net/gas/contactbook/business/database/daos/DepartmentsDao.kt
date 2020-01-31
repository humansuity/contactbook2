package net.gas.contactbook.business.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import net.gas.contactbook.business.database.entities.Departments


@Dao
interface DepartmentsDao {

    @Query("select * from departments")
    fun getEntities() : LiveData<List<Departments>>

    @Query("select * from departments, (select department_id from relation where unit_id = :selectedID) as temp_id where departments.id = temp_id.department_id group by rangir")
    fun getEntitiesById(selectedID: Int) : LiveData<List<Departments>>

    @Query("select * from departments where id = :id")
    fun getEntityById(id: Int) : LiveData<Departments>

}
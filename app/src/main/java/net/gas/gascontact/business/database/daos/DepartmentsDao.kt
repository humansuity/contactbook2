package net.gas.gascontact.business.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import net.gas.gascontact.business.database.entities.Departments
import net.gas.gascontact.business.database.entities.Units


@Dao
interface DepartmentsDao {

    @Query("select * from departments, (select department_id from relation where unit_id = :selectedID) as temp_id where departments.id = temp_id.department_id order by rangir")
    fun getEntitiesById(selectedID: Int): LiveData<List<Departments>>

    @Query("select * from units where parent_id = (select parent_id from units where id = (select unit_id from relation where department_id = :selectedID)) order by rangir")
    fun getUnitEntitiesByParentByDepartmentId(selectedID: Int): LiveData<List<Units>>

    @Query("select * from departments where id = :id")
    fun getEntityById(id: Int): LiveData<Departments>
}
package net.gas.gascontact.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import net.gas.gascontact.database.entities.Units


@Dao
interface UnitsDao {

    @Query("select * from units where parent_id = 0 order by rangir, name")
    fun getPrimaryEntities(): LiveData<List<Units>>

    @Query("select * from units where parent_id = :parent_id order by rangir, name")
    fun getSecondaryEntities(parent_id: Int): LiveData<List<Units>>

    @Query("select * from units where parent_id = (select parent_id from units where id = :parent_id) order by rangir")
    fun getEntitiesByParentId(parent_id: Int): LiveData<List<Units>>

    @Query("select * from units where id = :id")
    fun getEntityById(id: Int): LiveData<Units>

    @Query("select * from units where id in (:ids)")
    fun getEntitiesByIds(ids: Array<Int>): LiveData<List<Units>>

    @Query("select count(*) from units where parent_id = :id")
    fun getEntitiesAmountByParentID(id: Int): Int


    @Query("select unit_id from relation where department_id = :id")
    fun getEntityIdByDepartmentId(id: Int): LiveData<Int>
}

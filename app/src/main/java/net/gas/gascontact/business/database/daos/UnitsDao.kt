package net.gas.gascontact.business.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import net.gas.gascontact.business.database.entities.Units


@Dao
interface UnitsDao {

    @Query("select * from units order by rangir")
    fun getEntities() : LiveData<List<Units>>

    @Query("select * from units where id = :id")
    fun getEntityById(id: Int) : LiveData<Units>

}

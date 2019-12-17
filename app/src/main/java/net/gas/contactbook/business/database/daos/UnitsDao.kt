package net.gas.contactbook.business.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import net.gas.contactbook.business.database.entities.Units


@Dao
interface UnitsDao {

    @Query("select * from units order by rangir")
    fun getEntities() : LiveData<List<Units>>

}

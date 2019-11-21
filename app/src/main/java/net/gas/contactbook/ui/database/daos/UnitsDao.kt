package net.gas.contactbook.ui.database.daos

import androidx.room.Dao
import androidx.room.Query
import net.gas.contactbook.ui.database.entities.Units


@Dao
interface UnitsDao {

    @Query("select * from units")
    fun getEntities() : List<Units>

}
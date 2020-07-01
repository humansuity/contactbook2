package net.gas.gascontact.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import net.gas.gascontact.database.entities.Photos


@Dao
interface PhotosDao {

    @Query("select * from photos where id = :id")
    fun getEntityById(id: Int): LiveData<Photos>

}
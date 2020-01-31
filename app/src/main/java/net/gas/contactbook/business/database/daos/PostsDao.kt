package net.gas.contactbook.business.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import net.gas.contactbook.business.database.entities.Posts


@Dao
interface PostsDao {

    @Query("select * from posts where id = :id")
    fun getEntityById(id: Int) : LiveData<Posts>

}
package net.gas.gascontact.business.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import net.gas.gascontact.business.database.entities.Posts


@Dao
interface PostsDao {

    @Query("select * from posts where id = :id")
    fun getEntityById(id: Int) : LiveData<Posts>

    @Query("select * from posts")
    fun getEntities() : LiveData<List<Posts>>

    @Query("select * from posts where id in (:ids)")
    fun getEntitiesByIds(ids: Array<Int>) : LiveData<List<Posts>>

}
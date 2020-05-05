package net.gas.gascontact.business.database.entities


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "units")
data class Units (
    @PrimaryKey val id: Int,
    var name: String?,
    val code: String?,
    val rangir: Int?,
    val parent_id: Int?
)
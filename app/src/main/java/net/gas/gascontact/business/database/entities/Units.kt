package net.gas.gascontact.business.database.entities


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "units")
data class Units (
    @PrimaryKey val id: Short,
    var name: String?,
    val code: String?,
    val rangir: Int?
)
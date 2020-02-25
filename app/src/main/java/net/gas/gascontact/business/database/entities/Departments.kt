package net.gas.gascontact.business.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "departments")
data class Departments(
    @PrimaryKey val id: Short,
    val name: String?,
    val code: String?,
    val rangir: Int?
)
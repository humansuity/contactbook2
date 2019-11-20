package net.gas.contactbook.ui.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "relation")
data class Relation (
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "unit_id") val unitID: Int?,
    @ColumnInfo(name = "department_id") val departmentID: Int?
)
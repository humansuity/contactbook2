package net.gas.gascontact.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "relation",
    foreignKeys =
    [
        ForeignKey(
            entity = Units::class,
            parentColumns = ["id"],
            childColumns = ["unit_id"]
        ),
        ForeignKey(
            entity = Departments::class,
            parentColumns = ["id"],
            childColumns = ["department_id"]
        )
    ]
)
data class Relation(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "unit_id", index = true) val unitID: Int?,
    @ColumnInfo(name = "department_id", index = true) val departmentID: Int?
)
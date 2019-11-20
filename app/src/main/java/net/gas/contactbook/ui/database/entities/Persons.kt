package net.gas.contactbook.ui.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "persons")
data class Persons (
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "firstname") val firstName: String?,
    @ColumnInfo(name = "lastname") val lastName: String?,
    @ColumnInfo(name = "home_phone") val homePhone: String?,
    @ColumnInfo(name = "mobile_phone") val mobilePhone: String?,
    @ColumnInfo(name = "work_phone") val workPhone: String?,
    @ColumnInfo(name = "unit_id") val unitID: Int,
    @ColumnInfo(name = "department_id") val departmentID: Int,
    @ColumnInfo(name = "post_id") val postID: Int,
    @ColumnInfo(name = "photo_id") val photoID: Int,
    val mail: String?,
    val patronymic: String?,
    val birthday: String?
)
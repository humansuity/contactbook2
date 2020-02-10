package net.gas.contactbook.business.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = "persons",
    foreignKeys =
    [
        ForeignKey(
            entity = Units::class,
            parentColumns = ["id"],
            childColumns = ["unit_id"]),
        ForeignKey(
            entity = Departments::class,
            parentColumns = ["id"],
            childColumns = ["department_id"]),
        ForeignKey(
            entity = Posts::class,
            parentColumns = ["id"],
            childColumns = ["post_id"]),
        ForeignKey(
            entity = Photos::class,
            parentColumns = ["id"],
            childColumns = ["photo_id"])
    ]
)
data class Persons (
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "firstname") val firstName: String?,
    @ColumnInfo(name = "lastname") val lastName: String?,
    @ColumnInfo(name = "home_phone") val homePhone: String?,
    @ColumnInfo(name = "mobile_phone") val mobilePhone: String?,
    @ColumnInfo(name = "work_phone") val workPhone: String?,
    @ColumnInfo(name = "unit_id") val unitID: Short?,
    @ColumnInfo(name = "department_id") val departmentID: Short?,
    @ColumnInfo(name = "post_id") val postID: Short?,
    @ColumnInfo(name = "photo_id") val photoID: Int?,
    @ColumnInfo(name = "mail") val email: String?,
    @ColumnInfo(name = "short_phone") val shortPhone: String?,
    val patronymic: String?,
    val birthday: String?
)
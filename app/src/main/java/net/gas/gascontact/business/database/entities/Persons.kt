package net.gas.gascontact.business.database.entities

import androidx.room.*


@Entity(
    tableName = "persons",
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
        ),
        ForeignKey(
            entity = Posts::class,
            parentColumns = ["id"],
            childColumns = ["post_id"]
        ),
        ForeignKey(
            entity = Photos::class,
            parentColumns = ["id"],
            childColumns = ["photo_id"]
        )
    ],
    indices = [
        Index(
            "firstname",
            "lastname",
            "patronymic",
            "mobile_phone",
            name = "personIndex",
            unique = false
        )
    ]
)
data class Persons(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "firstname") val firstName: String?,
    @ColumnInfo(name = "lastname") val lastName: String?,
    @ColumnInfo(name = "home_phone") val homePhone: String?,
    @ColumnInfo(name = "mobile_phone") val mobilePhone: String?,
    @ColumnInfo(name = "work_phone") val workPhone: String?,
    @ColumnInfo(name = "unit_id", index = true) val unitID: Int?,
    @ColumnInfo(name = "department_id", index = true) val departmentID: Int?,
    @ColumnInfo(name = "post_id", index = true) val postID: Int?,
    @ColumnInfo(name = "photo_id", index = true) val photoID: Int?,
    @ColumnInfo(name = "mail") val email: String?,
    @ColumnInfo(name = "short_phone") val shortPhone: String?,
    val patronymic: String?,
    val birthday: String?
)
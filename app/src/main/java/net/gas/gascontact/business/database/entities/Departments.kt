package net.gas.gascontact.business.database.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "departments")
data class Departments(
    @PrimaryKey val id: Int,
    val name: String?,
    val code: String?,
    val rangir: Int?,
    val parent_id: Int?
) : Parcelable
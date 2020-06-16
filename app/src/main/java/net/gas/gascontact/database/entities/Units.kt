package net.gas.gascontact.database.entities


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "units")
data class Units(
    @PrimaryKey val id: Int,
    var name: String?,
    val code: String?,
    val rangir: Int?,
    val parent_id: Int?
) : Parcelable
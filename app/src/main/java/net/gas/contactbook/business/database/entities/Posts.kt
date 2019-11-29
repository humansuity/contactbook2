package net.gas.contactbook.business.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class Posts (
    @PrimaryKey val id: Short,
    val name: String?,
    val rangir: String?
)

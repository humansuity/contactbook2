package net.gas.contactbook.business.database.cores

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import net.gas.contactbook.business.database.daos.UnitsDao
import net.gas.contactbook.business.database.entities.*
import java.io.File


@Database(
    entities =
    [
        Departments::class,
        Persons::class,
        Photos::class,
        Posts::class,
        Relation::class,
        Units::class
    ],
    version = 1
)
abstract class ContactbookDatabase : RoomDatabase() {

    abstract fun unitsDao(): UnitsDao

    companion object {
        private var INSTANCE: ContactbookDatabase? = null
        private val DB_NAME = "qcontacts.db"

        fun getInstance(context: Context) : ContactbookDatabase? {

            val pathToDatabase = context.filesDir.path + "/" + DB_NAME

            if (INSTANCE == null) {
                synchronized(ContactbookDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        ContactbookDatabase::class.java,
                        DB_NAME
                    )
                        .createFromFile(File(pathToDatabase))
                        .build()
                }
            }
            return INSTANCE
        }


        fun destroyInstance() {
            INSTANCE = null
        }
    }

}
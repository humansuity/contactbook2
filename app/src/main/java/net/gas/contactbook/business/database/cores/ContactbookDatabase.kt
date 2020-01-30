package net.gas.contactbook.business.database.cores

import android.content.Context
import android.widget.Toast
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import net.gas.contactbook.business.database.daos.*
import net.gas.contactbook.business.database.entities.*
import net.gas.contactbook.utils.Var
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
    abstract fun postsDao(): PostsDao
    abstract fun photosDao(): PhotosDao
    abstract fun personsDao(): PersonsDao
    abstract fun departmentsDao() : DepartmentsDao

    companion object {
        private var INSTANCE: ContactbookDatabase? = null
        private val DB_NAME = Var.DATABASE_NAME

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
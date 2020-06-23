package net.gas.gascontact.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import net.gas.gascontact.database.daos.*
import net.gas.gascontact.database.entities.*
import net.gas.gascontact.utils.Constants
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
    abstract fun departmentsDao(): DepartmentsDao

    companion object {
        private var INSTANCE: ContactbookDatabase? = null

        fun getInstance(context: Context, key: String): ContactbookDatabase? {
            val pathToDatabase = context.filesDir.path + "/" + Constants.DATABASE_NAME
            if (INSTANCE == null) {
                synchronized(ContactbookDatabase::class) {
//                    val hook = object : SQLiteDatabaseHook {
//                        override fun preKey(database: SQLiteDatabase?) {}
//                        override fun postKey(database: SQLiteDatabase?) {
//                            database?.rawExecSQL("PRAGMA cipher_compatibility = 3;")
//                        }
//                    }
//                    val passphrase = SQLiteDatabase.getBytes(key.toCharArray())
//
//                    val supportFactory = SupportFactory(passphrase, hook)
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        ContactbookDatabase::class.java,
                        Constants.DATABASE_NAME
                    )
                        //.openHelperFactory(supportFactory)
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
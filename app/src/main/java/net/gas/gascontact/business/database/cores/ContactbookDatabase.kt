package net.gas.gascontact.business.database.cores

import android.content.Context
import android.widget.Toast
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.commonsware.cwac.saferoom.SafeHelperFactory
import net.gas.gascontact.business.database.daos.*
import net.gas.gascontact.business.database.entities.*
import net.gas.gascontact.utils.Var
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

        fun getInstance(context: Context, key: String) : ContactbookDatabase? {
            val pathToDatabase = context.filesDir.path + "/" + Var.DATABASE_NAME
            if (INSTANCE == null) {
                synchronized(ContactbookDatabase::class) {
                    try {
                        val factory = SafeHelperFactory(key.toCharArray(), SafeHelperFactory.POST_KEY_SQL_V3)
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            ContactbookDatabase::class.java,
                            Var.DATABASE_NAME
                        )
                            .openHelperFactory(factory)
                            .createFromFile(File(pathToDatabase))
                            .build()
                    } catch (e: Exception) {
                        Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            return INSTANCE
        }


        fun destroyInstance() {
            INSTANCE = null
        }
    }

}
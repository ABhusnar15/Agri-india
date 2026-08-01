package com.agriindia.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [UserEntity::class, OrderEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "agri_india_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    seedDefaultUsers(database.userDao())
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun seedDefaultUsers(userDao: UserDao) {
            val adminUser = UserEntity(
                uid = "admin_001",
                email = "admin@agriindia.com",
                passwordHash = "admin123",
                name = "Agri Admin",
                phone = "9876543210",
                state = "Maharashtra",
                profileImageUrl = null
            )
            val simpleAdmin = UserEntity(
                uid = "admin_002",
                email = "admin",
                passwordHash = "admin123",
                name = "Agri Admin",
                phone = "9876543210",
                state = "Maharashtra",
                profileImageUrl = null
            )
            userDao.insertUser(adminUser)
            userDao.insertUser(simpleAdmin)
        }
    }
}

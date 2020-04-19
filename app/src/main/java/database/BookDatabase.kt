package database

import androidx.room.Database
import androidx.room.RoomDatabase
import database.BookDao
import database.BookEntity

@Database(entities = [BookEntity::class],version=1)
abstract class BookDatabase: RoomDatabase(){

    abstract  fun bookDao():BookDao

}
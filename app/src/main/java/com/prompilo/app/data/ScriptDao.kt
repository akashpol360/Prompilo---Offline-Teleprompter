package com.prompilo.app.data

import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.flow.Flow

@Dao
interface ScriptDao {
    @Query("SELECT * FROM scripts ORDER BY updatedAt DESC") fun observeAll(): Flow<List<Script>>
    @Query("SELECT * FROM scripts WHERE id = :id") suspend fun get(id: Long): Script?
    @Insert suspend fun insert(script: Script): Long
    @Update suspend fun update(script: Script)
    @Delete suspend fun delete(script: Script)
    @Query("UPDATE scripts SET folderId = NULL WHERE folderId = :folderId") suspend fun moveFolderScriptsToRoot(folderId: Long)
}

@Dao
interface ScriptFolderDao {
    @Query("SELECT * FROM script_folders ORDER BY name COLLATE NOCASE") fun observeAll(): Flow<List<ScriptFolder>>
    @Insert suspend fun insert(folder: ScriptFolder): Long
    @Update suspend fun update(folder: ScriptFolder)
    @Delete suspend fun delete(folder: ScriptFolder)
}

@Dao
interface VideoDraftDao {
    @Query("SELECT * FROM video_drafts ORDER BY recordedAt DESC") fun observeAll(): Flow<List<VideoDraft>>
    @Insert suspend fun insert(draft: VideoDraft): Long
    @Update suspend fun update(draft: VideoDraft)
    @Delete suspend fun delete(draft: VideoDraft)
}

@Database(entities = [Script::class, ScriptFolder::class, VideoDraft::class], version = 5, exportSchema = false)
abstract class PrompiloDatabase : RoomDatabase() {
    abstract fun scriptDao(): ScriptDao
    abstract fun scriptFolderDao(): ScriptFolderDao
    abstract fun videoDraftDao(): VideoDraftDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                val columns = mutableSetOf<String>()
                database.query("PRAGMA table_info(`scripts`)").use { cursor ->
                    val nameColumn = cursor.getColumnIndexOrThrow("name")
                    while (cursor.moveToNext()) columns += cursor.getString(nameColumn)
                }
                if ("isFavorite" !in columns) {
                    database.execSQL("ALTER TABLE `scripts` ADD COLUMN `isFavorite` INTEGER NOT NULL DEFAULT 0")
                }
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_scripts_updatedAt` ON `scripts` (`updatedAt`)")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                val columns = mutableSetOf<String>()
                database.query("PRAGMA table_info(`scripts`)").use { cursor ->
                    val nameColumn = cursor.getColumnIndexOrThrow("name")
                    while (cursor.moveToNext()) columns += cursor.getString(nameColumn)
                }
                if ("folderId" !in columns) database.execSQL("ALTER TABLE `scripts` ADD COLUMN `folderId` INTEGER")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_scripts_folderId` ON `scripts` (`folderId`)")
                database.execSQL("CREATE TABLE IF NOT EXISTS `script_folders` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `createdAt` INTEGER NOT NULL)")
                database.execSQL("CREATE TABLE IF NOT EXISTS `video_drafts` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `uri` TEXT NOT NULL, `recordedAt` INTEGER NOT NULL, `durationMillis` INTEGER NOT NULL)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_video_drafts_recordedAt` ON `video_drafts` (`recordedAt`)")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `script_folders` ADD COLUMN `colorHex` TEXT NOT NULL DEFAULT '#6B55D9'")
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `video_drafts` ADD COLUMN `localPath` TEXT")
            }
        }

        @Volatile private var INSTANCE: PrompiloDatabase? = null
        fun get(context: android.content.Context): PrompiloDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(context.applicationContext, PrompiloDatabase::class.java, "prompilo.db")
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                .build().also { INSTANCE = it }
        }
    }
}

package com.prompilo.app.data;

import androidx.annotation.NonNull;
import androidx.room.InvalidationTracker;
import androidx.room.RoomOpenDelegate;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.SQLite;
import androidx.sqlite.SQLiteConnection;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class PrompiloDatabase_Impl extends PrompiloDatabase {
  private volatile ScriptDao _scriptDao;

  private volatile ScriptFolderDao _scriptFolderDao;

  private volatile VideoDraftDao _videoDraftDao;

  @Override
  @NonNull
  protected RoomOpenDelegate createOpenDelegate() {
    final RoomOpenDelegate _openDelegate = new RoomOpenDelegate(5, "7c8d50dbb54c754c5d58aa3f93f823f7", "7a3d6192a46b2453d63d199c7a8328c0") {
      @Override
      public void createAllTables(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `scripts` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `content` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `isFavorite` INTEGER NOT NULL, `folderId` INTEGER)");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_scripts_updatedAt` ON `scripts` (`updatedAt`)");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_scripts_folderId` ON `scripts` (`folderId`)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `script_folders` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `colorHex` TEXT NOT NULL, `createdAt` INTEGER NOT NULL)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `video_drafts` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `uri` TEXT NOT NULL, `recordedAt` INTEGER NOT NULL, `durationMillis` INTEGER NOT NULL, `localPath` TEXT)");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_video_drafts_recordedAt` ON `video_drafts` (`recordedAt`)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        SQLite.execSQL(connection, "INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '7c8d50dbb54c754c5d58aa3f93f823f7')");
      }

      @Override
      public void dropAllTables(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `scripts`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `script_folders`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `video_drafts`");
      }

      @Override
      public void onCreate(@NonNull final SQLiteConnection connection) {
      }

      @Override
      public void onOpen(@NonNull final SQLiteConnection connection) {
        internalInitInvalidationTracker(connection);
      }

      @Override
      public void onPreMigrate(@NonNull final SQLiteConnection connection) {
        DBUtil.dropFtsSyncTriggers(connection);
      }

      @Override
      public void onPostMigrate(@NonNull final SQLiteConnection connection) {
      }

      @Override
      @NonNull
      public RoomOpenDelegate.ValidationResult onValidateSchema(
          @NonNull final SQLiteConnection connection) {
        final Map<String, TableInfo.Column> _columnsScripts = new HashMap<String, TableInfo.Column>(7);
        _columnsScripts.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScripts.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScripts.put("content", new TableInfo.Column("content", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScripts.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScripts.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScripts.put("isFavorite", new TableInfo.Column("isFavorite", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScripts.put("folderId", new TableInfo.Column("folderId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysScripts = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesScripts = new HashSet<TableInfo.Index>(2);
        _indicesScripts.add(new TableInfo.Index("index_scripts_updatedAt", false, Arrays.asList("updatedAt"), Arrays.asList("ASC")));
        _indicesScripts.add(new TableInfo.Index("index_scripts_folderId", false, Arrays.asList("folderId"), Arrays.asList("ASC")));
        final TableInfo _infoScripts = new TableInfo("scripts", _columnsScripts, _foreignKeysScripts, _indicesScripts);
        final TableInfo _existingScripts = TableInfo.read(connection, "scripts");
        if (!_infoScripts.equals(_existingScripts)) {
          return new RoomOpenDelegate.ValidationResult(false, "scripts(com.prompilo.app.data.Script).\n"
                  + " Expected:\n" + _infoScripts + "\n"
                  + " Found:\n" + _existingScripts);
        }
        final Map<String, TableInfo.Column> _columnsScriptFolders = new HashMap<String, TableInfo.Column>(4);
        _columnsScriptFolders.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScriptFolders.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScriptFolders.put("colorHex", new TableInfo.Column("colorHex", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScriptFolders.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysScriptFolders = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesScriptFolders = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoScriptFolders = new TableInfo("script_folders", _columnsScriptFolders, _foreignKeysScriptFolders, _indicesScriptFolders);
        final TableInfo _existingScriptFolders = TableInfo.read(connection, "script_folders");
        if (!_infoScriptFolders.equals(_existingScriptFolders)) {
          return new RoomOpenDelegate.ValidationResult(false, "script_folders(com.prompilo.app.data.ScriptFolder).\n"
                  + " Expected:\n" + _infoScriptFolders + "\n"
                  + " Found:\n" + _existingScriptFolders);
        }
        final Map<String, TableInfo.Column> _columnsVideoDrafts = new HashMap<String, TableInfo.Column>(6);
        _columnsVideoDrafts.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVideoDrafts.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVideoDrafts.put("uri", new TableInfo.Column("uri", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVideoDrafts.put("recordedAt", new TableInfo.Column("recordedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVideoDrafts.put("durationMillis", new TableInfo.Column("durationMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVideoDrafts.put("localPath", new TableInfo.Column("localPath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysVideoDrafts = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesVideoDrafts = new HashSet<TableInfo.Index>(1);
        _indicesVideoDrafts.add(new TableInfo.Index("index_video_drafts_recordedAt", false, Arrays.asList("recordedAt"), Arrays.asList("ASC")));
        final TableInfo _infoVideoDrafts = new TableInfo("video_drafts", _columnsVideoDrafts, _foreignKeysVideoDrafts, _indicesVideoDrafts);
        final TableInfo _existingVideoDrafts = TableInfo.read(connection, "video_drafts");
        if (!_infoVideoDrafts.equals(_existingVideoDrafts)) {
          return new RoomOpenDelegate.ValidationResult(false, "video_drafts(com.prompilo.app.data.VideoDraft).\n"
                  + " Expected:\n" + _infoVideoDrafts + "\n"
                  + " Found:\n" + _existingVideoDrafts);
        }
        return new RoomOpenDelegate.ValidationResult(true, null);
      }
    };
    return _openDelegate;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final Map<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final Map<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "scripts", "script_folders", "video_drafts");
  }

  @Override
  public void clearAllTables() {
    super.performClear(false, "scripts", "script_folders", "video_drafts");
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final Map<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(ScriptDao.class, ScriptDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ScriptFolderDao.class, ScriptFolderDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(VideoDraftDao.class, VideoDraftDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final Set<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public ScriptDao scriptDao() {
    if (_scriptDao != null) {
      return _scriptDao;
    } else {
      synchronized(this) {
        if(_scriptDao == null) {
          _scriptDao = new ScriptDao_Impl(this);
        }
        return _scriptDao;
      }
    }
  }

  @Override
  public ScriptFolderDao scriptFolderDao() {
    if (_scriptFolderDao != null) {
      return _scriptFolderDao;
    } else {
      synchronized(this) {
        if(_scriptFolderDao == null) {
          _scriptFolderDao = new ScriptFolderDao_Impl(this);
        }
        return _scriptFolderDao;
      }
    }
  }

  @Override
  public VideoDraftDao videoDraftDao() {
    if (_videoDraftDao != null) {
      return _videoDraftDao;
    } else {
      synchronized(this) {
        if(_videoDraftDao == null) {
          _videoDraftDao = new VideoDraftDao_Impl(this);
        }
        return _videoDraftDao;
      }
    }
  }
}

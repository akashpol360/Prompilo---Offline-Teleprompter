package com.prompilo.app.data;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.coroutines.FlowUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import java.lang.Class;
import java.lang.Long;
import java.lang.NullPointerException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class ScriptFolderDao_Impl implements ScriptFolderDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<ScriptFolder> __insertAdapterOfScriptFolder;

  private final EntityDeleteOrUpdateAdapter<ScriptFolder> __deleteAdapterOfScriptFolder;

  private final EntityDeleteOrUpdateAdapter<ScriptFolder> __updateAdapterOfScriptFolder;

  public ScriptFolderDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfScriptFolder = new EntityInsertAdapter<ScriptFolder>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `script_folders` (`id`,`name`,`colorHex`,`createdAt`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final ScriptFolder entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getName());
        }
        if (entity.getColorHex() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getColorHex());
        }
        statement.bindLong(4, entity.getCreatedAt());
      }
    };
    this.__deleteAdapterOfScriptFolder = new EntityDeleteOrUpdateAdapter<ScriptFolder>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `script_folders` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final ScriptFolder entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfScriptFolder = new EntityDeleteOrUpdateAdapter<ScriptFolder>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `script_folders` SET `id` = ?,`name` = ?,`colorHex` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final ScriptFolder entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getName());
        }
        if (entity.getColorHex() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getColorHex());
        }
        statement.bindLong(4, entity.getCreatedAt());
        statement.bindLong(5, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final ScriptFolder folder, final Continuation<? super Long> $completion) {
    if (folder == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      return __insertAdapterOfScriptFolder.insertAndReturnId(_connection, folder);
    }, $completion);
  }

  @Override
  public Object delete(final ScriptFolder folder, final Continuation<? super Unit> $completion) {
    if (folder == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __deleteAdapterOfScriptFolder.handle(_connection, folder);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object update(final ScriptFolder folder, final Continuation<? super Unit> $completion) {
    if (folder == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __updateAdapterOfScriptFolder.handle(_connection, folder);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Flow<List<ScriptFolder>> observeAll() {
    final String _sql = "SELECT * FROM script_folders ORDER BY name COLLATE NOCASE";
    return FlowUtil.createFlow(__db, false, new String[] {"script_folders"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "name");
        final int _columnIndexOfColorHex = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "colorHex");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final List<ScriptFolder> _result = new ArrayList<ScriptFolder>();
        while (_stmt.step()) {
          final ScriptFolder _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpName;
          if (_stmt.isNull(_columnIndexOfName)) {
            _tmpName = null;
          } else {
            _tmpName = _stmt.getText(_columnIndexOfName);
          }
          final String _tmpColorHex;
          if (_stmt.isNull(_columnIndexOfColorHex)) {
            _tmpColorHex = null;
          } else {
            _tmpColorHex = _stmt.getText(_columnIndexOfColorHex);
          }
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          _item = new ScriptFolder(_tmpId,_tmpName,_tmpColorHex,_tmpCreatedAt);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}

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
public final class ScriptDao_Impl implements ScriptDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<Script> __insertAdapterOfScript;

  private final EntityDeleteOrUpdateAdapter<Script> __deleteAdapterOfScript;

  private final EntityDeleteOrUpdateAdapter<Script> __updateAdapterOfScript;

  public ScriptDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfScript = new EntityInsertAdapter<Script>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `scripts` (`id`,`title`,`content`,`createdAt`,`updatedAt`,`isFavorite`,`folderId`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, @NonNull final Script entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getTitle() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getTitle());
        }
        if (entity.getContent() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getContent());
        }
        statement.bindLong(4, entity.getCreatedAt());
        statement.bindLong(5, entity.getUpdatedAt());
        final int _tmp = entity.isFavorite() ? 1 : 0;
        statement.bindLong(6, _tmp);
        if (entity.getFolderId() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getFolderId());
        }
      }
    };
    this.__deleteAdapterOfScript = new EntityDeleteOrUpdateAdapter<Script>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `scripts` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, @NonNull final Script entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfScript = new EntityDeleteOrUpdateAdapter<Script>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `scripts` SET `id` = ?,`title` = ?,`content` = ?,`createdAt` = ?,`updatedAt` = ?,`isFavorite` = ?,`folderId` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, @NonNull final Script entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getTitle() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getTitle());
        }
        if (entity.getContent() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getContent());
        }
        statement.bindLong(4, entity.getCreatedAt());
        statement.bindLong(5, entity.getUpdatedAt());
        final int _tmp = entity.isFavorite() ? 1 : 0;
        statement.bindLong(6, _tmp);
        if (entity.getFolderId() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getFolderId());
        }
        statement.bindLong(8, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final Script script, final Continuation<? super Long> $completion) {
    if (script == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      return __insertAdapterOfScript.insertAndReturnId(_connection, script);
    }, $completion);
  }

  @Override
  public Object delete(final Script script, final Continuation<? super Unit> $completion) {
    if (script == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __deleteAdapterOfScript.handle(_connection, script);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object update(final Script script, final Continuation<? super Unit> $completion) {
    if (script == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __updateAdapterOfScript.handle(_connection, script);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Flow<List<Script>> observeAll() {
    final String _sql = "SELECT * FROM scripts ORDER BY updatedAt DESC";
    return FlowUtil.createFlow(__db, false, new String[] {"scripts"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfContent = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "content");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfUpdatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "updatedAt");
        final int _columnIndexOfIsFavorite = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isFavorite");
        final int _columnIndexOfFolderId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "folderId");
        final List<Script> _result = new ArrayList<Script>();
        while (_stmt.step()) {
          final Script _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpContent;
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null;
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent);
          }
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          final long _tmpUpdatedAt;
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt);
          final boolean _tmpIsFavorite;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsFavorite));
          _tmpIsFavorite = _tmp != 0;
          final Long _tmpFolderId;
          if (_stmt.isNull(_columnIndexOfFolderId)) {
            _tmpFolderId = null;
          } else {
            _tmpFolderId = _stmt.getLong(_columnIndexOfFolderId);
          }
          _item = new Script(_tmpId,_tmpTitle,_tmpContent,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsFavorite,_tmpFolderId);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Object get(final long id, final Continuation<? super Script> $completion) {
    final String _sql = "SELECT * FROM scripts WHERE id = ?";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfContent = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "content");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfUpdatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "updatedAt");
        final int _columnIndexOfIsFavorite = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isFavorite");
        final int _columnIndexOfFolderId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "folderId");
        final Script _result;
        if (_stmt.step()) {
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpContent;
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null;
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent);
          }
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          final long _tmpUpdatedAt;
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt);
          final boolean _tmpIsFavorite;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsFavorite));
          _tmpIsFavorite = _tmp != 0;
          final Long _tmpFolderId;
          if (_stmt.isNull(_columnIndexOfFolderId)) {
            _tmpFolderId = null;
          } else {
            _tmpFolderId = _stmt.getLong(_columnIndexOfFolderId);
          }
          _result = new Script(_tmpId,_tmpTitle,_tmpContent,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsFavorite,_tmpFolderId);
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object moveFolderScriptsToRoot(final long folderId,
      final Continuation<? super Unit> $completion) {
    final String _sql = "UPDATE scripts SET folderId = NULL WHERE folderId = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, folderId);
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}

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
public final class VideoDraftDao_Impl implements VideoDraftDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<VideoDraft> __insertAdapterOfVideoDraft;

  private final EntityDeleteOrUpdateAdapter<VideoDraft> __deleteAdapterOfVideoDraft;

  private final EntityDeleteOrUpdateAdapter<VideoDraft> __updateAdapterOfVideoDraft;

  public VideoDraftDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfVideoDraft = new EntityInsertAdapter<VideoDraft>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `video_drafts` (`id`,`title`,`uri`,`recordedAt`,`durationMillis`,`localPath`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final VideoDraft entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getTitle() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getTitle());
        }
        if (entity.getUri() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getUri());
        }
        statement.bindLong(4, entity.getRecordedAt());
        statement.bindLong(5, entity.getDurationMillis());
        if (entity.getLocalPath() == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.getLocalPath());
        }
      }
    };
    this.__deleteAdapterOfVideoDraft = new EntityDeleteOrUpdateAdapter<VideoDraft>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `video_drafts` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final VideoDraft entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfVideoDraft = new EntityDeleteOrUpdateAdapter<VideoDraft>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `video_drafts` SET `id` = ?,`title` = ?,`uri` = ?,`recordedAt` = ?,`durationMillis` = ?,`localPath` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final VideoDraft entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getTitle() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getTitle());
        }
        if (entity.getUri() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getUri());
        }
        statement.bindLong(4, entity.getRecordedAt());
        statement.bindLong(5, entity.getDurationMillis());
        if (entity.getLocalPath() == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.getLocalPath());
        }
        statement.bindLong(7, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final VideoDraft draft, final Continuation<? super Long> $completion) {
    if (draft == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      return __insertAdapterOfVideoDraft.insertAndReturnId(_connection, draft);
    }, $completion);
  }

  @Override
  public Object delete(final VideoDraft draft, final Continuation<? super Unit> $completion) {
    if (draft == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __deleteAdapterOfVideoDraft.handle(_connection, draft);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object update(final VideoDraft draft, final Continuation<? super Unit> $completion) {
    if (draft == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __updateAdapterOfVideoDraft.handle(_connection, draft);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Flow<List<VideoDraft>> observeAll() {
    final String _sql = "SELECT * FROM video_drafts ORDER BY recordedAt DESC";
    return FlowUtil.createFlow(__db, false, new String[] {"video_drafts"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfUri = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "uri");
        final int _columnIndexOfRecordedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "recordedAt");
        final int _columnIndexOfDurationMillis = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "durationMillis");
        final int _columnIndexOfLocalPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localPath");
        final List<VideoDraft> _result = new ArrayList<VideoDraft>();
        while (_stmt.step()) {
          final VideoDraft _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpUri;
          if (_stmt.isNull(_columnIndexOfUri)) {
            _tmpUri = null;
          } else {
            _tmpUri = _stmt.getText(_columnIndexOfUri);
          }
          final long _tmpRecordedAt;
          _tmpRecordedAt = _stmt.getLong(_columnIndexOfRecordedAt);
          final long _tmpDurationMillis;
          _tmpDurationMillis = _stmt.getLong(_columnIndexOfDurationMillis);
          final String _tmpLocalPath;
          if (_stmt.isNull(_columnIndexOfLocalPath)) {
            _tmpLocalPath = null;
          } else {
            _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath);
          }
          _item = new VideoDraft(_tmpId,_tmpTitle,_tmpUri,_tmpRecordedAt,_tmpDurationMillis,_tmpLocalPath);
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

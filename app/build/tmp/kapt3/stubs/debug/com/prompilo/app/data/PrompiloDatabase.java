package com.prompilo.app.data;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\'\u0018\u0000 \n2\u00020\u0001:\u0001\nB\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H&J\b\u0010\u0006\u001a\u00020\u0007H&J\b\u0010\b\u001a\u00020\tH&\u00a8\u0006\u000b"}, d2 = {"Lcom/prompilo/app/data/PrompiloDatabase;", "Landroidx/room/RoomDatabase;", "<init>", "()V", "scriptDao", "Lcom/prompilo/app/data/ScriptDao;", "scriptFolderDao", "Lcom/prompilo/app/data/ScriptFolderDao;", "videoDraftDao", "Lcom/prompilo/app/data/VideoDraftDao;", "Companion", "app_debug"})
@androidx.room.Database(entities = {com.prompilo.app.data.Script.class, com.prompilo.app.data.ScriptFolder.class, com.prompilo.app.data.VideoDraft.class}, version = 5, exportSchema = false)
public abstract class PrompiloDatabase extends androidx.room.RoomDatabase {
    @org.jetbrains.annotations.NotNull()
    private static final androidx.room.migration.Migration MIGRATION_1_2 = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.room.migration.Migration MIGRATION_2_3 = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.room.migration.Migration MIGRATION_3_4 = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.room.migration.Migration MIGRATION_4_5 = null;
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private static volatile com.prompilo.app.data.PrompiloDatabase INSTANCE;
    @org.jetbrains.annotations.NotNull()
    public static final com.prompilo.app.data.PrompiloDatabase.Companion Companion = null;
    
    public PrompiloDatabase() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.prompilo.app.data.ScriptDao scriptDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.prompilo.app.data.ScriptFolderDao scriptFolderDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.prompilo.app.data.VideoDraftDao videoDraftDao();
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\f\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u000eR\u0010\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0006R\u0010\u0010\u0007\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0006R\u0010\u0010\b\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0006R\u0010\u0010\t\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0006R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000f"}, d2 = {"Lcom/prompilo/app/data/PrompiloDatabase$Companion;", "", "<init>", "()V", "MIGRATION_1_2", "Landroidx/room/migration/Migration;", "Landroidx/room/migration/Migration;", "MIGRATION_2_3", "MIGRATION_3_4", "MIGRATION_4_5", "INSTANCE", "Lcom/prompilo/app/data/PrompiloDatabase;", "get", "context", "Landroid/content/Context;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.prompilo.app.data.PrompiloDatabase get(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
            return null;
        }
    }
}
package com.prompilo.app.data;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\bg\u0018\u00002\u00020\u0001J\u0014\u0010\u0002\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H\'J\u0016\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\tJ\u0016\u0010\n\u001a\u00020\u000b2\u0006\u0010\b\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\tJ\u0016\u0010\f\u001a\u00020\u000b2\u0006\u0010\b\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\t\u00a8\u0006\r\u00c0\u0006\u0003"}, d2 = {"Lcom/prompilo/app/data/ScriptFolderDao;", "", "observeAll", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/prompilo/app/data/ScriptFolder;", "insert", "", "folder", "(Lcom/prompilo/app/data/ScriptFolder;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "update", "", "delete", "app_release"})
@androidx.room.Dao()
public abstract interface ScriptFolderDao {
    
    @androidx.room.Query(value = "SELECT * FROM script_folders ORDER BY name COLLATE NOCASE")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.prompilo.app.data.ScriptFolder>> observeAll();
    
    @androidx.room.Insert()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.prompilo.app.data.ScriptFolder folder, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object update(@org.jetbrains.annotations.NotNull()
    com.prompilo.app.data.ScriptFolder folder, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object delete(@org.jetbrains.annotations.NotNull()
    com.prompilo.app.data.ScriptFolder folder, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}
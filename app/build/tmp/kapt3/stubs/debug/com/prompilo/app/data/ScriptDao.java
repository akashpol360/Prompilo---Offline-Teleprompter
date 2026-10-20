package com.prompilo.app.data;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0004\bg\u0018\u00002\u00020\u0001J\u0014\u0010\u0002\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H\'J\u0018\u0010\u0006\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0007\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\tJ\u0016\u0010\n\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\fJ\u0016\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000b\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\fJ\u0016\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u000b\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\fJ\u0016\u0010\u0010\u001a\u00020\u000e2\u0006\u0010\u0011\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\t\u00a8\u0006\u0012\u00c0\u0006\u0003"}, d2 = {"Lcom/prompilo/app/data/ScriptDao;", "", "observeAll", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/prompilo/app/data/Script;", "get", "id", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insert", "script", "(Lcom/prompilo/app/data/Script;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "update", "", "delete", "moveFolderScriptsToRoot", "folderId", "app_debug"})
@androidx.room.Dao()
public abstract interface ScriptDao {
    
    @androidx.room.Query(value = "SELECT * FROM scripts ORDER BY updatedAt DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.prompilo.app.data.Script>> observeAll();
    
    @androidx.room.Query(value = "SELECT * FROM scripts WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object get(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.prompilo.app.data.Script> $completion);
    
    @androidx.room.Insert()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.prompilo.app.data.Script script, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object update(@org.jetbrains.annotations.NotNull()
    com.prompilo.app.data.Script script, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object delete(@org.jetbrains.annotations.NotNull()
    com.prompilo.app.data.Script script, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "UPDATE scripts SET folderId = NULL WHERE folderId = :folderId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object moveFolderScriptsToRoot(long folderId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}
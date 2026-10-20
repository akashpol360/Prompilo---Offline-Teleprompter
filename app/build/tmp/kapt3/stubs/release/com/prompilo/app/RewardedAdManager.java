package com.prompilo.app;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\b\b\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u000f\u001a\u00020\u000eJ*\u0010\u0010\u001a\u00020\u000e2\u0006\u0010\u0011\u001a\u00020\f2\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u000e0\r2\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u000e0\rJ8\u0010\u0014\u001a\u00020\u000e2\u0006\u0010\u0011\u001a\u00020\f2\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u000e0\r2\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u000e0\r2\n\b\u0002\u0010\u0015\u001a\u0004\u0018\u00010\u0007H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R.\u0010\n\u001a\"\u0012\u0004\u0012\u00020\f\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\r\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\r\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"Lcom/prompilo/app/RewardedAdManager;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "cachedAd", "Lcom/google/android/gms/ads/rewarded/RewardedAd;", "loading", "", "pendingShow", "Lkotlin/Triple;", "Landroid/app/Activity;", "Lkotlin/Function0;", "", "preload", "show", "activity", "onReward", "onUnavailable", "present", "suppliedAd", "app_release"})
public final class RewardedAdManager {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.Nullable()
    private com.google.android.gms.ads.rewarded.RewardedAd cachedAd;
    private boolean loading = false;
    @org.jetbrains.annotations.Nullable()
    private kotlin.Triple<? extends android.app.Activity, ? extends kotlin.jvm.functions.Function0<kotlin.Unit>, ? extends kotlin.jvm.functions.Function0<kotlin.Unit>> pendingShow;
    
    public RewardedAdManager(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    public final void preload() {
    }
    
    public final void show(@org.jetbrains.annotations.NotNull()
    android.app.Activity activity, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onReward, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onUnavailable) {
    }
    
    private final void present(android.app.Activity activity, kotlin.jvm.functions.Function0<kotlin.Unit> onReward, kotlin.jvm.functions.Function0<kotlin.Unit> onUnavailable, com.google.android.gms.ads.rewarded.RewardedAd suppliedAd) {
    }
}
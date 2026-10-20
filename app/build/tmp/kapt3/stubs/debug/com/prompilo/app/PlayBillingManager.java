package com.prompilo.app;

/**
 * Play Store products supply the localized prices; product IDs must exist in Play Console.
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\f\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u001d\u001a\u00020\u001eJ\b\u0010!\u001a\u00020\u001eH\u0002J\b\u0010\"\u001a\u00020\u001eH\u0002J\u000e\u0010#\u001a\u00020\u001e2\u0006\u0010$\u001a\u00020\bJ\u0010\u0010%\u001a\u00020\u001e2\b\b\u0002\u0010&\u001a\u00020\u0011J\u0016\u0010\'\u001a\u00020\u001e2\f\u0010(\u001a\b\u0012\u0004\u0012\u00020*0)H\u0002J\u0010\u0010+\u001a\u00020\u00112\u0006\u0010,\u001a\u00020\bH\u0002J\u0006\u0010-\u001a\u00020\u001eR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000RC\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\t0\u00072\u0012\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\t0\u00078F@BX\u0086\u008e\u0002\u00a2\u0006\u0012\n\u0004\b\u000f\u0010\u0010\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR+\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u0006\u001a\u00020\u00118F@BX\u0086\u008e\u0002\u00a2\u0006\u0012\n\u0004\b\u0016\u0010\u0010\u001a\u0004\b\u0012\u0010\u0013\"\u0004\b\u0014\u0010\u0015R/\u0010\u0017\u001a\u0004\u0018\u00010\b2\b\u0010\u0006\u001a\u0004\u0018\u00010\b8F@BX\u0086\u008e\u0002\u00a2\u0006\u0012\n\u0004\b\u001c\u0010\u0010\u001a\u0004\b\u0018\u0010\u0019\"\u0004\b\u001a\u0010\u001bR\u000e\u0010\u001f\u001a\u00020 X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006."}, d2 = {"Lcom/prompilo/app/PlayBillingManager;", "", "activity", "Landroid/app/Activity;", "<init>", "(Landroid/app/Activity;)V", "<set-?>", "", "", "Lcom/android/billingclient/api/ProductDetails;", "products", "getProducts", "()Ljava/util/Map;", "setProducts", "(Ljava/util/Map;)V", "products$delegate", "Landroidx/compose/runtime/MutableState;", "", "isPro", "()Z", "setPro", "(Z)V", "isPro$delegate", "notice", "getNotice", "()Ljava/lang/String;", "setNotice", "(Ljava/lang/String;)V", "notice$delegate", "dismissNotice", "", "client", "Lcom/android/billingclient/api/BillingClient;", "connect", "queryProducts", "buy", "productId", "restore", "silent", "processPurchases", "purchases", "", "Lcom/android/billingclient/api/Purchase;", "isProProduct", "id", "close", "app_debug"})
public final class PlayBillingManager {
    @org.jetbrains.annotations.NotNull()
    private final android.app.Activity activity = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.compose.runtime.MutableState products$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.compose.runtime.MutableState isPro$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.compose.runtime.MutableState notice$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final com.android.billingclient.api.BillingClient client = null;
    
    public PlayBillingManager(@org.jetbrains.annotations.NotNull()
    android.app.Activity activity) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Map<java.lang.String, com.android.billingclient.api.ProductDetails> getProducts() {
        return null;
    }
    
    private final void setProducts(java.util.Map<java.lang.String, com.android.billingclient.api.ProductDetails> p0) {
    }
    
    public final boolean isPro() {
        return false;
    }
    
    private final void setPro(boolean p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getNotice() {
        return null;
    }
    
    private final void setNotice(java.lang.String p0) {
    }
    
    public final void dismissNotice() {
    }
    
    private final void connect() {
    }
    
    private final void queryProducts() {
    }
    
    public final void buy(@org.jetbrains.annotations.NotNull()
    java.lang.String productId) {
    }
    
    public final void restore(boolean silent) {
    }
    
    private final void processPurchases(java.util.List<? extends com.android.billingclient.api.Purchase> purchases) {
    }
    
    private final boolean isProProduct(java.lang.String id) {
        return false;
    }
    
    public final void close() {
    }
}
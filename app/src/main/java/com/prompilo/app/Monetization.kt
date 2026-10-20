package com.prompilo.app

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

internal const val YEARLY_PRODUCT_ID = "pro_yearly"
internal const val MONTHLY_PRODUCT_ID = "pro_monthly"
internal const val LIFETIME_PRODUCT_ID = "pro_lifetime"
private const val REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"

/** Play Store products supply the localized prices; product IDs must exist in Play Console. */
internal class PlayBillingManager(private val activity: Activity) {
    var products by mutableStateOf<Map<String, ProductDetails>>(emptyMap())
        private set
    var isPro by mutableStateOf(false)
        private set
    var notice by mutableStateOf<String?>(null)
        private set

    fun dismissNotice() { notice = null }

    private val client: BillingClient = BillingClient.newBuilder(activity)
        .setListener { result, purchases ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK && !purchases.isNullOrEmpty()) {
                processPurchases(purchases)
                notice = if (purchases.any { it.purchaseState == Purchase.PurchaseState.PENDING })
                    "Your purchase is pending. Pro unlocks after Google Play confirms payment."
                else "Prompilo Pro is ready. Ads are now disabled."
            } else if (result.responseCode != BillingClient.BillingResponseCode.USER_CANCELED) {
                notice = result.debugMessage.ifBlank { "Google Play could not complete the purchase." }
            }
        }
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        .build()

    init { connect() }

    private fun connect() {
        if (client.isReady) { queryProducts(); restore(silent = true); return }
        client.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: com.android.billingclient.api.BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryProducts()
                    restore(silent = true)
                } else notice = "Google Play billing is unavailable right now."
            }
            override fun onBillingServiceDisconnected() { /* Reconnect on the next billing action. */ }
        })
    }

    private fun queryProducts() {
        val params = QueryProductDetailsParams.newBuilder().setProductList(listOf(
            QueryProductDetailsParams.Product.newBuilder().setProductId(YEARLY_PRODUCT_ID).setProductType(BillingClient.ProductType.SUBS).build(),
            QueryProductDetailsParams.Product.newBuilder().setProductId(MONTHLY_PRODUCT_ID).setProductType(BillingClient.ProductType.SUBS).build(),
            QueryProductDetailsParams.Product.newBuilder().setProductId(LIFETIME_PRODUCT_ID).setProductType(BillingClient.ProductType.INAPP).build()
        )).build()
        client.queryProductDetailsAsync(params) { result, response ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                products = response.productDetailsList.associateBy { it.productId }
            }
        }
    }

    fun buy(productId: String) {
        if (!client.isReady) { connect(); notice = "Connecting to Google Play. Please try again in a moment."; return }
        val details = products[productId]
        if (details == null) { notice = "This plan is not available yet. Add its product to Google Play Console and try again."; queryProducts(); return }
        val paramsBuilder = BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(details)
        val offerToken = details.subscriptionOfferDetails?.firstOrNull()?.offerToken
        if (offerToken != null) paramsBuilder.setOfferToken(offerToken)
        val flow = BillingFlowParams.newBuilder().setProductDetailsParamsList(listOf(paramsBuilder.build())).build()
        val result = client.launchBillingFlow(activity, flow)
        if (result.responseCode != BillingClient.BillingResponseCode.OK) notice = result.debugMessage
    }

    fun restore(silent: Boolean = false) {
        if (!client.isReady) { connect(); if (!silent) notice = "Connecting to Google Play to restore purchases."; return }
        val remaining = intArrayOf(2)
        var found = false
        fun completeIfReady() {
            remaining[0]--
            if (remaining[0] == 0) {
                isPro = found
                if (!silent) notice = if (found) "Your Prompilo Pro purchase has been restored." else "No active Prompilo Pro purchase was found for this Google Play account."
            }
        }
        listOf(BillingClient.ProductType.SUBS, BillingClient.ProductType.INAPP).forEach { type ->
            client.queryPurchasesAsync(QueryPurchasesParams.newBuilder().setProductType(type).build()) { result, purchases ->
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    if (purchases.any { purchase -> purchase.purchaseState == Purchase.PurchaseState.PURCHASED && purchase.products.any(::isProProduct) }) found = true
                    processPurchases(purchases)
                }
                completeIfReady()
            }
        }
    }

    private fun processPurchases(purchases: List<Purchase>) {
        val purchased = purchases.filter { it.purchaseState == Purchase.PurchaseState.PURCHASED }
        if (purchased.isNotEmpty()) isPro = purchased.any { purchase -> purchase.products.any(::isProProduct) } || isPro
        purchased.filter { !it.isAcknowledged }.forEach { purchase ->
            client.acknowledgePurchase(AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()) { }
        }
    }

    private fun isProProduct(id: String) = id == YEARLY_PRODUCT_ID || id == MONTHLY_PRODUCT_ID || id == LIFETIME_PRODUCT_ID
    fun close() { if (client.isReady) client.endConnection() }
}

internal class RewardedAdManager(private val context: Context) {
    private var cachedAd: RewardedAd? = null
    private var loading = false
    private var pendingShow: Triple<Activity, () -> Unit, () -> Unit>? = null

    fun preload() {
        if (loading || cachedAd != null) return
        loading = true
        RewardedAd.load(context, REWARDED_AD_UNIT_ID, AdRequest.Builder().build(), object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) {
                cachedAd = ad; loading = false
                pendingShow?.let { (activity, reward, unavailable) -> pendingShow = null; present(activity, reward, unavailable) }
            }
            override fun onAdFailedToLoad(error: LoadAdError) { cachedAd = null; loading = false; pendingShow?.third?.invoke(); pendingShow = null }
        })
    }

    fun show(activity: Activity, onReward: () -> Unit, onUnavailable: () -> Unit) {
        val ad = cachedAd
        if (ad == null) {
            if (pendingShow != null) { onUnavailable(); return }
            pendingShow = Triple(activity, onReward, onUnavailable)
            preload()
            return
        }
        cachedAd = null
        present(activity, onReward, onUnavailable, ad)
    }

    private fun present(activity: Activity, onReward: () -> Unit, onUnavailable: () -> Unit, suppliedAd: RewardedAd? = null) {
        val ad = suppliedAd ?: cachedAd ?: run { onUnavailable(); return }
        cachedAd = null
        var rewarded = false
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() { preload(); if (!rewarded) onUnavailable() }
            override fun onAdFailedToShowFullScreenContent(error: com.google.android.gms.ads.AdError) { preload(); onUnavailable() }
        }
        ad.show(activity) {
            if (!rewarded) { rewarded = true; onReward() }
        }
    }
}

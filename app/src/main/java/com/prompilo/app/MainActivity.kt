package com.prompilo.app

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.content.res.Configuration
import android.os.Environment
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.ViewGroup
import android.widget.VideoView
import android.widget.MediaController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.prompilo.app.data.PrompiloDatabase
import com.prompilo.app.data.Script
import com.prompilo.app.data.ScriptFolder
import com.prompilo.app.data.VideoDraft
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date
import java.util.Locale
import java.io.File
import java.util.UUID
import java.util.concurrent.Executor
import com.google.android.gms.ads.MobileAds

private val Ink = Color(0xFF211A0F)
private val Accent = Color(0xFFB96E00)
private val Canvas = Color(0xFFFFF9EF)
private val FolderPalette = listOf("#B96E00", "#D64A4A", "#E99700", "#158A77", "#3D70A5", "#8B5A9F", "#CF6342", "#607D8B")
private fun folderColor(hex: String): Color = runCatching { Color(android.graphics.Color.parseColor(hex)) }.getOrDefault(Accent)

class MainActivity : ComponentActivity() {
    private val db by lazy { PrompiloDatabase.get(this) }
    private val billing by lazy { PlayBillingManager(this) }
    private val rewardedAds by lazy { RewardedAdManager(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(255,249,239)
        window.navigationBarColor = android.graphics.Color.rgb(255,249,239)
        MobileAds.initialize(this) { runOnUiThread { rewardedAds.preload() } }
        setContent { PrompiloTheme { PrompiloApp(db, billing, rewardedAds) } }
    }
    override fun onDestroy() { billing.close(); super.onDestroy() }
}

@Composable
private fun PrompiloTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = lightColorScheme(primary = Accent, onPrimary = Color.White, primaryContainer = Color(0xFFFFE0A3), onPrimaryContainer = Ink, secondary = Color(0xFFE99700), onSecondary = Ink, tertiary = Color(0xFFD64A4A), onTertiary = Color.White, background = Canvas, surface = Color(0xFFFFFDF8), onSurface = Ink, surfaceVariant = Color(0xFFF4EBDD), onSurfaceVariant = Color(0xFF6F6250), outline = Color(0xFFD8CBB7)), content = content)
}

private enum class Screen { HOME, EDITOR, PROMPTER, PLAYER, MORE, PRO }

@Composable
private fun PrompiloApp(db: PrompiloDatabase, billing: PlayBillingManager, rewardedAds: RewardedAdManager) {
    val context = LocalContext.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()
    val scripts by db.scriptDao().observeAll().collectAsState(initial = emptyList())
    val folders by db.scriptFolderDao().observeAll().collectAsState(initial = emptyList())
    val drafts by db.videoDraftDao().observeAll().collectAsState(initial = emptyList())
    var screen by remember { mutableStateOf(Screen.HOME) }
    var editing by remember { mutableStateOf<Script?>(null) }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var current by remember { mutableStateOf<Script?>(null) }
    var currentDraft by remember { mutableStateOf<VideoDraft?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    var deleteTarget by remember { mutableStateOf<Script?>(null) }
    var editorFolderId by remember { mutableStateOf<Long?>(null) }
    var selectedFolderId by remember { mutableStateOf<Long?>(null) }
    var showAddMenu by remember { mutableStateOf(false) }
    var showFolderDialog by remember { mutableStateOf(false) }
    var folderName by remember { mutableStateOf("") }
    var selectedFolderColor by remember { mutableStateOf(FolderPalette.first()) }
    var folderColorTarget by remember { mutableStateOf<ScriptFolder?>(null) }
    var deleteFolderTarget by remember { mutableStateOf<ScriptFolder?>(null) }
    var showDrafts by remember { mutableStateOf(false) }
    var deleteDraftTarget by remember { mutableStateOf<VideoDraft?>(null) }
    var importError by remember { mutableStateOf<String?>(null) }
    val prefs = remember(context) { context.getSharedPreferences("prompilo_entitlements", android.content.Context.MODE_PRIVATE) }
    var recordingCredits by remember { mutableIntStateOf(prefs.getInt("recording_credits", 0)) }
    fun addRecordingCredit() { recordingCredits += 1; prefs.edit().putInt("recording_credits", recordingCredits).apply() }
    fun consumeRecordingCredit() { recordingCredits = (recordingCredits - 1).coerceAtLeast(0); prefs.edit().putInt("recording_credits", recordingCredits).apply() }
    var pendingRestore by remember { mutableStateOf<VideoDraft?>(null) }
    val restoreDraft: (VideoDraft) -> Unit = { draft ->
        scope.launch {
            val restored = withContext(Dispatchers.IO) { copyDraftToGallery(context, draft) }
            if (restored == null) message = "Could not save this video to Gallery. The video draft backup is still available."
            else {
                val updated = draft.copy(uri = restored.toString())
                db.videoDraftDao().update(updated)
                currentDraft = updated
                message = "Video saved to Movies/Prompilo."
            }
        }
    }
    val legacyStoragePermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        val draft = pendingRestore
        pendingRestore = null
        if (granted && draft != null) restoreDraft(draft)
        else if (!granted) message = "Storage permission is needed to restore videos on this Android version."
    }
    val startRestore: (VideoDraft) -> Unit = { draft ->
        if (draft.localPath.isNullOrBlank() || !File(draft.localPath).isFile) {
            message = "A saved backup is not available for this video. New recordings will include a recoverable draft backup."
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            pendingRestore = draft
            legacyStoragePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else restoreDraft(draft)
    }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) scope.launch {
            try {
                val imported = withContext(Dispatchers.IO) {
                    val name = context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor -> if (cursor.moveToFirst()) cursor.getString(0) else null }
                    val text = context.contentResolver.openInputStream(uri)?.bufferedReader(Charsets.UTF_8)?.use { it.readText() } ?: error("The selected file could not be opened.")
                    (name ?: "Imported script") to text
                }
                editing = null
                title = imported.first.substringBeforeLast('.', imported.first).ifBlank { "Imported script" }
                content = imported.second
                editorFolderId = selectedFolderId
                screen = Screen.EDITOR
            } catch (error: Exception) { importError = "Could not import this file. Choose a readable text file. ${error.localizedMessage.orEmpty()}" }
        }
    }
    when (screen) {
        Screen.HOME -> HomeScreen(scripts, folders, drafts, selectedFolderId, showDrafts,
            onFolder = { selectedFolderId = it; showDrafts = false }, onBackFolder = { selectedFolderId = null }, onDrafts = { showDrafts = it }, onPlus = { showAddMenu = true },
            onOpen = { current = it; title = it.title; content = it.content; editing = it; editorFolderId = it.folderId; screen = Screen.EDITOR },
            onDraft = { draft ->
                currentDraft = draft
                screen = Screen.PLAYER
                if (draft.localPath.isNullOrBlank()) scope.launch {
                    val path = withContext(Dispatchers.IO) { copyGalleryVideoToDraft(context, Uri.parse(draft.uri)) }
                    if (path != null) { val updated = draft.copy(localPath = path); db.videoDraftDao().update(updated); currentDraft = updated }
                }
            }, onDelete = { deleteTarget = it }, onDeleteDraft = { deleteDraftTarget = it }, onDeleteFolder = { deleteFolderTarget = it }, onChangeFolderColor = { folderColorTarget = it }, onMore = { screen = Screen.MORE })
        Screen.EDITOR -> EditorScreen(title, { title = it }, content, { content = it }, editing != null, onBack = { screen = Screen.HOME }, onSave = {
            if (title.isBlank() || content.isBlank()) message = "Add a title and script before saving."
            else scope.launch {
                val now = System.currentTimeMillis(); val prior = editing
                if (prior == null) {
                    val cleanTitle = title.trim(); val id = db.scriptDao().insert(Script(title = cleanTitle, content = content, createdAt = now, updatedAt = now, folderId = editorFolderId))
                    current = Script(id = id, title = cleanTitle, content = content, createdAt = now, updatedAt = now, folderId = editorFolderId)
                } else { val updated = prior.copy(title = title.trim(), content = content, updatedAt = now); db.scriptDao().update(updated); current = updated }
                screen = Screen.HOME
            }
        }, onStart = {
            if (title.isBlank() || content.isBlank()) message = "Add a title and script before starting."
            else scope.launch {
                val now = System.currentTimeMillis(); val prior = editing
                current = if (prior == null) {
                    val cleanTitle = title.trim(); val id = db.scriptDao().insert(Script(title = cleanTitle, content = content, createdAt = now, updatedAt = now, folderId = editorFolderId))
                    Script(id = id, title = cleanTitle, content = content, createdAt = now, updatedAt = now, folderId = editorFolderId)
                } else prior.copy(title = title.trim(), content = content, updatedAt = now).also { db.scriptDao().update(it) }
                screen = Screen.PROMPTER
            }
        })
        Screen.PROMPTER -> current?.let { script -> TeleprompterScreen(script, isPro = billing.isPro, recordingCredits = recordingCredits,
            onConsumeCredit = { consumeRecordingCredit() }, onWatchAd = { onReward -> activity?.let { rewardedAds.show(it, { addRecordingCredit(); onReward() }, { message = "A rewarded ad is not ready right now. Check your connection and try again." }) } },
            onExit = { screen = Screen.HOME }, onRecorded = { uri, duration ->
            scope.launch {
                val localPath = withContext(Dispatchers.IO) { copyGalleryVideoToDraft(context, uri) }
                val draft = VideoDraft(title = script.title, uri = uri.toString(), durationMillis = duration, localPath = localPath)
                val id = db.videoDraftDao().insert(draft)
                currentDraft = draft.copy(id = id)
                if (localPath == null) message = "Your video was saved to Gallery, but Prompilo could not create a recovery copy. Check available device storage."
                screen = Screen.PLAYER
            }
        }) }
        Screen.PLAYER -> currentDraft?.let { draft -> PlayerScreen(draft, onBack = { screen = Screen.HOME }, onRestoreGallery = { startRestore(it) }) }
        Screen.MORE -> MoreScreen(onBack = { screen = Screen.HOME }, onBecomePro = { screen = Screen.PRO }, onRestore = { billing.restore() }, onFeedback = {
            val intent = Intent(Intent.ACTION_SEND).apply { type = "message/rfc822"; putExtra(Intent.EXTRA_SUBJECT, "Prompilo feedback"); putExtra(Intent.EXTRA_TEXT, "Hello Prompilo team,\n\n") }
            runCatching { context.startActivity(Intent.createChooser(intent, "Send feedback")) }.onFailure { message = "No email app is available for feedback." }
        }, onShare = {
            val intent = Intent(Intent.ACTION_SEND).apply { type = "text/plain"; putExtra(Intent.EXTRA_TEXT, "Try Prompilo, a simple offline teleprompter: https://play.google.com/store/apps/details?id=com.prompilo.app") }
            context.startActivity(Intent.createChooser(intent, "Share Prompilo"))
        }, onRate = {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.prompilo.app"))
            runCatching { context.startActivity(intent) }.onFailure { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.prompilo.app"))) }
        })
        Screen.PRO -> ProScreen(isPro = billing.isPro, products = billing.products, onBack = { screen = Screen.MORE }, onBuy = { billing.buy(it) },
            onWatchAd = { activity?.let { rewardedAds.show(it, { addRecordingCredit(); message = "Ad complete. You earned one video recording." }, { message = "A rewarded ad is not ready right now. Check your connection and try again." }) } }, onRestore = { billing.restore() })
    }
    deleteTarget?.let { target -> AlertDialog(onDismissRequest = { deleteTarget = null }, title = { Text("Delete script?") }, text = { Text("“${target.title}” will be removed from this device.") }, confirmButton = { TextButton(onClick = { scope.launch { db.scriptDao().delete(target); deleteTarget = null } }) { Text("Delete", color = Color(0xFFB3261E)) } }, dismissButton = { TextButton(onClick = { deleteTarget = null }) { Text("Cancel") } }) }
    if (showAddMenu) AddMenuDialog(
        onDismiss = { showAddMenu = false },
        onNewScript = { showAddMenu = false; editing = null; title = ""; content = ""; editorFolderId = selectedFolderId; screen = Screen.EDITOR },
        onImport = { showAddMenu = false; importLauncher.launch(arrayOf("text/*", "application/rtf")) },
        onNewFolder = { showAddMenu = false; folderName = ""; selectedFolderColor = FolderPalette.first(); showFolderDialog = true }
    )
    if (showFolderDialog) AlertDialog(
        onDismissRequest = { showFolderDialog = false },
        title = { Text("Create a folder", fontWeight = FontWeight.Bold) },
        text = { Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            OutlinedTextField(folderName, { folderName = it }, singleLine = true, label = { Text("Folder name") })
            Text("Choose a color", color = Ink, fontWeight = FontWeight.SemiBold)
            FolderColorPicker(selectedFolderColor) { selectedFolderColor = it }
        } },
        confirmButton = { TextButton(onClick = { val name = folderName.trim(); if (name.isNotEmpty()) scope.launch { selectedFolderId = db.scriptFolderDao().insert(ScriptFolder(name = name, colorHex = selectedFolderColor)); showFolderDialog = false } }) { Text("Create folder") } },
        dismissButton = { TextButton(onClick = { showFolderDialog = false }) { Text("Cancel") } }
    )
    folderColorTarget?.let { folder -> AlertDialog(
        onDismissRequest = { folderColorTarget = null }, title = { Text("Change folder color") },
        text = { FolderColorPicker(folder.colorHex) { color -> scope.launch { db.scriptFolderDao().update(folder.copy(colorHex = color)); folderColorTarget = null } } },
        confirmButton = { TextButton(onClick = { folderColorTarget = null }) { Text("Done") } }
    ) }
    deleteFolderTarget?.let { folder -> AlertDialog(
        onDismissRequest = { deleteFolderTarget = null }, title = { Text("Delete ${folder.name}?") },
        text = { Text("The folder will be deleted. Its scripts will stay on your device in the main scripts list.") },
        confirmButton = { TextButton(onClick = { scope.launch { db.scriptDao().moveFolderScriptsToRoot(folder.id); db.scriptFolderDao().delete(folder); if (selectedFolderId == folder.id) selectedFolderId = null; deleteFolderTarget = null } }) { Text("Delete folder", color = Color(0xFFB3261E)) } },
        dismissButton = { TextButton(onClick = { deleteFolderTarget = null }) { Text("Cancel") } }
    ) }
    deleteDraftTarget?.let { draft -> AlertDialog(onDismissRequest = { deleteDraftTarget = null }, title = { Text("Remove video draft?") }, text = { Text("This removes the draft and its private recovery copy. The video remains in your gallery.") }, confirmButton = { TextButton(onClick = { scope.launch { db.videoDraftDao().delete(draft); withContext(Dispatchers.IO) { draft.localPath?.let { File(it).delete() } }; deleteDraftTarget = null } }) { Text("Remove") } }, dismissButton = { TextButton(onClick = { deleteDraftTarget = null }) { Text("Cancel") } }) }
    message?.let { AlertDialog(onDismissRequest = { message = null }, text = { Text(it) }, confirmButton = { TextButton(onClick = { message = null }) { Text("OK") } }) }
    importError?.let { AlertDialog(onDismissRequest = { importError = null }, title = { Text("Import failed") }, text = { Text(it) }, confirmButton = { TextButton(onClick = { importError = null }) { Text("OK") } }) }
    billing.notice?.let { notice -> AlertDialog(onDismissRequest = { billing.dismissNotice() }, text = { Text(notice) }, confirmButton = { TextButton(onClick = { billing.dismissNotice() }) { Text("OK") } }) }
}

@Composable
private fun MoreScreen(onBack: () -> Unit, onBecomePro: () -> Unit, onRestore: () -> Unit, onFeedback: () -> Unit, onShare: () -> Unit, onRate: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Canvas).statusBarsPadding().navigationBarsPadding().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
        Row(Modifier.fillMaxWidth().height(62.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back", tint = Ink) }
            Text("More", fontSize = 23.sp, fontWeight = FontWeight.Bold, color = Ink)
        }
        Text("MAKE MORE OF PROMPILO", color = Color(0xFF897D6A), fontSize = 11.sp, letterSpacing = 1.2.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp, top = 12.dp, bottom = 8.dp))
        MoreRow(Icons.Default.WorkspacePremium, "Become Pro", "Record without ads and unlock every take", Color(0xFFFFE6AB), Accent, onBecomePro)
        MoreRow(Icons.Default.Restore, "Restore Purchase", "Bring back a previous Pro purchase", Color(0xFFE7E5FA), Color(0xFF6554A4), onRestore)
        Spacer(Modifier.height(22.dp))
        Text("SUPPORT", color = Color(0xFF897D6A), fontSize = 11.sp, letterSpacing = 1.2.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp, bottom = 8.dp))
        MoreRow(Icons.Default.Feedback, "Feedback", "Tell us what would make Prompilo better", Color(0xFFDFF2E9), Color(0xFF218266), onFeedback)
        MoreRow(Icons.Default.Share, "Share to Friends", "Send Prompilo to someone who speaks on camera", Color(0xFFFFE5DA), Color(0xFFC15E34), onShare)
        MoreRow(Icons.Default.ThumbUp, "Rate us", "Enjoying Prompilo? Leave a rating", Color(0xFFE4EEFF), Color(0xFF4776B7), onRate)
        Spacer(Modifier.height(24.dp))
        Text("Made for your next great take ✨", Modifier.fillMaxWidth().padding(bottom = 24.dp), textAlign = TextAlign.Center, color = Color(0xFF897D6A), fontSize = 13.sp)
    }
}

@Composable
private fun MoreRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, iconBg: Color, tint: Color, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp).clip(RoundedCornerShape(20.dp)).background(Color.White).clickable(onClick = onClick).padding(horizontal = 14.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(46.dp).background(iconBg, RoundedCornerShape(15.dp)), contentAlignment = Alignment.Center) { Icon(icon, null, tint = tint) }
        Spacer(Modifier.width(13.dp))
        Column(Modifier.weight(1f)) { Text(title, color = Ink, fontWeight = FontWeight.Bold, fontSize = 16.sp); Spacer(Modifier.height(3.dp)); Text(subtitle, color = Color(0xFF807361), fontSize = 12.sp, lineHeight = 16.sp) }
        Icon(Icons.Default.ChevronRight, null, tint = Color(0xFF9A8E7D))
    }
}

@Composable
private fun ProScreen(isPro: Boolean, products: Map<String, com.android.billingclient.api.ProductDetails>, onBack: () -> Unit, onBuy: (String) -> Unit, onWatchAd: () -> Unit, onRestore: () -> Unit) {
    var selected by remember { mutableStateOf(YEARLY_PRODUCT_ID) }
    val yearly = products[YEARLY_PRODUCT_ID]?.subscriptionOfferDetails?.firstOrNull()?.pricingPhases?.pricingPhaseList?.lastOrNull()?.formattedPrice ?: "Price shown by Google Play"
    val monthly = products[MONTHLY_PRODUCT_ID]?.subscriptionOfferDetails?.firstOrNull()?.pricingPhases?.pricingPhaseList?.lastOrNull()?.formattedPrice ?: "Price shown by Google Play"
    val lifetime = products[LIFETIME_PRODUCT_ID]?.oneTimePurchaseOfferDetails?.formattedPrice ?: "Price shown by Google Play"
    Column(Modifier.fillMaxSize().background(Color(0xFF100F0D)).statusBarsPadding().navigationBarsPadding().verticalScroll(rememberScrollState()).padding(horizontal = 22.dp)) {
        Row(Modifier.fillMaxWidth().height(54.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.background(Color.White.copy(alpha = .12f), CircleShape)) { Icon(Icons.Default.Close, "Close Pro", tint = Color.White) }
            if (!isPro) Button(onClick = onWatchAd, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF178DE0)), shape = RoundedCornerShape(24.dp), contentPadding = PaddingValues(horizontal = 15.dp)) { Icon(Icons.Default.PlayCircle, null); Spacer(Modifier.width(7.dp)); Text("Watch Ads +1", fontWeight = FontWeight.Bold) }
        }
        Spacer(Modifier.height(10.dp))
        Box(Modifier.fillMaxWidth().height(156.dp).clip(RoundedCornerShape(24.dp)).background(Brush.linearGradient(listOf(Color(0xFF42301C), Color(0xFF191713), Color(0xFF30200E)))).padding(20.dp)) {
            Column(Modifier.align(Alignment.BottomStart)) {
                Text("PROMPILO", color = Color(0xFFFFC653), fontSize = 11.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
                Text(if (isPro) "You’re Pro" else "Prompilo Pro", color = Color.White, fontSize = 31.sp, fontWeight = FontWeight.ExtraBold)
                Text(if (isPro) "All set for your next take" else "Focus on your words, not the ads", color = Color(0xFFE6DCCD), fontSize = 14.sp)
            }
            Icon(Icons.Default.WorkspacePremium, null, tint = Color(0xFFFFB11B), modifier = Modifier.align(Alignment.TopEnd).size(48.dp))
        }
        Spacer(Modifier.height(17.dp))
        listOf("No ads while recording", "Unlock unlimited video takes", "More features in the future").forEach { benefit ->
            Row(Modifier.padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.CheckCircle, null, tint = Color(0xFFFFB11B), modifier = Modifier.size(21.dp)); Spacer(Modifier.width(10.dp)); Text(benefit, color = Color.White, fontSize = 15.sp) }
        }
        Spacer(Modifier.height(17.dp))
        ProPlanCard("Yearly Plan", yearly, YEARLY_PRODUCT_ID, selected == YEARLY_PRODUCT_ID) { selected = YEARLY_PRODUCT_ID }
        Spacer(Modifier.height(10.dp))
        ProPlanCard("Monthly Plan", monthly, MONTHLY_PRODUCT_ID, selected == MONTHLY_PRODUCT_ID) { selected = MONTHLY_PRODUCT_ID }
        Spacer(Modifier.height(10.dp))
        ProPlanCard("One-Time Purchase", lifetime, LIFETIME_PRODUCT_ID, selected == LIFETIME_PRODUCT_ID) { selected = LIFETIME_PRODUCT_ID }
        Spacer(Modifier.height(17.dp))
        Button(onClick = { if (isPro) onBack() else onBuy(selected) }, modifier = Modifier.fillMaxWidth().height(58.dp), colors = ButtonDefaults.buttonColors(containerColor = Accent), shape = RoundedCornerShape(17.dp)) {
            Text(if (isPro) "Pro is active" else "Continue", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Text(if (selected == LIFETIME_PRODUCT_ID) "One-time payment · No recurring charge" else "Subscription renews automatically · Cancel anytime", Modifier.fillMaxWidth().padding(top = 9.dp), textAlign = TextAlign.Center, color = Color(0xFFBFB6A9), fontSize = 12.sp)
        TextButton(onClick = onRestore, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text("Already purchased? Restore", color = Color(0xFFFFC653), fontSize = 14.sp) }
        if (!isPro && products.isEmpty()) Text("Plans appear here after they are set up in Google Play.", Modifier.fillMaxWidth().padding(bottom = 18.dp), textAlign = TextAlign.Center, color = Color(0xFF8E867B), fontSize = 11.sp)
    }
}

@Composable
private fun ProPlanCard(title: String, price: String, id: String, selected: Boolean, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(17.dp)).background(if (selected) Color(0xFFFFF8ED) else Color(0xFFF3F0EB)).border(if (selected) 3.dp else 1.dp, if (selected) Accent else Color(0xFFDED8CE), RoundedCornerShape(17.dp)).clickable(onClick = onClick).padding(horizontal = 14.dp, vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
        RadioButton(selected = selected, onClick = onClick, colors = RadioButtonDefaults.colors(selectedColor = Accent))
        Column(Modifier.weight(1f)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(title, Modifier.weight(1f), color = Accent, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                if (id == YEARLY_PRODUCT_ID) Text("POPULAR", Modifier.clip(CircleShape).background(Accent.copy(alpha = .12f)).padding(horizontal = 9.dp, vertical = 4.dp), color = Accent, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = .6.sp)
            }
            Text(price, color = Ink, fontWeight = FontWeight.Bold, fontSize = 19.sp)
        }
    }
}

@Composable
private fun AddMenuDialog(onDismiss: () -> Unit, onNewScript: () -> Unit, onImport: () -> Unit, onNewFolder: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(28.dp), color = Color(0xFFFFFDF8), tonalElevation = 10.dp) {
            Column(Modifier.fillMaxWidth().padding(22.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("What would you like to add?", fontSize = 21.sp, fontWeight = FontWeight.Bold, color = Ink)
                Text("Choose a starting point for your next take.", fontSize = 13.sp, color = Color(0xFF786B58))
                AddMenuOption(Icons.Default.NoteAdd, "New script", "Write your words from scratch", Color(0xFFFFEBC2), Accent, onNewScript)
                AddMenuOption(Icons.Default.FileOpen, "Import a file", "Bring in a text script", Color(0xFFE0F5EE), Color(0xFF16856D), onImport)
                AddMenuOption(Icons.Default.CreateNewFolder, "New folder", "Keep scripts organized", Color(0xFFFFE8D8), Color(0xFFD87527), onNewFolder)
                TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) { Text("Close", color = Accent) }
            }
        }
    }
}

@Composable
private fun AddMenuOption(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, tintBackground: Color, tint: Color, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(tintBackground).clickable(onClick = onClick).padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(44.dp).background(Color.White.copy(alpha = .85f), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) { Icon(icon, null, tint = tint) }
        Spacer(Modifier.width(13.dp))
        Column(Modifier.weight(1f)) { Text(title, color = Ink, fontWeight = FontWeight.Bold); Spacer(Modifier.height(3.dp)); Text(subtitle, color = Color(0xFF71634F), fontSize = 12.sp) }
        Icon(Icons.Default.ChevronRight, null, tint = tint)
    }
}

@Composable
private fun FolderColorPicker(selected: String, onSelect: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        FolderPalette.chunked(4).forEach { rowColors ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                rowColors.forEach { hex ->
                    val color = folderColor(hex)
                    Box(Modifier.size(34.dp).clip(CircleShape).background(color).clickable { onSelect(hex) }, contentAlignment = Alignment.Center) {
                        if (selected.equals(hex, ignoreCase = true)) Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(19.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeScreen(scripts: List<Script>, folders: List<ScriptFolder>, drafts: List<VideoDraft>, selectedFolderId: Long?, showDrafts: Boolean,
    onFolder: (Long) -> Unit, onBackFolder: () -> Unit, onDrafts: (Boolean) -> Unit, onPlus: () -> Unit,
    onOpen: (Script) -> Unit, onDraft: (VideoDraft) -> Unit, onDelete: (Script) -> Unit, onDeleteDraft: (VideoDraft) -> Unit, onDeleteFolder: (ScriptFolder) -> Unit, onChangeFolderColor: (ScriptFolder) -> Unit, onMore: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) .92f else 1f, label = "plus")
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val homeScroll = rememberScrollState()
    LaunchedEffect(pressed) { if (pressed) { delay(130); pressed = false } }
    Column(Modifier.fillMaxSize().background(Canvas).statusBarsPadding().navigationBarsPadding().padding(horizontal = if (isLandscape) 18.dp else 24.dp).then(if (isLandscape) Modifier.verticalScroll(homeScroll) else Modifier)) {
        Spacer(Modifier.height(if (isLandscape) 10.dp else 28.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Image(painterResource(R.mipmap.ic_launcher), contentDescription = "Prompilo app icon", modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)), contentScale = ContentScale.Crop)
            Spacer(Modifier.width(12.dp)); Column { Text("Prompilo", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Ink); Text("Your words, right on cue.", fontSize = 13.sp, color = Color(0xFF786B58)) }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onMore, modifier = Modifier.background(Color(0xFFFFEBC2), CircleShape)) { Icon(Icons.Default.MoreHoriz, "More options", tint = Accent) }
        }
        Spacer(Modifier.height(if (isLandscape) 10.dp else 30.dp))
        Row(Modifier.fillMaxWidth().height(if (isLandscape) 102.dp else 152.dp).background(Color(0xFFFFF0D1), RoundedCornerShape(28.dp)).padding(horizontal = if (isLandscape) 16.dp else 20.dp, vertical = if (isLandscape) 12.dp else 18.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Speak with confidence", fontSize = if (isLandscape) 17.sp else 19.sp, lineHeight = if (isLandscape) 21.sp else 24.sp, fontWeight = FontWeight.Bold, color = Ink, maxLines = 2)
                Spacer(Modifier.height(if (isLandscape) 4.dp else 8.dp))
                Text("Practice your script. Record your best take.", color = Color(0xFF6F5C3B), fontSize = if (isLandscape) 12.sp else 13.sp, lineHeight = 18.sp, maxLines = 2)
            }
            Spacer(Modifier.width(12.dp))
            Box(Modifier.size(68.dp).background(Color(0xFFFFD884), CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.AutoAwesome, null, tint = Accent, modifier = Modifier.size(38.dp))
            }
        }
        Spacer(Modifier.height(if (isLandscape) 8.dp else 27.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (selectedFolderId != null && !showDrafts) IconButton(onClick = onBackFolder) { Icon(Icons.Default.ArrowBack, "All scripts", tint = Ink) }
                Text(if (showDrafts) "Video drafts" else selectedFolderId?.let { id -> folders.firstOrNull { it.id == id }?.name } ?: "Your scripts", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = Ink, maxLines = 1)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onDrafts(!showDrafts) }, modifier = Modifier.size(46.dp).background(if (showDrafts) Color(0xFFFFE8C8) else Color(0xFFFFEBC2), CircleShape)) {
                    Icon(Icons.Default.VideoLibrary, if (showDrafts) "Show scripts" else "Video drafts", tint = if (showDrafts) Color(0xFFE68A00) else Accent)
                }
                Spacer(Modifier.width(8.dp))
                Box(Modifier.scale(scale).size(50.dp).background(Accent, CircleShape).clickable { pressed = true; onPlus() }, contentAlignment = Alignment.Center) { Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(30.dp)) }
            }
        }
        Spacer(Modifier.height(12.dp))
        val visibleScripts = scripts.filter { it.folderId == selectedFolderId }
        if (showDrafts) {
            if (drafts.isEmpty()) {
                Box(Modifier.fillMaxWidth().then(if (isLandscape) Modifier.height(100.dp) else Modifier.weight(1f)), contentAlignment = Alignment.Center) { Text("Recorded videos will appear here", color = Color(0xFF786B58)) }
            } else if (isLandscape) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) { drafts.forEach { DraftRow(it, onDraft, onDeleteDraft) } }
            } else LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 18.dp)) { items(drafts, key = { "draft-${it.id}" }) { DraftRow(it, onDraft, onDeleteDraft) } }
        } else if (selectedFolderId == null && folders.isNotEmpty()) {
            if (isLandscape) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    folders.forEach { folder -> FolderRow(folder, scripts.count { it.folderId == folder.id }, onFolder, onChangeFolderColor, onDeleteFolder) }
                    visibleScripts.forEach { ScriptRow(it, onOpen, onDelete) }
                }
            } else LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 18.dp)) {
                items(folders, key = { "folder-${it.id}" }) { folder -> FolderRow(folder, scripts.count { it.folderId == folder.id }, onFolder, onChangeFolderColor, onDeleteFolder) }
                items(visibleScripts, key = { it.id }) { ScriptRow(it, onOpen, onDelete) }
            }
        } else if (visibleScripts.isEmpty()) {
            Box(Modifier.fillMaxWidth().then(if (isLandscape) Modifier.height(100.dp) else Modifier.weight(1f)), contentAlignment = Alignment.Center) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(if (selectedFolderId == null) "Your first script starts here" else "This folder is empty", color = Color(0xFF786B58), fontSize = 15.sp); Spacer(Modifier.height(6.dp)); Text("Tap + to write something", color = Color(0xFF9B8F7D), fontSize = 13.sp) } }
        } else if (isLandscape) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) { visibleScripts.forEach { ScriptRow(it, onOpen, onDelete) } }
        } else LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 18.dp)) { items(visibleScripts, key = { it.id }) { ScriptRow(it, onOpen, onDelete) } }
        Text("Saved privately on this device", Modifier.fillMaxWidth().padding(bottom = 15.dp), textAlign = TextAlign.Center, color = Color(0xFF9B8F7D), fontSize = 11.sp)
    }
}

@Composable
private fun DraftRow(draft: VideoDraft, onDraft: (VideoDraft) -> Unit, onDeleteDraft: (VideoDraft) -> Unit) {
    Row(Modifier.fillMaxWidth().background(Color(0xFFFFFDF8), RoundedCornerShape(18.dp)).clickable { onDraft(draft) }.padding(start = 16.dp, end = 5.dp, top = 12.dp, bottom = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(44.dp).background(Color(0xFFFFF0D1), RoundedCornerShape(13.dp)), contentAlignment = Alignment.Center) { Icon(Icons.Default.PlayCircle, null, tint = Accent) }
        Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(draft.title, fontWeight = FontWeight.SemiBold, color = Ink, maxLines = 1); Spacer(Modifier.height(4.dp)); Text("${formatTime(draft.durationMillis)} · ${DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(draft.recordedAt))}", fontSize = 12.sp, color = Color(0xFF897D6A)) }
        IconButton(onClick = { onDeleteDraft(draft) }) { Icon(Icons.Default.DeleteOutline, "Remove draft", tint = Color(0xFF958875)) }
    }
}

@Composable
private fun FolderRow(folder: ScriptFolder, count: Int, onFolder: (Long) -> Unit, onChangeFolderColor: (ScriptFolder) -> Unit, onDeleteFolder: (ScriptFolder) -> Unit) {
    val color = folderColor(folder.colorHex)
    Row(Modifier.fillMaxWidth().background(color.copy(alpha = .12f), RoundedCornerShape(18.dp)).clickable { onFolder(folder.id) }.padding(start = 16.dp, end = 6.dp, top = 8.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(42.dp).background(color.copy(alpha = .18f), RoundedCornerShape(13.dp)), contentAlignment = Alignment.Center) { Icon(Icons.Default.Folder, null, tint = color) }
        Spacer(Modifier.width(12.dp)); Text(folder.name, Modifier.weight(1f), color = Ink, fontWeight = FontWeight.SemiBold); Text("$count", color = Color(0xFF897D6A)); IconButton(onClick = { onChangeFolderColor(folder) }) { Icon(Icons.Default.Palette, "Change folder color", tint = color) }; IconButton(onClick = { onDeleteFolder(folder) }) { Icon(Icons.Default.DeleteOutline, "Delete folder", tint = Color(0xFF9A6871)) }
    }
}

@Composable
private fun ScriptRow(script: Script, onOpen: (Script) -> Unit, onDelete: (Script) -> Unit) {
    Row(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(18.dp)).clickable { onOpen(script) }.padding(start = 17.dp, end = 8.dp, top = 15.dp, bottom = 15.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(42.dp).background(Color(0xFFFFF0D1), RoundedCornerShape(13.dp)), contentAlignment = Alignment.Center) { Icon(Icons.Default.Article, null, tint = Accent) }
        Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(script.title, fontWeight = FontWeight.SemiBold, color = Ink, maxLines = 1); Spacer(Modifier.height(4.dp)); Text("${script.content.lineSequence().firstOrNull().orEmpty().take(62)} · ${DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(script.updatedAt))}", fontSize = 12.sp, color = Color(0xFF897D6A), maxLines = 1) }
        IconButton(onClick = { onDelete(script) }) { Icon(Icons.Default.DeleteOutline, "Delete script", tint = Color(0xFF958875)) }
    }
}

@Composable
private fun EditorScreen(title: String, onTitle: (String) -> Unit, content: String, onContent: (String) -> Unit, isEditing: Boolean, onBack: () -> Unit, onSave: () -> Unit, onStart: () -> Unit) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    Column(Modifier.fillMaxSize().background(Canvas).statusBarsPadding().navigationBarsPadding().padding(horizontal = if (isLandscape) 14.dp else 20.dp)) {
        Row(Modifier.fillMaxWidth().height(if (isLandscape) 44.dp else 60.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back", tint = Ink) }
            Text(if (isEditing) "Edit script" else "New script", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Ink)
        }
        Spacer(Modifier.height(if (isLandscape) 3.dp else 12.dp))
        Text("TITLE", fontSize = 11.sp, letterSpacing = 1.2.sp, fontWeight = FontWeight.Bold, color = Color(0xFF786B58))
        Spacer(Modifier.height(if (isLandscape) 3.dp else 8.dp))
        OutlinedTextField(value = title, onValueChange = onTitle, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Enter script title") }, singleLine = true, shape = RoundedCornerShape(15.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Accent, unfocusedBorderColor = Color(0xFFE8DDC9)))
        Spacer(Modifier.height(if (isLandscape) 6.dp else 17.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("YOUR SCRIPT", fontSize = 11.sp, letterSpacing = 1.2.sp, fontWeight = FontWeight.Bold, color = Color(0xFF786B58))
            Text("${content.length} characters", fontSize = 11.sp, color = Color(0xFF958875))
        }
        Spacer(Modifier.height(if (isLandscape) 4.dp else 8.dp))
        Box(Modifier.weight(1f).fillMaxWidth().background(Color.White, RoundedCornerShape(17.dp)).border(1.dp, Color(0xFFE8DDC9), RoundedCornerShape(17.dp)).padding(15.dp)) {
            BasicTextField(value = content, onValueChange = onContent, modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()), textStyle = TextStyle(color = Ink, fontSize = 17.sp, lineHeight = 26.sp), decorationBox = { inner -> if (content.isEmpty()) Text("Paste or write your script here…", color = Color(0xFFA99B86), fontSize = 17.sp); inner() })
        }
        Spacer(Modifier.height(if (isLandscape) 8.dp else 14.dp))
        if (isLandscape) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = onStart, modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(15.dp), contentPadding = PaddingValues(horizontal = 8.dp)) { Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) { Icon(Icons.Default.Videocam, null); Spacer(Modifier.width(6.dp)); Text("Start teleprompter", maxLines = 1) } }
                OutlinedButton(onClick = onSave, modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(15.dp), contentPadding = PaddingValues(horizontal = 8.dp)) { Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { Text("Save script", color = Accent, fontWeight = FontWeight.SemiBold, maxLines = 1) } }
            }
        } else {
            Button(onClick = onStart, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(16.dp)) { Icon(Icons.Default.Videocam, null); Spacer(Modifier.width(8.dp)); Text("Start teleprompter", fontWeight = FontWeight.SemiBold) }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = onSave, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(16.dp)) { Text("Save script", color = Accent, fontWeight = FontWeight.SemiBold) }
        }
        Spacer(Modifier.height(if (isLandscape) 4.dp else 12.dp))
    }
}

@Composable
private fun TeleprompterScreen(script: Script, isPro: Boolean, recordingCredits: Int, onConsumeCredit: () -> Unit, onWatchAd: (onReward: () -> Unit) -> Unit, onExit: () -> Unit, onRecorded: (Uri, Long) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val executor = remember(context) { ContextCompat.getMainExecutor(context) }
    var hasPermissions by remember { mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) }
    var permissionMessage by remember { mutableStateOf(false) }
    var cameraMessage by remember { mutableStateOf<String?>(null) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grants -> hasPermissions = grants[Manifest.permission.CAMERA] == true && grants[Manifest.permission.RECORD_AUDIO] == true; if (!hasPermissions) permissionMessage = true }
    var previewView by remember { mutableStateOf<PreviewView?>(null) }
    var videoCapture by remember { mutableStateOf<VideoCapture<Recorder>?>(null) }
    var recording by remember { mutableStateOf<Recording?>(null) }
    var isRecording by remember { mutableStateOf(false) }
    var startedAt by remember { mutableLongStateOf(0L) }
    var elapsed by remember { mutableLongStateOf(0L) }
    var facingFront by remember { mutableStateOf(true) }
    var speed by remember { mutableFloatStateOf(120f) }
    var fontSize by remember { mutableFloatStateOf(32f) }
    var mirrored by remember { mutableStateOf(false) }
    var textColor by remember { mutableStateOf(Color.White) }
    var backgroundAlpha by remember { mutableFloatStateOf(.48f) }
    var scriptPosition by remember { mutableFloatStateOf(0f) }
    var panelScale by remember { mutableFloatStateOf(1f) }
    var playing by remember { mutableStateOf(false) }
    var controlsVisible by remember { mutableStateOf(true) }
    var panelOffset by remember { mutableStateOf(androidx.compose.ui.unit.IntOffset(0, 0)) }
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val scroll = rememberScrollState()
    val scrollScope = rememberCoroutineScope()
    val activeRecording by rememberUpdatedState(recording)

    LaunchedEffect(playing, speed, hasPermissions) {
        var previousFrame = androidx.compose.runtime.withFrameNanos { it }
        while (playing && hasPermissions) {
            val frame = androidx.compose.runtime.withFrameNanos { it }
            val seconds = (frame - previousFrame) / 1_000_000_000f
            scroll.scrollBy(speed * 0.42f * seconds)
            previousFrame = frame
        }
    }
    LaunchedEffect(isRecording) { while (isRecording) { elapsed = System.currentTimeMillis() - startedAt; delay(250) } }
    DisposableEffect(Unit) { onDispose { activeRecording?.stop() } }

    DisposableEffect(hasPermissions, facingFront, previewView, lifecycleOwner) {
        if (hasPermissions && previewView != null) {
            val providerFuture = ProcessCameraProvider.getInstance(context)
            val listener = Runnable {
                try {
                    val provider = providerFuture.get()
                    val preview = Preview.Builder().build().also { it.surfaceProvider = previewView?.surfaceProvider }
                    val recorder = Recorder.Builder().setQualitySelector(QualitySelector.from(Quality.HD, FallbackStrategy.lowerQualityOrHigherThan(Quality.SD))).build()
                    val capture = VideoCapture.withOutput(recorder)
                    val selector = if (facingFront) CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA
                    provider.unbindAll(); provider.bindToLifecycle(lifecycleOwner, selector, preview, capture); videoCapture = capture
                } catch (error: Exception) { cameraMessage = "The camera could not start. Try switching cameras or restart the teleprompter. ${error.localizedMessage.orEmpty()}" }
            }
            providerFuture.addListener(listener, executor)
            onDispose { try { ProcessCameraProvider.getInstance(context).get().unbindAll() } catch (_: Exception) { } }
        } else onDispose { }
    }

    fun beginRecording() {
        videoCapture?.let { capture ->
            val values = ContentValues().apply { put(MediaStore.Video.Media.DISPLAY_NAME, "Prompilo_${System.currentTimeMillis()}.mp4"); put(MediaStore.Video.Media.MIME_TYPE, "video/mp4"); put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/Prompilo") }
            val output = MediaStoreOutputOptions.Builder(context.contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI).setContentValues(values).build()
            try {
                val pending = capture.output.prepareRecording(context, output).withAudioEnabled()
                recording = pending.start(executor) { event -> when (event) {
                    is VideoRecordEvent.Start -> { startedAt = System.currentTimeMillis(); isRecording = true }
                    is VideoRecordEvent.Finalize -> { isRecording = false; recording = null; if (!event.hasError()) onRecorded(event.outputResults.outputUri, event.recordingStats.recordedDurationNanos / 1_000_000) else { context.contentResolver.delete(event.outputResults.outputUri, null, null); cameraMessage = "Recording could not be saved. Check available device storage and try again." } }
                } }
            } catch (_: SecurityException) { permissionMessage = true } catch (error: Exception) { cameraMessage = "Recording could not start. ${error.localizedMessage.orEmpty()}" }
        }
    }

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        if (hasPermissions) {
            AndroidView(factory = { ctx -> PreviewView(ctx).also { it.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT); it.scaleType = PreviewView.ScaleType.FILL_CENTER; previewView = it } }, modifier = Modifier.fillMaxSize())
            Column(Modifier.fillMaxWidth(((if (isLandscape) .72f else .90f) * panelScale).coerceAtMost(1f)).fillMaxHeight((if (isLandscape) .55f else .52f) * panelScale).align(Alignment.TopCenter).padding(top = if (isLandscape) 44.dp else 82.dp, start = 20.dp, end = 20.dp).offset(x = if (isLandscape) scriptPosition.coerceIn(-100f, 100f).dp else 0.dp, y = if (isLandscape) 0.dp else scriptPosition.coerceIn(-160f, 10f).dp).graphicsLayer { scaleX = if (mirrored) -1f else 1f }.verticalScroll(scroll), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.height(18.dp))
                Text(script.content, color = textColor, fontSize = fontSize.sp, lineHeight = (fontSize * 1.42f).sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().background(Color.Black.copy(alpha = backgroundAlpha), RoundedCornerShape(18.dp)).padding(horizontal = 18.dp, vertical = 14.dp))
                Spacer(Modifier.height(90.dp))
            }
        } else {
            Column(Modifier.align(Alignment.Center).padding(30.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Videocam, null, tint = Color.White, modifier = Modifier.size(42.dp)); Spacer(Modifier.height(12.dp)); Text("Camera and microphone access", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp, textAlign = TextAlign.Center); Spacer(Modifier.height(8.dp)); Text("Prompilo uses the camera for your preview and the microphone for recorded audio. Nothing is uploaded.", color = Color(0xFFD4D1DC), textAlign = TextAlign.Center, fontSize = 14.sp); Spacer(Modifier.height(18.dp)); Button(onClick = { permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)) }) { Text("Allow access") }
            }
        }
        Row(Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onExit, modifier = Modifier.background(Color.Black.copy(alpha = .48f), CircleShape)) { Icon(Icons.Default.Close, "Close teleprompter", tint = Color.White) }
            Text(script.title, color = Color.White, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f).padding(horizontal = 12.dp), maxLines = 1)
            if (isRecording) Text(formatTime(elapsed), color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.background(Color.Black.copy(alpha = .5f), RoundedCornerShape(20.dp)).padding(horizontal = 11.dp, vertical = 7.dp))
        }
        if (controlsVisible && hasPermissions) {
            Column(Modifier.align(Alignment.BottomCenter).fillMaxWidth().navigationBarsPadding().padding(start = (if (isLandscape) 8.dp else 14.dp) + with(androidx.compose.ui.platform.LocalDensity.current) { panelOffset.x.toDp() }, end = if (isLandscape) 8.dp else 14.dp, bottom = if (isLandscape) 5.dp else 12.dp).offset(y = with(androidx.compose.ui.platform.LocalDensity.current) { panelOffset.y.toDp() }).background(Color(0xEE211F2A), RoundedCornerShape(22.dp)).padding(horizontal = if (isLandscape) 10.dp else 16.dp, vertical = if (isLandscape) 4.dp else 10.dp)) {
                Row(Modifier.fillMaxWidth().pointerInput(Unit) { detectDragGestures { change, drag -> change.consume(); panelOffset = androidx.compose.ui.unit.IntOffset((panelOffset.x + drag.x.toInt()).coerceIn(-220, 30), (panelOffset.y + drag.y.toInt()).coerceIn(-450, 250)) } }, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("PROMPTER", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (!playing) controlsVisible = false; playing = !playing }, modifier = Modifier.size(42.dp).background(Accent, CircleShape)) { Icon(if (playing) Icons.Default.Pause else Icons.Default.PlayArrow, if (playing) "Pause scrolling" else "Start scrolling", tint = Color.White) }
                        IconButton(onClick = { playing = false; scrollScope.launch { scroll.animateScrollTo(0) } }, modifier = Modifier.size(38.dp)) { Icon(Icons.Default.Replay, "Restart script", tint = Color.White) }
                        IconButton(onClick = { facingFront = !facingFront }, modifier = Modifier.size(38.dp)) { Icon(Icons.Default.Cameraswitch, "Switch camera", tint = Color.White) }
                        IconButton(onClick = { mirrored = !mirrored }, modifier = Modifier.size(38.dp)) { Icon(Icons.Default.Flip, "Mirror script", tint = Color.White) }; IconButton(onClick = { textColor = if (textColor == Color.White) Color(0xFFFFE082) else Color.White }, modifier = Modifier.size(38.dp)) { Icon(Icons.Default.FormatColorText, "Change text color", tint = textColor) }; IconButton(onClick = { controlsVisible = false }, modifier = Modifier.size(38.dp)) { Icon(Icons.Default.ExpandMore, "Collapse controls", tint = Color.White) }
                    }
                }
                if (isLandscape) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("WPM", color = Color(0xFFC9C5D3), fontSize = 10.sp, modifier = Modifier.width(38.dp))
                        Slider(value = speed, onValueChange = { speed = it }, valueRange = 40f..300f, modifier = Modifier.weight(1f))
                        Text("${speed.toInt()}", color = Color.White, fontSize = 11.sp, modifier = Modifier.width(34.dp), textAlign = TextAlign.End)
                        Spacer(Modifier.width(10.dp))
                        Text("TEXT", color = Color(0xFFC9C5D3), fontSize = 10.sp, modifier = Modifier.width(38.dp))
                        Slider(value = fontSize, onValueChange = { fontSize = it }, valueRange = 22f..52f, modifier = Modifier.weight(1f))
                        Text("${fontSize.toInt()}", color = Color.White, fontSize = 11.sp, modifier = Modifier.width(34.dp), textAlign = TextAlign.End)
                    }
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("BG", color = Color(0xFFC9C5D3), fontSize = 10.sp, modifier = Modifier.width(38.dp))
                        Slider(value = backgroundAlpha, onValueChange = { backgroundAlpha = it }, valueRange = .25f..0.82f, modifier = Modifier.weight(1f))
                        Text("${(backgroundAlpha * 100).toInt()}%", color = Color.White, fontSize = 11.sp, modifier = Modifier.width(38.dp), textAlign = TextAlign.End)
                        Spacer(Modifier.width(10.dp))
                        Text("POSITION", color = Color(0xFFC9C5D3), fontSize = 10.sp, modifier = Modifier.width(60.dp))
                        Slider(value = scriptPosition.coerceIn(-100f, 100f), onValueChange = { scriptPosition = it }, valueRange = -100f..100f, modifier = Modifier.weight(1f))
                        Text("${scriptPosition.coerceIn(-100f, 100f).toInt()}", color = Color.White, fontSize = 11.sp, modifier = Modifier.width(34.dp), textAlign = TextAlign.End)
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) { Text("SPEED", color = Color(0xFFC9C5D3), fontSize = 10.sp, modifier = Modifier.width(68.dp)); Slider(value = speed, onValueChange = { speed = it }, valueRange = 40f..300f, modifier = Modifier.weight(1f)); Text("${speed.toInt()} wpm", color = Color.White, fontSize = 11.sp, modifier = Modifier.width(62.dp), textAlign = TextAlign.End) }
                    Row(verticalAlignment = Alignment.CenterVertically) { Text("TEXT", color = Color(0xFFC9C5D3), fontSize = 10.sp, modifier = Modifier.width(68.dp)); Slider(value = fontSize, onValueChange = { fontSize = it }, valueRange = 22f..52f, modifier = Modifier.weight(1f)); Text("${fontSize.toInt()} sp", color = Color.White, fontSize = 11.sp, modifier = Modifier.width(62.dp), textAlign = TextAlign.End) }
                    Row(verticalAlignment = Alignment.CenterVertically) { Text("BG", color = Color(0xFFC9C5D3), fontSize = 10.sp, modifier = Modifier.width(68.dp)); Slider(value = backgroundAlpha, onValueChange = { backgroundAlpha = it }, valueRange = .25f..0.82f, modifier = Modifier.weight(1f)); Text("${(backgroundAlpha * 100).toInt()}%", color = Color.White, fontSize = 11.sp, modifier = Modifier.width(62.dp), textAlign = TextAlign.End) }
                    Row(verticalAlignment = Alignment.CenterVertically) { Text("POSITION", color = Color(0xFFC9C5D3), fontSize = 10.sp, modifier = Modifier.width(68.dp)); Slider(value = scriptPosition, onValueChange = { scriptPosition = it }, valueRange = -160f..10f, modifier = Modifier.weight(1f)); Text("${scriptPosition.toInt()}", color = Color.White, fontSize = 11.sp, modifier = Modifier.width(62.dp), textAlign = TextAlign.End) }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) { Text("PANEL SIZE · ${(panelScale * 100).toInt()}%", color = Color(0xFFC9C5D3), fontSize = 9.sp, letterSpacing = 1.sp) }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { panelScale = (panelScale - .1f).coerceAtLeast(.7f) }, modifier = Modifier.size(42.dp).background(Color.White.copy(alpha = .12f), CircleShape)) { Icon(Icons.Default.ZoomOut, "Shrink prompter panel", tint = Color.White) }
                    Button(onClick = {
                        if (isRecording) { recording?.stop(); recording = null; isRecording = false }
                        else if (isPro) beginRecording()
                        else if (recordingCredits > 0) { onConsumeCredit(); beginRecording() }
                        else onWatchAd { onConsumeCredit(); beginRecording() }
                    }, enabled = isRecording || videoCapture != null, colors = ButtonDefaults.buttonColors(containerColor = if (isRecording) Color(0xFFCF4548) else Color.White, contentColor = if (isRecording) Color.White else Ink), shape = RoundedCornerShape(24.dp), contentPadding = PaddingValues(horizontal = 18.dp, vertical = 0.dp)) { Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) { Icon(if (isRecording) Icons.Default.Stop else Icons.Default.FiberManualRecord, null); Spacer(Modifier.width(7.dp)); Text(if (isRecording) "Stop recording" else if (!isPro && recordingCredits == 0) "Watch ad & record" else "Record", maxLines = 1) } }
                    IconButton(onClick = { panelScale = (panelScale + .1f).coerceAtMost(1.3f) }, modifier = Modifier.size(42.dp).background(Color.White.copy(alpha = .12f), CircleShape)) { Icon(Icons.Default.ZoomIn, "Enlarge prompter panel", tint = Color.White) }
                }
            }
        } else if (hasPermissions) {
            IconButton(onClick = { controlsVisible = true }, modifier = Modifier.align(Alignment.BottomEnd).navigationBarsPadding().padding(18.dp).background(Accent, CircleShape)) { Icon(Icons.Default.ExpandLess, "Show controls", tint = Color.White) }
        }
    }
    cameraMessage?.let { detail -> AlertDialog(onDismissRequest = { cameraMessage = null }, title = { Text("Camera issue") }, text = { Text(detail) }, confirmButton = { TextButton(onClick = { cameraMessage = null }) { Text("OK") } }) }
    if (permissionMessage) AlertDialog(onDismissRequest = { permissionMessage = false }, title = { Text("Permissions needed") }, text = { Text("Camera access shows the live preview. Microphone access records audio with your video. You can still edit scripts without these permissions.") }, confirmButton = { TextButton(onClick = { permissionMessage = false; permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)) }) { Text("Try again") } }, dismissButton = { TextButton(onClick = { permissionMessage = false }) { Text("Close") } })
}

internal fun formatTime(ms: Long): String {
    val total = ms / 1000
    return String.format(Locale.getDefault(), "%02d:%02d", total / 60, total % 60)
}

@Composable
private fun PlayerScreen(draft: VideoDraft, onBack: () -> Unit, onRestoreGallery: (VideoDraft) -> Unit) {
    val context = LocalContext.current
    val backupFile = draft.localPath?.let(::File)?.takeIf { it.isFile }
    val playbackUri = backupFile?.let(Uri::fromFile) ?: Uri.parse(draft.uri)
    val galleryAvailable by produceState(initialValue = false, key1 = draft.uri) {
        value = withContext(Dispatchers.IO) {
            runCatching { context.contentResolver.openFileDescriptor(Uri.parse(draft.uri), "r")?.use { true } ?: false }.getOrDefault(false)
        }
    }
    Column(Modifier.fillMaxSize().background(Color(0xFF101015)).statusBarsPadding().navigationBarsPadding()) {
        Row(Modifier.fillMaxWidth().height(58.dp).padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back", tint = Color.White) }; Text(draft.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 19.sp, maxLines = 1) }
        AndroidView(factory = { ctx -> VideoView(ctx).apply {
            val controller = MediaController(ctx).also { it.setAnchorView(this) }
            setMediaController(controller)
            tag = playbackUri.toString()
            setOnPreparedListener { player -> player.isLooping = false; start(); controller.show(0) }
            setVideoURI(playbackUri)
        } }, update = { view ->
            if (view.tag != playbackUri.toString()) {
                val controller = MediaController(view.context).also { it.setAnchorView(view) }
                view.setMediaController(controller)
                view.tag = playbackUri.toString()
                view.setOnPreparedListener { player -> player.isLooping = false; view.start(); controller.show(0) }
                view.setVideoURI(playbackUri)
            }
        }, modifier = Modifier.fillMaxWidth().weight(1f))
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Video length", color = Color(0xFFD8D0C2), fontSize = 14.sp)
            Text(formatTime(draft.durationMillis), color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
        if (draft.localPath != null) {
            if (galleryAvailable) Text("Saved in Gallery", Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp), color = Color(0xFF8ED5A7), fontSize = 13.sp)
            else Button(onClick = { onRestoreGallery(draft) }, modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp).height(48.dp), shape = RoundedCornerShape(14.dp)) {
                Icon(Icons.Default.AddToPhotos, null); Spacer(Modifier.width(8.dp)); Text("Save to Gallery")
            }
        } else if (!galleryAvailable) Text("Gallery copy unavailable. This older draft has no recovery backup.", Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp), color = Color(0xFFFFCC80), fontSize = 12.sp)
        Row(Modifier.fillMaxWidth().padding(18.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { val uri = Uri.parse(draft.uri); val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply { type = "video/mp4"; putExtra(android.content.Intent.EXTRA_STREAM, uri); addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION) }; context.startActivity(android.content.Intent.createChooser(intent, "Share recording")) }, enabled = galleryAvailable, modifier = Modifier.weight(1f).height(54.dp), shape = RoundedCornerShape(15.dp), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)) { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Share, null); Spacer(Modifier.width(8.dp)); Text("Share video", maxLines = 1) } }
            OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f).height(54.dp), shape = RoundedCornerShape(15.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)) { Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { Text("Done", maxLines = 1) } }
        }
    }
}

private fun copyGalleryVideoToDraft(context: android.content.Context, uri: Uri): String? {
    var target: File? = null
    return try {
        val source = context.contentResolver.openInputStream(uri) ?: return null
        val directory = File(context.noBackupFilesDir, "video_drafts").apply { mkdirs() }
        target = File(directory, "draft_${System.currentTimeMillis()}_${UUID.randomUUID()}.mp4")
        source.use { input -> target!!.outputStream().buffered().use { output -> input.copyTo(output) } }
        target!!.absolutePath
    } catch (_: Exception) {
        target?.delete()
        null
    }
}

private fun copyDraftToGallery(context: android.content.Context, draft: VideoDraft): Uri? = runCatching {
    val sourceFile = draft.localPath?.let(::File)?.takeIf { it.isFile } ?: return null
    val resolver = context.contentResolver
    val name = "Prompilo_${draft.recordedAt}_${draft.id}.mp4"
    val values = ContentValues().apply {
        put(MediaStore.Video.Media.DISPLAY_NAME, name)
        put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/Prompilo")
            put(MediaStore.Video.Media.IS_PENDING, 1)
        } else {
            val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "Prompilo").apply { mkdirs() }
            put(MediaStore.Video.Media.DATA, File(directory, name).absolutePath)
        }
    }
    val destination = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values) ?: return null
    try {
        val output = resolver.openOutputStream(destination, "w") ?: error("Gallery destination could not be opened")
        sourceFile.inputStream().buffered().use { input -> output.buffered().use { input.copyTo(it) } }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val complete = ContentValues().apply { put(MediaStore.Video.Media.IS_PENDING, 0) }
            resolver.update(destination, complete, null, null)
        }
        destination
    } catch (error: Exception) {
        resolver.delete(destination, null, null)
        throw error
    }
}.getOrNull()

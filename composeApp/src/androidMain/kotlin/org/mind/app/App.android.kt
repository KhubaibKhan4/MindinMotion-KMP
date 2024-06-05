package org.mind.app

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.cmppreference.AppContext
import com.rizzi.bouquet.ResourceType
import com.rizzi.bouquet.VerticalPDFReader
import com.rizzi.bouquet.rememberVerticalPdfReaderState
import dev.gitlive.firebase.storage.File
import io.github.vinceglb.filekit.core.FileKit
import io.ktor.client.statement.HttpResponse
import io.ktor.util.InternalAPI
import io.ktor.utils.io.jvm.javaio.copyTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.mind.app.db.MyDatabase
import org.mind.app.di.appModule
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream

class AndroidApp : Application() {
    companion object {
        lateinit var INSTANCE: AndroidApp
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        AppContext.apply { set(this@AndroidApp) }
        var currentActivity: AppActivity? = null
        val options = com.google.firebase.FirebaseOptions.Builder()
            .setProjectId("mind-in-motion-70e7b")
            .setApplicationId("1:315093871386:android:aeb50fce5145217cec1aa3")
            .setApiKey("AIzaSyB65lrOPXWuZlKVNF0pH36fOigm81XNMWc")
            .setStorageBucket("mind-in-motion-70e7b.appspot.com")
            .build()

        com.google.firebase.FirebaseApp.initializeApp(this, options)
        /* Firebase.initialize(
             applicationContext,
             options = FirebaseOptions(
                 applicationId = "1:315093871386:android:aeb50fce5145217cec1aa3",
                 apiKey = "AIzaSyB65lrOPXWuZlKVNF0pH36fOigm81XNMWc",
                 projectId = "mind-in-motion-70e7b"
             )
         )*/
    }
}

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FileKit.init(this)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        setContent {
            startKoin {
                androidLogger()
                androidContext(this@AppActivity)
                modules(appModule)
            }
            App()
        }
    }
}

internal actual fun openUrl(url: String?) {
    val uri = url?.let { Uri.parse(it) } ?: return
    val intent = Intent().apply {
        action = Intent.ACTION_VIEW
        data = uri
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    AndroidApp.INSTANCE.startActivity(intent)
}

@Composable
internal actual fun notify(message: String) {
    val context = LocalContext.current
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}


actual fun createDriver(): SqlDriver {
    return AndroidSqliteDriver(MyDatabase.Schema, AndroidApp.INSTANCE.applicationContext, "chat.db")
}

@Composable
actual fun providePDF(url: String) {
    val pdfState = rememberVerticalPdfReaderState(
        resource = ResourceType.Remote(url),
        isZoomEnable = true,
    )
    if (!pdfState.isLoaded) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        )
        {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator(progress = { pdfState.loadPercent / 100f })
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Please Wait, Loading...")
            }
        }
    }

    VerticalPDFReader(
        state = pdfState,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Gray)
    )
}

actual fun createTempFileFromBitmap(imageBitmap: ImageBitmap): dev.gitlive.firebase.storage.File {
    val tempFile = java.io.File.createTempFile("temp", null)
    FileOutputStream(tempFile).use { outputStream ->
        // Convert ImageBitmap to ByteArray
        val byteArray = imageBitmap.asByteArray()

        // Write ByteArray to file
        outputStream.write(byteArray)
    }
    return adaptToFile(tempFile)
}

private fun adaptToFile(file: java.io.File): dev.gitlive.firebase.storage.File {
    return dev.gitlive.firebase.storage.File(Uri.fromFile(file))
}

private fun ImageBitmap.asAndroidBitmap(): android.graphics.Bitmap {
    val androidImageBitmap = this.asAndroidBitmap()
    return androidImageBitmap.copy(android.graphics.Bitmap.Config.ARGB_8888, false)
}

fun ImageBitmap.asByteArray(): ByteArray {
    val bitmap = this.asAndroidBitmap()
    val stream = ByteArrayOutputStream()
    bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}

actual fun createTempFile(name: String, extension: String): String {
    return java.io.File.createTempFile(name, extension).absolutePath
}

@OptIn(InternalAPI::class)
actual suspend fun saveResponseToFile(response: HttpResponse, filePath: String) {
    withContext(Dispatchers.IO) {
        val file = java.io.File(filePath)
        FileOutputStream(file).use { outputStream ->
            response.content.copyTo(outputStream)
        }
    }
}

actual fun sharePdf(pdfFilePath: String) {
    val uri = Uri.fromFile(java.io.File(pdfFilePath))
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    AndroidApp.INSTANCE.applicationContext.startActivity(Intent.createChooser(intent, "Share Resume"))
}
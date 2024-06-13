package org.mind.app

import androidx.compose.foundation.layout.* // Correct
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSUUID
import platform.UIKit.UIImage
import platform.posix.mkdir
import platform.posix.fopen
import platform.posix.fwrite
import platform.posix.fclose
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.toKString
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import platform.CoreGraphics.CGContextRef
import platform.CoreGraphics.CGBitmapInfo
import platform.CoreGraphics.CGColorSpaceCreateDeviceRGB
import platform.CoreGraphics.CGDataProviderCreateWithData
import platform.CoreGraphics.CGImage
import platform.CoreGraphics.CGImageGetBitsPerComponent
import platform.CoreGraphics.CGImageGetBitsPerPixel
import platform.CoreGraphics.CGImageGetBytesPerRow
import platform.CoreGraphics.CGImageGetColorSpace
import platform.CoreGraphics.CGImageGetHeight
import platform.CoreGraphics.CGImageGetWidth
import platform.CoreGraphics.CGImageAlphaInfo
import platform.CoreGraphics.kCGImageAlphaPremultipliedLast
import platform.UIKit.UIColor // Correct import for backgroundColor
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
// import androidx.compose.ui.viewinterop.AndroidView  // Remove this import
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import dev.gitlive.firebase.storage.File
import io.ktor.client.statement.HttpResponse
import org.mind.app.db.MyDatabase
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.WebKit.*

internal actual fun openUrl(url: String?) {
    val nsUrl = url?.let { NSURL.URLWithString(it) } ?: return
    UIApplication.sharedApplication.openURL(nsUrl)
}
@Composable
internal actual fun notify(message: String) {
    val window = UIApplication.sharedApplication.keyWindow ?: return
    val toastLabel = UILabel().apply {
        translatesAutoresizingMaskIntoConstraints = false
        backgroundColor = UIColor.black.withAlphaComponent(0.6)
        textColor = UIColor.white
        font = UIFont.systemFontOfSize(16.0)
        textAlignment = NSTextAlignmentCenter
        text = message
        alpha = 1.0
        layer.cornerRadius = 10.0
        clipsToBounds = true
    }
    window.addSubview(toastLabel)

    toastLabel.centerXAnchor.constraintEqualToAnchor(window.centerXAnchor).isActive = true
    toastLabel.bottomAnchor.constraintEqualToAnchor(
        window.bottomAnchor,
        constant = -100.0
    ).isActive = true

    UIView.animateWithDuration(
        duration = 4.0,
        delay = 0.1,
        options = UIViewAnimationOptions.CurveEaseOut,
        animations = {
            toastLabel.alpha = 0.0
        },
        completion = { finished in
                toastLabel.removeFromSuperview()
        }
    )
}

actual fun createDriver(): SqlDriver {
    return NativeSqliteDriver(MyDatabase.Schema, "chat.db")
}
@Composable
actual fun providePDF(url: String) {
    val context = LocalContext.current
    var webView: WKWebView? by remember { mutableStateOf(null) }

    iOSView(factory = {
        WKWebView(frame = UIScreen.mainScreen.bounds).apply {
            webView = this
            loadRequest(NSURLRequest.requestWithURL(NSURL(string = url) ?: NSURL()))
        }
    }, modifier = Modifier.fillMaxSize())
}
actual fun createTempFileFromBitmap(imageBitmap: ImageBitmap): File {
    val tempDir = "${IOSApp.INSTANCE.applicationContext.cacheDir.path}/resume"
    if (!File(tempDir).exists()) {
        mkdir(tempDir, 0b111111000)
    }
    val fileName = "${NSUUID().UUIDString}.jpg"
    val tempFile = File(tempDir, fileName)

    return try {
        val imageData = imageBitmap.toUIImage()?.jpegDataRepresentation(0.8)
        if (imageData != null) {
            val filePointer = fopen(tempFile.path, "w")
            if (filePointer != null) {
                memScoped {
                    val data = allocArray<ByteVar>(imageData.length.toInt())
                    imageData.getBytes(data, imageData.length)
                    fwrite(data, 1, imageData.length.toULong(), filePointer)
                }
                fclose(filePointer)
            }
        }
        tempFile
    } catch (e: IOException) {
        e.printStackTrace()
        tempFile
    }
}

fun ImageBitmap.toUIImage(): UIImage? {
    val width = width
    val height = height
    val bitmapInfo = CGBitmapInfo(rawValue = kCGImageAlphaPremultipliedLast.toULong())
    val colorSpace = CGColorSpaceCreateDeviceRGB()
    val bytesPerPixel = 4
    val bytesPerRow = bytesPerPixel * width
    val bitsPerComponent = 8

    memScoped {
        val bitmapData = allocArray<UInt8Var>(width * height * bytesPerPixel)
        val context = this@toUIImage.toCGContext(bitmapData, width, height, bitsPerComponent, bytesPerRow, colorSpace, bitmapInfo)

        if (context != null) {
            val cgImage = context.createCGImage(width, height, bitsPerComponent, bytesPerPixel * 8, bytesPerRow, colorSpace, bitmapInfo, null, null)
            return if (cgImage != null) {
                UIImage(CGImage = cgImage)
            } else {
                null
            }
        } else {
            return null
        }
    }
}


fun ImageBitmap.toCGContext(
    data: CPointer<UInt8Var>,
    width: Int,
    height: Int,
    bitsPerComponent: Int,
    bytesPerRow: Int,
    colorSpace: CGColorSpaceRef?,
    bitmapInfo: CGBitmapInfo
): CGContextRef? {
    val dataProvider = CGDataProviderCreateWithData(
        null,
        data,
        width * height * 4,
        null
    )

    return CGContextRef.create(
        data,
        width,
        height,
        bitsPerComponent,
        bytesPerRow,
        colorSpace,
        bitmapInfo
    )
}

fun CGContextRef?.createCGImage(
    width: Int,
    height: Int,
    bitsPerComponent: Int,
    bitsPerPixel: Int,
    bytesPerRow: Int,
    space: CGColorSpaceRef?,
    bitmapInfo: CGBitmapInfo,
    shouldInterpolate: Boolean?,
    intent: CGRenderingIntent?
): CGImage? {
    return if (this != null) {
        nativeImage?.let { CGImage(it) }
    } else {
        null
    }
}

actual fun createTempFile(name: String, extension: String): String {
    TODO("Not yet implemented")
}

actual suspend fun saveResponseToFile(
    response: HttpResponse,
    filePath: String,
) {
}

actual fun sharePdf(pdfFilePath: String) {
}
actual fun generateResumePdf(resumeHtml: String): ByteArray {
    // Use a library like PDFKit or your custom implementation to generate PDF from HTML.
    // Here is a simplified example using HTML to create PDF data.

    val webView = WKWebView(frame: CGRectMake(0.0, 0.0, 595.0, 842.0)) // A4 size
    webView.loadHTMLString(resumeHtml, baseURL = null)

    val pdfData = NSMutableData()
    let printFormatter = webView.viewPrintFormatter()
    let renderer = UIPrintPageRenderer()
    renderer.addPrintFormatter(printFormatter, startingAtPageAt: 0)

    let paperRect = CGRectMake(0.0, 0.0, 595.0, 842.0)
    let printableRect = CGRectMake(0.0, 0.0, 595.0, 842.0)

    renderer.setValue(paperRect, forKey: "paperRect")
    renderer.setValue(printableRect, forKey: "printableRect")

    let pdfContext = UIGraphicsBeginPDFContextToData(pdfData, paperRect, null)
    for i in 0..<renderer.numberOfPages {
        UIGraphicsBeginPDFPage()
        let bounds = UIGraphicsGetPDFContextBounds()
        renderer.drawPage(at: i, in: bounds)
    }
    UIGraphicsEndPDFContext()

    return pdfData.bytes!!.toByteArray()
}

actual fun saveResumeToFile(data: ByteArray, fileName: String) {
    val fileManager = NSFileManager.defaultManager
    val documentsURL = fileManager.URLsForDirectory(NSDocumentDirectory, NSUserDomainMask).lastObject as NSURL
    val fileURL = documentsURL.URLByAppendingPathComponent(fileName)
    data.usePinned {
        NSData.dataWithBytes(it.addressOf(0), data.size.toULong()).writeToURL(fileURL, true)
    }

    val documentInteractionController = UIDocumentInteractionController.interactionControllerWithURL(fileURL)
    documentInteractionController.delegate = object : NSObject(), UIDocumentInteractionControllerDelegateProtocol {}
    documentInteractionController.presentOptionsMenuFromRect(CGRectZero, UIWindow.keyWindow, true)
}
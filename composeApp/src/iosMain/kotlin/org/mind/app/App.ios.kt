package org.mind.app

import androidx.compose.foundation.layout.*
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import dev.gitlive.firebase.storage.File
import org.mind.app.db.MyDatabase
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIColor
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
        completion = {
            toastLabel.removeFromSuperview()
        }
    )
}

actual fun createDriver(): SqlDriver {
    return NativeSqliteDriver(MyDatabase.Schema, "chat.db")
}
@Composable
actual fun providePDF(url: String) {
    var webView: WKWebView? by remember { mutableStateOf(null) }

    AndroidView(factory = { context ->
        WKWebView(context).apply {
            webView = this
            loadRequest(NSURLRequest.requestWithURL(NSURL(string = url)))
        }
    }) { view ->
        Box(modifier = Modifier.fillMaxSize()) {
            view.backgroundColor = UIColor.blackColor
        }
    }
}
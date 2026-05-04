package com.smartcash.app.feature.earn

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.smartcash.app.core.design.DeepNavy
import com.smartcash.app.core.design.EmeraldGreen
import com.smartcash.app.core.design.NavySurface
import com.smartcash.app.core.design.White
import android.content.Intent
import android.net.Uri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewScreen(
    url: String,
    title: String,
    onBackPress: () -> Unit = {},
) {
    val context = LocalContext.current
    val webViewRef = remember { arrayOfNulls<WebView>(1) }
    var isLoading by remember { mutableStateOf(true) }
    var canGoBack by remember { mutableStateOf(false) }

    BackHandler {
        val webView = webViewRef[0]
        if (webView?.canGoBack() == true) {
            webView.goBack()
        } else {
            onBackPress()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavy),
    ) {
        TopAppBar(
            title = { Text(title, color = White) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = NavySurface,
                navigationIconContentColor = White,
                actionIconContentColor = White,
            ),
            navigationIcon = {
                IconButton(onClick = onBackPress) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = {
                    webViewRef[0]?.reload()
                }) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                }
                IconButton(onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }) {
                    Icon(Icons.Filled.OpenInBrowser, contentDescription = "Open in Browser")
                }
            },
        )

        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = EmeraldGreen,
            )
        }

        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        databaseEnabled = true
                    }
                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                            isLoading = true
                            canGoBack = view?.canGoBack() ?: false
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            isLoading = false
                            canGoBack = view?.canGoBack() ?: false
                        }
                    }
                    loadUrl(url)
                    webViewRef[0] = this
                }
            },
            modifier = Modifier.fillMaxSize(),
        )
    }
}

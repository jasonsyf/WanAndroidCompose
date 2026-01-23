package com.syf.wanandroidcompose.home.detail

import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailView(url: String, title: String? = null, onBack: () -> Unit) {
    var webView: WebView? by remember { mutableStateOf(null) }
    var progress by remember { mutableStateOf(0f) }
    var pageTitle by remember { mutableStateOf(title ?: "详情") }

    // Intercept back press for WebView history
    BackHandler(enabled = true) {
        if (webView?.canGoBack() == true) {
            webView?.goBack()
        } else {
            onBack()
        }
    }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text(text = pageTitle, maxLines = 1) },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                )
                            }
                        }
                )
            }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (progress < 1f) {
                LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
            }
            AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.loadWithOverviewMode = true
                            settings.useWideViewPort = true

                            webChromeClient =
                                    object : WebChromeClient() {
                                        override fun onProgressChanged(
                                                view: WebView?,
                                                newProgress: Int
                                        ) {
                                            progress = newProgress / 100f
                                        }

                                        override fun onReceivedTitle(
                                                view: WebView?,
                                                title: String?
                                        ) {
                                            super.onReceivedTitle(view, title)
                                            if (!title.isNullOrEmpty()) {
                                                pageTitle = title
                                            }
                                        }
                                    }

                            webViewClient =
                                    object : WebViewClient() {
                                        override fun shouldOverrideUrlLoading(
                                                view: WebView?,
                                                request: WebResourceRequest?
                                        ): Boolean {
                                            return super.shouldOverrideUrlLoading(view, request)
                                        }

                                        override fun onPageStarted(
                                                view: WebView?,
                                                url: String?,
                                                favicon: Bitmap?
                                        ) {
                                            super.onPageStarted(view, url, favicon)
                                        }

                                        override fun onPageFinished(view: WebView?, url: String?) {
                                            super.onPageFinished(view, url)
                                        }
                                    }

                            loadUrl(url)
                            webView = this
                        }
                    },
                    update = {
                        // Update valid?
                        // Often loadUrl should only be called once or on change.
                    },
                    modifier = Modifier.fillMaxSize()
            )
        }
    }
}

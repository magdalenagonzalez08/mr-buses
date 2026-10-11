package com.mr.buses

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast

class MainActivity : Activity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = WebView(this)
        setContentView(webView)

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = true
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString() ?: return false
                return if (url.startsWith("http")) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    true
                } else {
                    false
                }
            }
        }

        webView.addJavascriptInterface(Bridge(), "AndroidBridge")
        webView.loadUrl("file:///android_asset/index.html")
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }

    inner class Bridge {

        @JavascriptInterface
        fun saveFile(nombre: String, contenido: String) {
            try {
                val values = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, nombre)
                    put(MediaStore.Downloads.MIME_TYPE, "application/json")
                    put(MediaStore.Downloads.IS_PENDING, 1)
                }
                val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                if (uri != null) {
                    contentResolver.openOutputStream(uri)?.use {
                        it.write(contenido.toByteArray(Charsets.UTF_8))
                    }
                    values.clear()
                    values.put(MediaStore.Downloads.IS_PENDING, 0)
                    contentResolver.update(uri, values, null, null)
                }
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Archivo guardado en Descargas", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "No se pudo guardar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

package info.bugnode.view

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import info.bugnode.R
import info.bugnode.controller.WebController
import info.bugnode.model.Url
import info.bugnode.model.User
import okhttp3.FormBody
import java.util.*
import kotlin.concurrent.schedule

class NetworkActivity : AppCompatActivity() {
  private lateinit var webView: WebView
  private lateinit var user: User

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_network)
    webView = findViewById(R.id.network_view)
    user = User(this)
    val webSettings: WebSettings = webView.settings
    webView.settings.javaScriptEnabled = true
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
      webSettings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
    }
    webView.webViewClient = object : WebViewClient() {
      override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
      }
    }
    webView.webChromeClient = WebChromeClient()
    Timer().schedule(1000) {
      Log.d("MEMEME", ("==========="))
      Log.d("MEMEME", user.getString("token"))
      Log.d("MEMEME", user.getString("id"))
      Log.d("MEMEME", ("==========="))
      val res = WebController.WebView("binary.android.binary", user.getString("token"), FormBody.Builder()).call()
      runOnUiThread() {
        webView.loadData(res.getString("data"), "text/html", "UTF-8")
        webView.loadDataWithBaseURL(Url.web("binary"), res.getString("data"), "text/html", "utf-8", null)
      }
    }
  }
}
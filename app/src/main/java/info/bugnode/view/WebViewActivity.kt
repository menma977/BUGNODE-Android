package info.bugnode.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import info.bugnode.R
import info.bugnode.config.Loading
import info.bugnode.controller.WebController
import info.bugnode.model.Url
import info.bugnode.model.User
import okhttp3.FormBody
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.schedule

class WebViewActivity : AppCompatActivity() {
  private lateinit var loading: Loading
  private lateinit var user: User
  private lateinit var response: JSONObject
  private lateinit var webView: WebView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_web_view)

    loading = Loading(this)
    user = User(this)

    webView = findViewById(R.id.webViewContent)

    loadHtml()
  }

  private fun loadHtml() {
    loading.openDialog()
    Timer().schedule(1000) {
      val body = FormBody.Builder()
      body.addEncoded("Authorization", user.getString("token"))
      response = WebController.WebView(intent.getStringExtra("url").toString(), user.getString("token"), body).call()
      if (response.getInt("code") == 200) {
        runOnUiThread {
          webView.settings.javaScriptEnabled = true
          webView.loadData(response.getString("data"), "text/html", "UTF-8")
          webView.loadDataWithBaseURL(Url.web("binary.android"), response.getString("data"), "text/html", "UTF-8", null)
          loading.closeDialog()
        }
      }
    }
  }
}
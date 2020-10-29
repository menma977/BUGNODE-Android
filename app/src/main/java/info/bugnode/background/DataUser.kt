package info.bugnode.background

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import info.bugnode.BuildConfig
import info.bugnode.controller.WebController
import info.bugnode.model.User
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.schedule

class DataUser : Service() {
  private lateinit var json: JSONObject
  private lateinit var user: User
  private var startBackgroundService: Boolean = false

  override fun onBind(p0: Intent?): IBinder? {
    TODO("Not yet implemented")
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    onHandleIntent()

    return START_STICKY
  }

  private fun onHandleIntent() {
    user = User(this)
    var time = System.currentTimeMillis()
    val trigger = Object()

    Timer().schedule(1000) {
      while (true) {
        val delta = System.currentTimeMillis() - time
        if (delta >= 2000) {
          time = System.currentTimeMillis()
          val privateIntent = Intent()
          if (startBackgroundService) {
            synchronized(trigger) {
              json = WebController.Get("user.show", user.getString("token")).call()
              if (json.getInt("code") == 200) {
                privateIntent.putExtra("username", json.getJSONObject("data").getString("username"))
                privateIntent.putExtra("cookie", json.getJSONObject("data").getString("sessionKey"))
                privateIntent.putExtra("wallet", json.getJSONObject("data").getString("wallet"))
                privateIntent.putExtra("balanceDogeBug", json.getJSONObject("data").getString("balanceDogeBug"))
                privateIntent.putExtra("canPlay", json.getJSONObject("data").getBoolean("canPlay"))
                privateIntent.putExtra("role", json.getJSONObject("data").getString("role"))
                privateIntent.putExtra("name", json.getJSONObject("data").getString("name"))
                privateIntent.putExtra("email", json.getJSONObject("data").getString("email"))
                privateIntent.putExtra("phone", json.getJSONObject("data").getString("phone"))
                privateIntent.putExtra("active", json.getJSONObject("data").getBoolean("active"))
                privateIntent.putExtra("dollar", json.getJSONObject("data").getString("dollar"))

                if (json.getJSONObject("data").getInt("version") != BuildConfig.VERSION_CODE) {
                  privateIntent.putExtra("isLogout", true)
                }
              } else {
                if (json.getString("data").contains("Unauthenticated.")) {
                  privateIntent.putExtra("isLogout", true)
                } else {
                  privateIntent.putExtra("isLogout", false)
                  trigger.wait(5000)
                }
              }

              privateIntent.action = "api.web"
              LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(privateIntent)
            }
          } else {
            break
          }
        }
      }
    }
  }

  override fun onCreate() {
    super.onCreate()
    startBackgroundService = true
  }

  override fun onDestroy() {
    super.onDestroy()
    startBackgroundService = false
  }
}
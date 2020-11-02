package info.bugnode.background

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import info.bugnode.BuildConfig
import info.bugnode.controller.WebController
import info.bugnode.model.User
import org.json.JSONObject
import java.lang.Thread.sleep
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

    Timer().schedule(100) {
      while (true) {
        val delta = System.currentTimeMillis() - time
        if (delta >= 1000) {
          time = System.currentTimeMillis()
          val privateIntent = Intent()
          if (startBackgroundService) {
            if (user.getString("token").isEmpty()) {
              break
            }
            json = WebController.Get("user.show", user.getString("token")).call()
            println(json)
            if (json.getInt("code") == 200) {
              user.setString("username", json.getJSONObject("data").getString("username"))
              user.setString("cookie", json.getJSONObject("data").getString("sessionKey"))
              user.setString("wallet", json.getJSONObject("data").getString("wallet"))
              user.setBoolean("canPlay", json.getJSONObject("data").getBoolean("canPlay"))
              user.setString("role", json.getJSONObject("data").getString("role"))
              user.setString("name", json.getJSONObject("data").getString("name"))
              user.setString("email", json.getJSONObject("data").getString("email"))
              user.setString("phone", json.getJSONObject("data").getString("phone"))
              user.setBoolean("active", json.getJSONObject("data").getBoolean("active"))
              user.setString("dollar", json.getJSONObject("data").getString("dollar"))
              user.setString("balanceDogeBug", json.getJSONObject("data").getString("balanceDogeBug"))
              user.setInteger("progress", json.getJSONObject("data").getInt("progress"))
              user.setString("totalLimit", json.getJSONObject("data").getString("total"))
              user.setBoolean("queue", json.getJSONObject("data").getBoolean("queue"))

              if (json.getJSONObject("data").getInt("version") != BuildConfig.VERSION_CODE) {
                user.setBoolean("isLogout", true)
              }
            } else {
              if (json.getString("data").contains("Unauthenticated.")) {
                user.setBoolean("isLogout", true)
                sleep(1000)
              } else {
                user.setBoolean("isLogout", false)
                sleep(10000)
              }
            }

            privateIntent.action = "api.web"
            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(privateIntent)
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
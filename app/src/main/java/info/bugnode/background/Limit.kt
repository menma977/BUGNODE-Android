package info.bugnode.background

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import info.bugnode.controller.WebController
import info.bugnode.model.User
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.schedule

class Limit : Service() {
  private lateinit var json: JSONObject
  private lateinit var user: User
  private var startBackgroundService: Boolean = false

  override fun onBind(intent: Intent?): IBinder? {
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
    Timer().schedule(100) {
      while (true) {
        val delta = System.currentTimeMillis() - time
        if (delta >= 3000) {
          time = System.currentTimeMillis()
          val privateIntent = Intent()
          if (startBackgroundService) {
            synchronized(trigger) {
              json = WebController.Get("user.limit", user.getString("token")).call()
              if (json.getInt("code") == 200) {
                user.setString("progress", json.getJSONObject("data").getString("progress"))
                user.setString("totalLimit", json.getJSONObject("data").getString("total"))
              } else {
                if (json.getString("data").contains("Unauthenticated.")) {
                  user.setBoolean("isLogout", true)
                } else {
                  user.setBoolean("isLogout", false)
                  trigger.wait(5000)
                }
              }
              privateIntent.action = "api.web.queue"
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
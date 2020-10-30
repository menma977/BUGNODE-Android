package info.bugnode.background

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import info.bugnode.controller.DogeController
import info.bugnode.model.User
import okhttp3.FormBody
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.schedule

class Balance999Doge : Service() {
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

    Timer().schedule(5000) {
      while (true) {
        val delta = System.currentTimeMillis() - time
        if (delta >= 15000) {
          time = System.currentTimeMillis()
          val privateIntent = Intent()
          if (startBackgroundService) {
            synchronized(trigger) {
              try {
                val body = FormBody.Builder()
                body.addEncoded("a", "GetBalance")
                body.addEncoded("key", "1b4755ced78e4d91bce9128b9a053cad")
                body.addEncoded("s", user.getString("cookie"))
                body.addEncoded("Currency", "doge")
                json = DogeController.Post(body).call()
                println(json)
                if (json.getInt("code") == 200) {
                  if (json.getJSONObject("data").getString("Balance").isEmpty()) {
                    user.setString("balance", "0")
                  } else {
                    user.setString("balance", json.getJSONObject("data").getString("Balance"))
                  }
                  privateIntent.action = "api.doge"
                  LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(privateIntent)
                } else {
                  trigger.wait(60000)
                }
              } catch (e: Exception) {
                Log.w("error", e.message.toString())
              }
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
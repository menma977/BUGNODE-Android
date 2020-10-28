package info.bugnode

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import info.bugnode.controller.DogeController
import info.bugnode.controller.WebController
import info.bugnode.model.User
import info.bugnode.view.LoginActivity
import info.bugnode.view.NavigationActivity
import okhttp3.FormBody
import org.json.JSONObject
import java.math.BigDecimal
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {
  private lateinit var move: Intent
  private lateinit var user: User
  private lateinit var jsonObject: JSONObject

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    user = User(this)

    get()
  }

  private fun get() {
    Timer().schedule(100) {
      jsonObject = if (user.getString("token").isEmpty()) {
        WebController.Get("version", "").call()
      } else {
        WebController.Get("user.show", user.getString("token")).call()
      }
      println(jsonObject)
      if (jsonObject.getInt("code") == 200) {
        if (jsonObject.getJSONObject("data").getInt("version") == BuildConfig.VERSION_CODE) {
          if (user.getString("cookie").isNotEmpty()) {
            val body = FormBody.Builder()
            body.addEncoded("a", "GetBalance")
            body.addEncoded("key", "1b4755ced78e4d91bce9128b9a053cad")
            body.addEncoded("s", user.getString("cookie"))
            body.addEncoded("Currency", "doge")
            jsonObject = DogeController.Post(body).call()
            if (jsonObject.getInt("code") == 200) {
              move = Intent(applicationContext, NavigationActivity::class.java)
              move.putExtra("balance", BigDecimal(jsonObject.getJSONObject("data").getString("balance")))
              startActivity(move)
              finish()
            } else {
              move = Intent(applicationContext, LoginActivity::class.java)
              move.putExtra("isUpdate", false)
              startActivity(move)
              finish()
            }
          } else {
            move = Intent(applicationContext, LoginActivity::class.java)
            move.putExtra("isUpdate", false)
            startActivity(move)
            finish()
          }
        } else {
          move = Intent(applicationContext, LoginActivity::class.java)
          move.putExtra("isUpdate", true)
          startActivity(move)
          finish()
        }
      } else {
        move = Intent(applicationContext, LoginActivity::class.java)
        move.putExtra("isUpdate", false)
        startActivity(move)
        finish()
      }
    }
  }
}
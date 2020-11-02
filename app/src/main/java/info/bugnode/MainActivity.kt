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
    Timer().schedule(1000) {
      jsonObject = if (user.getString("token").isEmpty()) {
        WebController.Get("version", "").call()
      } else {
        WebController.Get("user.show", user.getString("token")).call()
      }
      if (jsonObject.getInt("code") == 200) {
        if (jsonObject.getJSONObject("data").getInt("version") == BuildConfig.VERSION_CODE) {
          if (user.getString("token").isNotEmpty()) {
            val body = FormBody.Builder()
            body.addEncoded("a", "GetBalance")
            body.addEncoded("key", "1b4755ced78e4d91bce9128b9a053cad")
            body.addEncoded("s", user.getString("cookie"))
            body.addEncoded("Currency", "doge")
            val jsonObjectDoge = DogeController.Post(body).call()
            if (jsonObjectDoge.getInt("code") == 200) {
              user.setString("cookie", jsonObject.getJSONObject("data").getString("sessionKey"))
              user.setString("wallet", jsonObject.getJSONObject("data").getString("wallet"))
              user.setBoolean("canPlay", jsonObject.getJSONObject("data").getBoolean("canPlay"))
              user.setString("role", jsonObject.getJSONObject("data").getString("role"))
              user.setString("name", jsonObject.getJSONObject("data").getString("name"))
              user.setString("email", jsonObject.getJSONObject("data").getString("email"))
              user.setString("phone", jsonObject.getJSONObject("data").getString("phone"))
              user.setBoolean("active", jsonObject.getJSONObject("data").getBoolean("active"))
              user.setString("dollar", jsonObject.getJSONObject("data").getString("dollar"))
              user.setString("balanceDogeBug", jsonObject.getJSONObject("data").getString("balanceDogeBug"))
              user.setInteger("progress", jsonObject.getJSONObject("data").getInt("progress"))
              user.setString("totalLimit", jsonObject.getJSONObject("data").getString("total"))
              user.setBoolean("queue", jsonObject.getJSONObject("data").getBoolean("queue"))
              user.setBoolean("isLogout", false)
              val getBalanceDogeBug = jsonObject.getJSONObject("data").getString("balanceDogeBug").toBigDecimal()

              user.setString("balance", jsonObjectDoge.getJSONObject("data").getString("Balance"))
              user.setString("balanceDogeBug", getBalanceDogeBug.toPlainString())

              move = Intent(applicationContext, NavigationActivity::class.java)
              move.putExtra("balance", BigDecimal(jsonObjectDoge.getJSONObject("data").getString("Balance")))
              move.putExtra("balanceDogeBug", getBalanceDogeBug)
              startActivity(move)
              finishAffinity()
            } else {
              user.clear()
              move = Intent(applicationContext, LoginActivity::class.java)
              move.putExtra("isUpdate", false)
              startActivity(move)
              finishAffinity()
            }
          } else {
            user.clear()
            move = Intent(applicationContext, LoginActivity::class.java)
            move.putExtra("isUpdate", false)
            startActivity(move)
            finishAffinity()
          }
        } else {
          user.clear()
          move = Intent(applicationContext, LoginActivity::class.java)
          move.putExtra("isUpdate", true)
          startActivity(move)
          finishAffinity()
        }
      } else {
        user.clear()
        move = Intent(applicationContext, LoginActivity::class.java)
        move.putExtra("isUpdate", false)
        startActivity(move)
        finishAffinity()
      }
    }
  }
}
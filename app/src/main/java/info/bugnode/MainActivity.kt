package info.bugnode

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import info.bugnode.controller.WebController
import info.bugnode.model.User
import info.bugnode.view.NavigationActivity
import org.json.JSONObject
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
      jsonObject = WebController.Get("get.data", user.getString("token")).call()
      println(jsonObject)
    }
  }
}
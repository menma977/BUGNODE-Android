package info.bugnode.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import org.json.JSONObject
import info.bugnode.R
import info.bugnode.config.Loading
import info.bugnode.controller.WebController
import okhttp3.FormBody
import java.util.*
import kotlin.concurrent.schedule

class LoginActivity : AppCompatActivity() {
  private lateinit var loading: Loading
  private lateinit var move: Intent
  private lateinit var username: EditText
  private lateinit var password: EditText
  private lateinit var loginButton: Button
  private lateinit var textViewRegister: TextView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)

    loading = Loading(this)

    loginButton = findViewById(R.id.buttonLogin)
    textViewRegister = findViewById(R.id.textViewRegister)
    username = findViewById(R.id.editTextUsername)
    password = findViewById(R.id.editTextPassword)

    loginButton.setOnClickListener {
      loading.openDialog()
      Timer().schedule(1000) {
        move = Intent(applicationContext, NavigationActivity::class.java)
        startActivity(move)
        loading.closeDialog()
      }
    }

    textViewRegister.setOnClickListener {
      loading.openDialog()
      Timer().schedule(1000) {
        move = Intent(applicationContext, RegisterActivity::class.java)
        startActivity(move)
        loading.closeDialog()
      }
    }
  }

  private fun doLogin(){
    val body = FormBody.Builder()
    body.addEncoded("username", username.text.toString())
    body.addEncoded("password", password.text.toString())
    Timer().schedule(100) {
      val result: JSONObject = WebController.Post("login", "", body).call()
      if (result.getInt("code") == 200) {
        runOnUiThread {
          Toast.makeText(applicationContext, result.getJSONObject("data").getString("message"), Toast.LENGTH_SHORT).show()
          loading.closeDialog()
          move = Intent(applicationContext, NavigationActivity::class.java)
          startActivity(move)
          loading.closeDialog()
          finish()
        }
      } else {
        runOnUiThread {
          Toast.makeText(applicationContext, result.getJSONObject("data").getString("message"), Toast.LENGTH_SHORT).show()
          loading.closeDialog()
        }
      }
    }
  }
}
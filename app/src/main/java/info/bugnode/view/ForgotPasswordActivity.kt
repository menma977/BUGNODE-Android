package info.bugnode.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import info.bugnode.MainActivity
import info.bugnode.R
import info.bugnode.config.Loading
import info.bugnode.controller.WebController
import okhttp3.FormBody
import java.util.*
import kotlin.concurrent.schedule

class ForgotPasswordActivity : AppCompatActivity() {
  private lateinit var loading: Loading
  private lateinit var email: EditText
  private lateinit var emailSend: Button
  private lateinit var login: TextView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_forgot_password)

    loading = Loading(this)

    email = findViewById(R.id.editTextEmail)
    emailSend = findViewById(R.id.buttonSend)
    login = findViewById(R.id.textVIewLogin)

    emailSend.setOnClickListener {
      loading.openDialog()
      val body = FormBody.Builder()
      body.addEncoded("email", email.text.toString())
      Timer().schedule(100) {
        val response = WebController.Post("password.request", "", body).call()
        if (response.getInt("code") == 200) {
          runOnUiThread {
            Toast.makeText(applicationContext, response.getJSONObject("data").getString("message"), Toast.LENGTH_SHORT).show()
            loading.closeDialog()
            finish()
          }
        } else {
          runOnUiThread {
            Toast.makeText(applicationContext, response.getString("data"), Toast.LENGTH_LONG).show()
            loading.closeDialog()
            finish()
          }
        }
      }
    }

    login.setOnClickListener {
      val intent = Intent(this, MainActivity::class.java)
      startActivity(intent)
    }
  }
}
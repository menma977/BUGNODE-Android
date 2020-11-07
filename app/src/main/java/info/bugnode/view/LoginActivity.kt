package info.bugnode.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import info.bugnode.BuildConfig
import info.bugnode.R
import info.bugnode.config.Loading
import info.bugnode.controller.WebController
import info.bugnode.model.User
import okhttp3.FormBody
import java.util.*
import kotlin.concurrent.schedule

class LoginActivity : AppCompatActivity() {
  private lateinit var user: User
  private lateinit var loading: Loading
  private lateinit var move: Intent
  private lateinit var version: TextView
  private lateinit var username: EditText
  private lateinit var password: EditText
  private lateinit var loginButton: Button
  private lateinit var textViewRegister: TextView
  private lateinit var updateButton: Button
  private lateinit var textViewForgotPassword: TextView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)

    user = User(this)
    loading = Loading(this)

    version = findViewById(R.id.textViewVersion)
    loginButton = findViewById(R.id.buttonLogin)
    textViewRegister = findViewById(R.id.textViewRegister)
    username = findViewById(R.id.editTextUsername)
    password = findViewById(R.id.editTextPassword)
    updateButton = findViewById(R.id.buttonUpdate)
    textViewForgotPassword = findViewById(R.id.textViewForgotPassword)

    version.text = BuildConfig.VERSION_NAME
    //    username.setText("bugnode")
    //    password.setText("admin")
    if (intent.getBooleanExtra("isUpdate", true)) {
      loginButton.visibility = Button.GONE
      updateButton.visibility = Button.VISIBLE
      username.visibility = EditText.GONE
      password.visibility = EditText.GONE
    } else {
      updateButton.visibility = Button.GONE
    }

    loginButton.setOnClickListener {
      loading.openDialog()
      when {
        !validatePermission() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
          loading.closeDialog()
          doRequestPermission()
        }
        username.text.isEmpty() -> {
          Toast.makeText(this, "username required", Toast.LENGTH_SHORT).show()
          username.requestFocus()
          loading.closeDialog()
        }
        password.text.isEmpty() -> {
          Toast.makeText(this, "password required", Toast.LENGTH_SHORT).show()
          password.requestFocus()
          loading.closeDialog()
        }
        else -> {
          doLogin()
        }
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

    textViewForgotPassword.setOnClickListener {
      loading.openDialog()
      Timer().schedule(1000) {
        move = Intent(applicationContext, ForgotPasswordActivity::class.java)
        startActivity(move)
        loading.closeDialog()
      }
    }
  }

  private fun doLogin() {
    val body = FormBody.Builder()
    body.addEncoded("username", username.text.toString())
    body.addEncoded("password", password.text.toString())
    Timer().schedule(100) {
      val result = WebController.Post("login", "", body).call()
      runOnUiThread {
        if (result.getInt("code") == 200) {
          if (result.getJSONObject("data").getInt("version") == BuildConfig.VERSION_CODE) {
            user.setString("username", result.getJSONObject("data").getString("username"))
            user.setString("token", result.getJSONObject("data").getString("token"))
            user.setString("cookie", result.getJSONObject("data").getString("sessionKey"))
            user.setString("wallet", result.getJSONObject("data").getString("wallet"))
            user.setBoolean("canPlay", result.getJSONObject("data").getBoolean("canPlay"))
            user.setString("role", result.getJSONObject("data").getString("role"))
            user.setString("name", result.getJSONObject("data").getString("name"))
            user.setString("email", result.getJSONObject("data").getString("email"))
            user.setString("phone", result.getJSONObject("data").getString("phone"))
            user.setString("whatsapp", result.getJSONObject("data").getString("whatsapp"))
            user.setBoolean("active", result.getJSONObject("data").getBoolean("active"))
            user.setString("dollar", result.getJSONObject("data").getString("dollar"))
            user.setString("balance", result.getJSONObject("data").getString("balance"))
            user.setString("balanceDogeBug", result.getJSONObject("data").getString("balanceDogeBug"))
            user.setInteger("progress", result.getJSONObject("data").getInt("progress"))
            user.setString("totalLimit", result.getJSONObject("data").getString("total"))
            user.setBoolean("queue", result.getJSONObject("data").getBoolean("queue"))
            user.setString("lastPackage", result.getJSONObject("data").getString("lastPackage"))
            user.setInteger("position", result.getJSONObject("data").getInt("position"))
            move = Intent(applicationContext, NavigationActivity::class.java)
            move.putExtra("balance", result.getJSONObject("data").getString("balance"))
            move.putExtra("balanceDogeBug", result.getJSONObject("data").getString("balanceDogeBug"))
            user.setBoolean("isLogout", false)
            startActivity(move)
            loading.closeDialog()
            finishAffinity()
          } else {
            Toast.makeText(applicationContext, "your application is not up to date", Toast.LENGTH_SHORT).show()
            loading.closeDialog()
          }
        } else {
          Toast.makeText(applicationContext, result.getString("data"), Toast.LENGTH_SHORT).show()
          loading.closeDialog()
        }
      }
    }
  }

  private fun doRequestPermission() {
    requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA), 100)
  }

  private fun validatePermission(): Boolean {
    return when {
      ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED -> {
        false
      }
      ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED -> {
        false
      }
      ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED -> {
        false
      }
      else -> {
        true
      }
    }
  }
}
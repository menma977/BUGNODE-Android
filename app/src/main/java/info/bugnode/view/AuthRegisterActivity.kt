package info.bugnode.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import info.bugnode.R
import info.bugnode.config.Loading
import info.bugnode.controller.WebController
import info.bugnode.model.User
import okhttp3.FormBody
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.schedule

class AuthRegisterActivity : AppCompatActivity() {
  private lateinit var loading: Loading
  private lateinit var user: User
  private lateinit var jsonObject: JSONObject
  private lateinit var nameEditText: EditText
  private lateinit var usernameEditText: EditText
  private lateinit var emailEditText: EditText
  private lateinit var phoneEditText: EditText
  private lateinit var registerButton: Button

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_auth_register)

    user = User(this)
    loading = Loading(this)

    nameEditText = findViewById(R.id.editTextName)
    usernameEditText = findViewById(R.id.editTextUsername)
    emailEditText = findViewById(R.id.editTextEmail)
    phoneEditText = findViewById(R.id.editTextPhone)
    registerButton = findViewById(R.id.buttonRegister)

    registerButton.setOnClickListener {
      when {
        nameEditText.text.isEmpty() -> {
          Toast.makeText(this, "Name required", Toast.LENGTH_SHORT).show()
          nameEditText.requestFocus()
        }
        usernameEditText.text.isEmpty() -> {
          Toast.makeText(this, "Username required", Toast.LENGTH_SHORT).show()
          usernameEditText.requestFocus()
        }
        emailEditText.text.isEmpty() -> {
          Toast.makeText(this, "Email required", Toast.LENGTH_SHORT).show()
          emailEditText.requestFocus()
        }
        phoneEditText.text.isEmpty() -> {
          Toast.makeText(this, "Phone required", Toast.LENGTH_SHORT).show()
          phoneEditText.requestFocus()
        }
        else -> {
          loading.openDialog()
          onRegister()
        }
      }
    }
  }

  private fun onRegister() {
    val body = FormBody.Builder()
    body.addEncoded("sponsor", user.getString("username"))
    body.addEncoded("name", nameEditText.text.toString())
    body.addEncoded("username", usernameEditText.text.toString())
    body.addEncoded("email", emailEditText.text.toString())
    body.addEncoded("phone", phoneEditText.text.toString())
    Timer().schedule(100) {
      jsonObject = WebController.Post("register", "", body).call()
      if (jsonObject.getInt("code") == 200) {
        runOnUiThread {
          Toast.makeText(applicationContext, jsonObject.getJSONObject("data").getString("message"), Toast.LENGTH_SHORT).show()
          loading.closeDialog()
          finish()
        }
      } else {
        runOnUiThread {
          Toast.makeText(applicationContext, jsonObject.getString("data"), Toast.LENGTH_LONG).show()
          loading.closeDialog()
        }
      }
    }
  }
}
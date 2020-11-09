package info.bugnode.view

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import info.bugnode.R
import info.bugnode.config.Loading
import info.bugnode.controller.WebController
import okhttp3.FormBody
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.schedule

class RegisterActivity : AppCompatActivity() {
  private lateinit var loading: Loading
  private lateinit var jsonObject: JSONObject
  private lateinit var sponsorEditText: EditText
  private lateinit var nameEditText: EditText
  private lateinit var usernameEditText: EditText
  private lateinit var emailEditText: EditText
  private lateinit var phoneEditText: EditText
  private lateinit var passwordEditText: EditText
  private lateinit var passwordConfirmationEditText: EditText
  private lateinit var numberPasswordEditText: EditText
  private lateinit var numberPasswordConfirmationEditText: EditText
  private lateinit var registerButton: Button
  private lateinit var loginTextView: TextView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_register)

    loading = Loading(this)

    sponsorEditText = findViewById(R.id.editTextSponsor)
    nameEditText = findViewById(R.id.editTextName)
    usernameEditText = findViewById(R.id.editTextUsername)
    emailEditText = findViewById(R.id.editTextEmail)
    phoneEditText = findViewById(R.id.editTextPhone)
    passwordEditText = findViewById(R.id.editTextPassword)
    passwordConfirmationEditText = findViewById(R.id.editTextPasswordConfirmation)
    numberPasswordEditText = findViewById(R.id.editTextNumberPassword)
    numberPasswordConfirmationEditText = findViewById(R.id.editTextNumberPasswordConfirmation)
    registerButton = findViewById(R.id.buttonRegister)
    loginTextView = findViewById(R.id.textVIewLogin)

    registerButton.setOnClickListener {
      when {
        sponsorEditText.text.isEmpty() -> {
          Toast.makeText(this, "Sponsor required", Toast.LENGTH_SHORT).show()
          sponsorEditText.requestFocus()
        }
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
        passwordEditText.text.isEmpty() -> {
          Toast.makeText(this, "Password required", Toast.LENGTH_SHORT).show()
          passwordEditText.requestFocus()
        }
        passwordConfirmationEditText.text.isEmpty() -> {
          Toast.makeText(this, "Password confirmation required", Toast.LENGTH_SHORT).show()
          passwordConfirmationEditText.requestFocus()
        }
        numberPasswordEditText.text.isEmpty() -> {
          Toast.makeText(this, "Secondary password required", Toast.LENGTH_SHORT).show()
          numberPasswordEditText.requestFocus()
        }
        numberPasswordConfirmationEditText.text.isEmpty() -> {
          Toast.makeText(this, "Secondary password confirmation required", Toast.LENGTH_SHORT).show()
          numberPasswordConfirmationEditText.requestFocus()
        }
        else -> {
          loading.openDialog()
          onRegister()
        }
      }
    }

    loginTextView.setOnClickListener {
      finish()
    }
  }

  private fun onRegister() {
    val body = FormBody.Builder()
    body.addEncoded("sponsor", sponsorEditText.text.toString())
    body.addEncoded("name", nameEditText.text.toString())
    body.addEncoded("username", usernameEditText.text.toString())
    body.addEncoded("email", emailEditText.text.toString())
    body.addEncoded("phone", phoneEditText.text.toString())
    body.addEncoded("password", passwordEditText.text.toString())
    body.addEncoded("password_confirmation", passwordConfirmationEditText.text.toString())
    body.addEncoded("password_key", numberPasswordEditText.text.toString())
    body.addEncoded("password_key_confirmation", numberPasswordConfirmationEditText.text.toString())
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
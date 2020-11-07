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
  private lateinit var firstNumber: TextView
  private lateinit var secondNumber: TextView
  private lateinit var result: EditText
  private var numberOne = 0
  private var numberTow = 0

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_forgot_password)

    loading = Loading(this)

    email = findViewById(R.id.editTextEmail)
    emailSend = findViewById(R.id.buttonSend)
    login = findViewById(R.id.textVIewLogin)
    firstNumber = findViewById(R.id.textViewFirstNumber)
    secondNumber = findViewById(R.id.textViewSecondNumber)
    result = findViewById(R.id.editTextResult)

    numberOne = (1..20).random()
    numberTow = (1..20).random()

    firstNumber.text = numberOne.toString()
    secondNumber.text = numberTow.toString()

    emailSend.setOnClickListener {
      loading.openDialog()
      when {
        email.text.isEmpty() -> {
          Toast.makeText(this, "Email required", Toast.LENGTH_SHORT).show()
          loading.closeDialog()
          email.requestFocus()
        }
        result.text.isEmpty() -> {
          Toast.makeText(this, "Result required", Toast.LENGTH_SHORT).show()
          loading.closeDialog()
          result.requestFocus()
        }
        (numberOne + numberTow) != result.text.toString().toInt() -> {
          Toast.makeText(this, "calculation wrong", Toast.LENGTH_SHORT).show()

          numberOne = (1..9).random()
          numberTow = (1..9).random()

          firstNumber.text = numberOne.toString()
          secondNumber.text = numberTow.toString()

          loading.closeDialog()
          result.requestFocus()
        }
        else -> {
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
              }
            }
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
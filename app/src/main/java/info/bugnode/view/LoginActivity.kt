package info.bugnode.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import info.bugnode.R
import info.bugnode.config.Loading
import java.util.*
import kotlin.concurrent.schedule

class LoginActivity : AppCompatActivity() {
  private lateinit var loading: Loading
  private lateinit var move: Intent
  private lateinit var loginButton: Button
  private lateinit var textViewRegister: TextView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)

    loading = Loading(this)

    loginButton = findViewById(R.id.buttonLogin)
    textViewRegister = findViewById(R.id.textViewRegister)

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
}
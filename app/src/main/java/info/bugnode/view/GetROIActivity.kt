package info.bugnode.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import info.bugnode.MainActivity
import info.bugnode.R
import info.bugnode.config.BitCoinFormat
import info.bugnode.config.Loading
import info.bugnode.controller.WebController
import info.bugnode.model.User
import kotlinx.android.synthetic.main.activity_get_r_o_i.*
import java.util.*
import kotlin.concurrent.schedule

class GetROIActivity : AppCompatActivity() {
  private lateinit var user: User
  private lateinit var loading: Loading
  private lateinit var balance: TextView
  private lateinit var claim: Button
  private var isBreak = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_get_r_o_i)

    isBreak = false

    user = User(this)
    loading = Loading(this)
    balance = findViewById(R.id.textViewRoi)
    claim = findViewById(R.id.buttonClaim)

    this.textViewUsername.text = user.getString("username")

    claim.setOnClickListener {
      loading.openDialog()
      Timer().schedule(100) {
        val response = WebController.Get("bonus.roi.post", user.getString("token")).call()
        if (response.getInt("code") == 200) {
          runOnUiThread {
            Toast.makeText(applicationContext, response.getString("data"), Toast.LENGTH_SHORT).show()
            loading.closeDialog()
          }
        } else {
          runOnUiThread {
            Toast.makeText(applicationContext, response.getString("data"), Toast.LENGTH_SHORT).show()
            loading.closeDialog()
          }
        }
      }
    }
    startROI()
  }

  private fun startROI() {
    var target = 500
    var time = System.currentTimeMillis()
    Timer().schedule(100) {
      while (true) {
        if (isBreak) {
          break
        } else {
          val delta = System.currentTimeMillis() - time
          if (delta > target) {
            time = System.currentTimeMillis()
            val response = WebController.Get("bonus.roi.get", user.getString("token")).call()
            if (response.getInt("code") == 200) {
              runOnUiThread {
                balance.text = BitCoinFormat.decimalToDoge(response.getJSONObject("data").getString("roi").toBigDecimal()).toPlainString()
                target = 2000
              }
            } else {
              if (response.getString("data").contains("Unauthenticated.")) {
                user.setBoolean("isLogout", true)
                target = 1000
                break
              } else {
                runOnUiThread {
                  user.setBoolean("isLogout", false)
                  balance.text = "timeout. please wait for it to respond"
                  target = 10000
                }
              }
            }
          }
        }
      }

      if (user.getBoolean("isLogout")) {
        user.clear()
        val move = Intent(applicationContext, MainActivity::class.java)
        startActivity(move)
      }
    }
  }

  override fun onResume() {
    super.onResume()
    isBreak = false
    startROI()
  }

  override fun onDestroy() {
    isBreak = true
    super.onDestroy()
  }

  override fun onStop() {
    isBreak = true
    super.onStop()
  }

  override fun onBackPressed() {
    isBreak = true
    super.onBackPressed()
  }
}
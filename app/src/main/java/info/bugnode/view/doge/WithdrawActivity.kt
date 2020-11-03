package info.bugnode.view.doge

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import info.bugnode.R
import info.bugnode.config.BitCoinFormat
import info.bugnode.config.Loading
import info.bugnode.controller.WebController
import info.bugnode.model.User
import java.math.BigDecimal
import java.util.*
import kotlin.concurrent.schedule

class WithdrawActivity : AppCompatActivity() {
  private lateinit var user: User
  private lateinit var loading: Loading
  private lateinit var bonus: TextView
  private lateinit var maxWithdraw: TextView
  private lateinit var balance: EditText
  private lateinit var secondaryPassword: EditText
  private lateinit var withdrawButton: Button
  private lateinit var limit: BigDecimal

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_withdraw)

    user = User(this)
    loading = Loading(this)

    bonus = findViewById(R.id.textViewBonus)
    maxWithdraw = findViewById(R.id.textViewMaxWithdraw)
    balance = findViewById(R.id.editTextBalance)
    secondaryPassword = findViewById(R.id.editTextPasswordKey)
    withdrawButton = findViewById(R.id.buttonTopUp)
  }

  override fun onStart() {
    super.onStart()
    loading.openDialog()
    Timer().schedule(100) {
      val response = WebController.Get("bonus.show", user.getString("token")).call()
      if (response.getInt("code") == 200) {
        bonus.text = BitCoinFormat.decimalToDoge(response.getJSONObject("data").getString("bonus").toBigDecimal()).toPlainString()
        maxWithdraw.text = BitCoinFormat.decimalToDoge(response.getJSONObject("data").getString("limit").toBigDecimal()).toPlainString()
        limit = response.getJSONObject("data").getString("limit").toBigDecimal()
        loading.closeDialog()
      } else {
        runOnUiThread {
          Toast.makeText(applicationContext, response.getString("data"), Toast.LENGTH_LONG).show()
          loading.closeDialog()
          finish()
        }
      }
    }
  }
}
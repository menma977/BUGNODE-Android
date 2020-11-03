package info.bugnode.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import info.bugnode.R
import info.bugnode.config.BitCoinFormat
import info.bugnode.config.Loading
import info.bugnode.controller.WebController
import info.bugnode.model.User
import java.util.*
import kotlin.concurrent.schedule

class GetROIActivity : AppCompatActivity() {
  private lateinit var user: User
  private lateinit var loading: Loading
  private lateinit var balance: TextView
  private lateinit var claim: Button

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_get_r_o_i)

    user = User(this)
    loading = Loading(this)
    balance = findViewById(R.id.textViewRoi)
    claim = findViewById(R.id.buttonClaim)

  }

  override fun onStart() {
    super.onStart()
    var target = 1000
    var time = System.currentTimeMillis()
    Timer().schedule(100) {
      while (true) {
        val delta = System.currentTimeMillis() - time
        if (delta > target) {
          time = System.currentTimeMillis()
          val response = WebController.Get("bonus.roi.get", user.getString("token")).call()
          println(response)
          if (response.getInt("code") == 200) {
            target = 1000
            balance.text = BitCoinFormat.decimalToDoge(response.getJSONObject("data").getString("roi").toBigDecimal()).toPlainString()
          } else {
            target = 5000
          }
        }
      }
    }
  }
}
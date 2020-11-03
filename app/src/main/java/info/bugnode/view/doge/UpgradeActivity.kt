package info.bugnode.view.doge

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import info.bugnode.MainActivity
import info.bugnode.R
import info.bugnode.background.Balance999Doge
import info.bugnode.background.DataUser
import info.bugnode.config.BitCoinFormat
import info.bugnode.config.Loading
import info.bugnode.controller.WebController
import info.bugnode.model.User
import okhttp3.FormBody
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class UpgradeActivity : AppCompatActivity() {
  private lateinit var user: User
  private lateinit var loading: Loading
  private lateinit var packageList: ArrayList<String>
  private lateinit var spinnerPackage: Spinner
  private lateinit var intentGetUser: Intent
  private lateinit var intentGetBalance: Intent
  private lateinit var move: Intent
  private lateinit var balanceTextView: TextView
  private lateinit var balanceDogeBugTextView: TextView
  private lateinit var passwordKeyEditText: EditText
  private lateinit var topUpButton: Button

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_upgrade)

    user = User(this)
    loading = Loading(this)


    balanceTextView = findViewById(R.id.textViewDogeBalance)
    balanceDogeBugTextView = findViewById(R.id.textViewDogeBugBalance)
    passwordKeyEditText = findViewById(R.id.editTextPasswordKey)
    topUpButton = findViewById(R.id.buttonTopUp)
    spinnerPackage = findViewById(R.id.packageSpinner)

    setSpinner()

    balanceTextView.text = BitCoinFormat.decimalToDoge(user.getString("balance").toBigDecimal()).toPlainString()
    balanceDogeBugTextView.text = BitCoinFormat.decimalToDoge(user.getString("balanceDogeBug").toBigDecimal()).toPlainString()

    topUpButton.setOnClickListener {
      when {
        passwordKeyEditText.text.isEmpty() -> {
          Toast.makeText(this, "Secondary Password required", Toast.LENGTH_SHORT).show()
          passwordKeyEditText.requestFocus()
        }
        passwordKeyEditText.text.length < 6 -> {
          Toast.makeText(this, "Secondary password must be 6 digit numbers", Toast.LENGTH_SHORT).show()
          passwordKeyEditText.requestFocus()
        }
        else -> {
          loading.openDialog()
          topUp()
        }
      }
    }
  }

  private fun topUp() {
    val body = FormBody.Builder()
    val value = BitCoinFormat.dogeToDecimal(spinnerPackage.selectedItem.toString().toBigDecimal()).toPlainString()
    body.addEncoded("balance", user.getString("balance"))
    body.addEncoded("value", value)
    body.addEncoded("secondary_password", passwordKeyEditText.text.toString())
    Timer().schedule(100) {
      val response = WebController.Post("doge.upgrade", user.getString("token"), body).call()
      if (response.getInt("code") == 200) {
        runOnUiThread {
          Toast.makeText(applicationContext, "Your Top Up in process", Toast.LENGTH_LONG).show()
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

  private fun setSpinner() {
    packageList = ArrayList()
    packageList.add("20.00000000")
    var firstPackage = 1000
    while (true) {
      packageList.add("$firstPackage.00000000")
      firstPackage += if (firstPackage in 10000..99999) {
        10000
      } else if (firstPackage in 100000..999999) {
        100000
      } else if (firstPackage == 1000000) {
        break
      } else {
        1000
      }
    }

    spinnerPackage.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, packageList)
  }

  override fun onStart() {
    super.onStart()
    Timer().schedule(1000) {
      intentGetUser = Intent(applicationContext, DataUser::class.java)
      startService(intentGetUser)

      intentGetBalance = Intent(applicationContext, Balance999Doge::class.java)
      startService(intentGetBalance)

      LocalBroadcastManager.getInstance(applicationContext).registerReceiver(broadcastReceiverDataUser, IntentFilter("api.web"))
    }
  }

  override fun onStop() {
    LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiverDataUser)

    stopService(intentGetUser)
    stopService(intentGetBalance)
    super.onStop()
  }

  override fun onBackPressed() {
    stopService(intentGetUser)
    stopService(intentGetBalance)
    finish()
    super.onBackPressed()
  }

  private var broadcastReceiverDataUser: BroadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
      if (user.getBoolean("isLogout")) {
        onLogout()
      } else {
        balanceTextView.text = BitCoinFormat.decimalToDoge(user.getString("balance").toBigDecimal()).toPlainString()
        balanceDogeBugTextView.text = BitCoinFormat.decimalToDoge(user.getString("balanceDogeBug").toBigDecimal()).toPlainString()
      }
    }
  }

  private fun onLogout() {
    Timer().schedule(100) {
      WebController.Get("user.logout", user.getString("token")).call()
      runOnUiThread {
        user.clear()
        move = Intent(applicationContext, MainActivity::class.java)
        loading.closeDialog()
        startActivity(move)
        finishAffinity()
      }
    }
  }
}
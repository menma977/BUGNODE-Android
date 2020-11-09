package info.bugnode.view.doge

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.zxing.Result
import info.bugnode.MainActivity
import info.bugnode.R
import info.bugnode.background.Balance999Doge
import info.bugnode.config.BitCoinFormat
import info.bugnode.config.Loading
import info.bugnode.controller.WebController
import info.bugnode.model.User
import me.dm7.barcodescanner.zxing.ZXingScannerView
import okhttp3.FormBody
import org.json.JSONObject
import java.math.BigDecimal
import java.util.*
import kotlin.concurrent.schedule

class SendDogeActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {
  private lateinit var user: User
  private lateinit var loading: Loading
  private lateinit var response: JSONObject
  private lateinit var frameScanner: FrameLayout
  private lateinit var scannerEngine: ZXingScannerView
  private lateinit var wallet: String
  private lateinit var title: TextView
  private lateinit var userBalance: TextView
  private lateinit var balanceText: EditText
  private lateinit var sendDoge: Button
  private lateinit var walletText: EditText
  private lateinit var secondaryPasswordText: EditText
  private lateinit var intentGetBalance: Intent
  private lateinit var balanceValue: BigDecimal
  private var isHasCode = false
  private var isStart = true
  private lateinit var move: Intent
  private var type = 1

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_send_doge)

    user = User(this)
    loading = Loading(this)

    title = findViewById(R.id.textViewTitle)
    userBalance = findViewById(R.id.textViewBalance)
    walletText = findViewById(R.id.editTextWallet)
    frameScanner = findViewById(R.id.frameLayoutScanner)
    balanceText = findViewById(R.id.editTextBalance)
    secondaryPasswordText = findViewById(R.id.editTextSecondaryPassword)
    sendDoge = findViewById(R.id.buttonSend)

    title.text = intent.getStringExtra("title")
    type = intent.getIntExtra("type", 1)

    initScannerView()

    frameScanner.setOnClickListener {
      if (isStart) {
        scannerEngine.startCamera()
        isStart = false
      }
    }

    if (type == 1) {
      userBalance.text = BitCoinFormat.decimalToDoge(user.getString("balanceDogeBug").toBigDecimal()).toPlainString() + " BOOST"
      balanceValue = user.getString("balanceDogeBug").toBigDecimal()
    } else {
      userBalance.text = BitCoinFormat.decimalToDoge(user.getString("balance").toBigDecimal()).toPlainString() + " LTC"
      balanceValue = user.getString("balance").toBigDecimal()
    }

    sendDoge.setOnClickListener {
      when {
        balanceText.text.isEmpty() -> {
          Toast.makeText(this, "Amount required", Toast.LENGTH_SHORT).show()
          secondaryPasswordText.requestFocus()
        }
        BitCoinFormat.dogeToDecimal(balanceText.text.toString().toBigDecimal()) > balanceValue -> {
          Toast.makeText(this, "Amount exceeds the maximum balance", Toast.LENGTH_SHORT).show()
          secondaryPasswordText.requestFocus()
        }
        secondaryPasswordText.text.isEmpty() -> {
          Toast.makeText(this, "Secondary Password required", Toast.LENGTH_SHORT).show()
          secondaryPasswordText.requestFocus()
        }
        secondaryPasswordText.text.length < 6 -> {
          Toast.makeText(this, "Secondary password must be 6 digit numbers", Toast.LENGTH_SHORT).show()
          secondaryPasswordText.requestFocus()
        }
        else -> {
          loading.openDialog()
          onSend()
        }
      }
    }
  }

  private fun onSend() {
    val body = FormBody.Builder()
    body.addEncoded("wallet", walletText.text.toString())
    body.addEncoded("value", BitCoinFormat.dogeToDecimal(balanceText.text.toString().toBigDecimal()).toEngineeringString())
    body.addEncoded("secondary_password", secondaryPasswordText.text.toString())
    body.addEncoded("type", type.toString())
    Timer().schedule(100) {
      response = WebController.Post("doge.transfer", user.getString("token"), body).call()
      if (response.getInt("code") == 200) {
        runOnUiThread {
          Toast.makeText(applicationContext, response.getJSONObject("data").getString("message"), Toast.LENGTH_LONG).show()
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

  override fun onStart() {
    super.onStart()
    Timer().schedule(1000) {
      intentGetBalance = Intent(applicationContext, Balance999Doge::class.java)
      startService(intentGetBalance)

      LocalBroadcastManager.getInstance(applicationContext).registerReceiver(broadcastReceiverBalance, IntentFilter("api.doge"))
    }
  }

  override fun onStop() {
    LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiverBalance)

    stopService(intentGetBalance)
    super.onStop()
  }

  override fun onBackPressed() {
    stopService(intentGetBalance)
    finish()
    super.onBackPressed()
  }

  private var broadcastReceiverBalance: BroadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
      if (user.getBoolean("isLogout")) {
        onLogout()
      } else {
        val text = if (type == 1) {
          BitCoinFormat.decimalToDoge(user.getString("balanceDogeBug").toBigDecimal()).toPlainString() + " BOOST"
        } else {
          BitCoinFormat.decimalToDoge(user.getString("balance").toBigDecimal()).toPlainString() + " LTC"
        }
        userBalance.text = text
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

  private fun initScannerView() {
    scannerEngine = ZXingScannerView(this)
    scannerEngine.setAutoFocus(true)
    scannerEngine.setResultHandler(this)
    frameScanner.addView(scannerEngine)
  }

  override fun handleResult(rawResult: Result?) {
    if (rawResult?.text?.isNotEmpty()!!) {
      isHasCode = true
      wallet = rawResult.text.toString()
      walletText.setText(wallet)
    } else {
      isHasCode = false
    }
  }

  override fun onPause() {
    scannerEngine.stopCamera()
    super.onPause()
  }
}
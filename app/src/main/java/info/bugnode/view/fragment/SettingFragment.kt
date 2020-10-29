package info.bugnode.view.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import info.bugnode.view.settings.ChangePassword
import info.bugnode.MainActivity
import info.bugnode.R
import info.bugnode.config.BitCoinFormat
import info.bugnode.config.Loading
import info.bugnode.controller.WebController
import info.bugnode.model.User
import info.bugnode.view.LoginActivity
import info.bugnode.view.NavigationActivity

class SettingFragment : Fragment() {
  private lateinit var parentActivity: NavigationActivity
  private lateinit var user: User
  private lateinit var loading: Loading
  private lateinit var dollarTextView: TextView
  private lateinit var balanceTextView: TextView
  private lateinit var balanceDogeBugTextView: TextView
  private lateinit var notification: LinearLayout
  private lateinit var notificationMessage: TextView
  private lateinit var editPassword: TableRow
  private lateinit var editPasswordKey: TableRow
  private lateinit var editPhone: TableRow
  private lateinit var logout: TableRow
  private lateinit var progressBar: ProgressBar
  private lateinit var progressBarTextVIew: TextView

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val root = inflater.inflate(R.layout.fragment_setting, container, false)

    parentActivity = activity as NavigationActivity

    user = User(parentActivity)
    loading = Loading(parentActivity)

    dollarTextView = root.findViewById(R.id.textViewDollar)
    balanceTextView = root.findViewById(R.id.textViewDogeBalance)
    balanceDogeBugTextView = root.findViewById(R.id.textViewDogeBugBalance)
    notification = root.findViewById(R.id.notification)
    notificationMessage = root.findViewById(R.id.textViewNotification)
    progressBar = root.findViewById(R.id.progressBar)
    progressBarTextVIew = root.findViewById(R.id.textViewProgressBar)
    editPassword = root.findViewById(R.id.edit_password)
    editPasswordKey = root.findViewById(R.id.edit_password_key)
    editPhone = root.findViewById(R.id.edit_phone_number)
    logout = root.findViewById(R.id.logout)

    editPassword.setOnClickListener {
      do_edit_password()
    }
    editPasswordKey.setOnClickListener {
      do_edit_password_key()
    }
    editPhone.setOnClickListener {
      do_edit_phone()
    }
    logout.setOnClickListener {
      doLogout()
    }


    if (!user.getBoolean("active")) {
      notificationMessage.text = "Your Account is not ready. please upgrade account"
    } else {
      notification.visibility = LinearLayout.GONE
    }

    dollarTextView.text = BitCoinFormat.decimalToDoge(user.getString("balance").toBigDecimal()).multiply(user.getString("dollar").toBigDecimal()).toPlainString()
    balanceTextView.text = BitCoinFormat.decimalToDoge(user.getString("balance").toBigDecimal()).toPlainString()
    balanceDogeBugTextView.text = BitCoinFormat.decimalToDoge(user.getString("balanceDogeBug").toBigDecimal()).toPlainString()

    progressBar.progress = user.getInteger("progress")
    progressBarTextVIew.text = BitCoinFormat.decimalToDoge(user.getString("totalLimit").toBigDecimal()).toPlainString()

    return root
  }

  override fun onResume() {
    super.onResume()
    LocalBroadcastManager.getInstance(parentActivity).registerReceiver(broadcastReceiverDataUser, IntentFilter("api.web"))
  }

  override fun onDestroy() {
    super.onDestroy()
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverDataUser)
  }

  private var broadcastReceiverDataUser: BroadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
      if (user.getBoolean("isLogout")) {
        parentActivity.onLogout()
      } else {
        if (!user.getBoolean("active")) {
          val message = "Your Account is not ready. please upgrade account"
          notificationMessage.text = message
        } else {
          notification.visibility = LinearLayout.GONE
        }

        dollarTextView.text = BitCoinFormat.decimalToDoge(user.getString("balance").toBigDecimal()).multiply(user.getString("dollar").toBigDecimal()).toPlainString()
        balanceTextView.text = BitCoinFormat.decimalToDoge(user.getString("balance").toBigDecimal()).toPlainString()
        balanceDogeBugTextView.text = BitCoinFormat.decimalToDoge(user.getString("balanceDogeBug").toBigDecimal()).toPlainString()

        progressBar.progress = user.getInteger("progress")
        progressBarTextVIew.text = BitCoinFormat.decimalToDoge(user.getString("totalLimit").toBigDecimal()).toPlainString()
      }
    }
  }

  private fun do_edit_password(){
    val intent = Intent(context, ChangePassword::class.java)
    startActivity(intent)
  }

  private fun do_edit_password_key() {

  }

  private fun do_edit_phone() {

  }

  private fun doLogout() {
    WebController.Get("user.logout", user.getString("token")).call()
    user.clear()
    val intent = Intent(context, MainActivity::class.java)
    startActivity(intent)
    parentActivity.finish()
  }
}
package info.bugnode.view.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import info.bugnode.R
import info.bugnode.config.BitCoinFormat
import info.bugnode.config.Loading
import info.bugnode.model.User
import info.bugnode.view.NavigationActivity
import info.bugnode.view.settings.ChangePassword
import info.bugnode.view.settings.ChangePhone
import info.bugnode.view.settings.ChangeSecondaryPassword
import java.math.BigDecimal

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
  private lateinit var progressBarTargetTextVIew: TextView

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
    progressBarTargetTextVIew = root.findViewById(R.id.textViewTarget)
    editPassword = root.findViewById(R.id.edit_password)
    editPasswordKey = root.findViewById(R.id.edit_password_key)
    editPhone = root.findViewById(R.id.edit_phone_number)
    logout = root.findViewById(R.id.logout)

    editPassword.setOnClickListener {
      doEditPassword()
    }
    editPasswordKey.setOnClickListener {
      doEditPasswordKey()
    }
    editPhone.setOnClickListener {
      doEditPhone()
    }
    logout.setOnClickListener {
      parentActivity.onLogout()
    }


    if (!user.getBoolean("active")) {
      notificationMessage.text = "Your Account is not ready. please upgrade account"
    } else {
      notification.visibility = LinearLayout.GONE
    }

    dollarTextView.text = BitCoinFormat.decimalToDoge(user.getString("lastPackage").toBigDecimal()).toPlainString()
    balanceTextView.text = BitCoinFormat.decimalToDoge(user.getString("balance").toBigDecimal()).toPlainString()
    balanceDogeBugTextView.text = BitCoinFormat.decimalToDoge(user.getString("balanceDogeBug").toBigDecimal()).toPlainString()

    progressBar.progress = user.getInteger("progress")
    progressBarTextVIew.text = BitCoinFormat.decimalToDoge(user.getString("totalLimit").toBigDecimal()).toPlainString()
    progressBarTargetTextVIew.text = BitCoinFormat.decimalToDoge(user.getString("lastPackage").toBigDecimal().multiply(BigDecimal(4))).toPlainString()

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

        dollarTextView.text = BitCoinFormat.decimalToDoge(user.getString("lastPackage").toBigDecimal()).toPlainString()
        balanceTextView.text = BitCoinFormat.decimalToDoge(user.getString("balance").toBigDecimal()).toPlainString()
        balanceDogeBugTextView.text = BitCoinFormat.decimalToDoge(user.getString("balanceDogeBug").toBigDecimal()).toPlainString()

        progressBar.progress = user.getInteger("progress")
        progressBarTextVIew.text = BitCoinFormat.decimalToDoge(user.getString("totalLimit").toBigDecimal()).toPlainString()
        progressBarTargetTextVIew.text = BitCoinFormat.decimalToDoge(user.getString("lastPackage").toBigDecimal().multiply(BigDecimal(4))).toPlainString()
      }
    }
  }

  private fun doEditPassword() {
    val intent = Intent(context, ChangePassword::class.java)
    startActivity(intent)
  }

  private fun doEditPasswordKey() {
    val intent = Intent(context, ChangeSecondaryPassword::class.java)
    startActivity(intent)
  }

  private fun doEditPhone() {
    val intent = Intent(context, ChangePhone::class.java)
    startActivity(intent)
  }
}
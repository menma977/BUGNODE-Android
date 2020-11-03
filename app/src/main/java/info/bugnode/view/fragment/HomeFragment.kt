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
import info.bugnode.R
import info.bugnode.config.BitCoinFormat
import info.bugnode.config.Loading
import info.bugnode.model.User
import info.bugnode.view.*
import info.bugnode.view.AuthRegisterActivity
import info.bugnode.view.NavigationActivity
import info.bugnode.view.NetworkActivity
import info.bugnode.view.WebViewActivity
import info.bugnode.view.doge.SendDogeActivity
import info.bugnode.view.doge.UpgradeActivity
import info.bugnode.view.doge.WithdrawActivity
import info.bugnode.view.modal.WalletDialog
import info.bugnode.view.stake.ManualStakeActivity

class HomeFragment : Fragment() {
  private lateinit var move: Intent
  private lateinit var parentActivity: NavigationActivity
  private lateinit var user: User
  private lateinit var loading: Loading
  private lateinit var dollarTextView: TextView
  private lateinit var balanceTextView: TextView
  private lateinit var balanceDogeBugTextView: TextView
  private lateinit var notification: LinearLayout
  private lateinit var notificationMessage: TextView
  private lateinit var progressBar: ProgressBar
  private lateinit var progressBarTextVIew: TextView
  private lateinit var registerButton: LinearLayout
  private lateinit var stakeButton: LinearLayout
  private lateinit var buttonUpgrade: LinearLayout
  private lateinit var buttonHistoryRoi: LinearLayout
  private lateinit var buttonHistoryDoge: LinearLayout
  private lateinit var buttonHistoryDogeBoge: LinearLayout
  private lateinit var buttonHistoryBonus: LinearLayout
  private lateinit var buttonNetwork: LinearLayout
  private lateinit var buttonWithdraw: LinearLayout
  private lateinit var teamLinearLayout: LinearLayout
  private lateinit var sendDogeButton: ImageButton
  private lateinit var sendDogeBugButton: ImageButton
  private lateinit var walletdogeview: ImageView
  private lateinit var walletdogebogeview: ImageView

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val root = inflater.inflate(R.layout.fragment_home, container, false)

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
    registerButton = root.findViewById(R.id.buttonRegister)
    //stakeButton = root.findViewById(R.id.buttonHistoryBonus)
    buttonUpgrade = root.findViewById(R.id.buttonUpgrade)
    buttonNetwork = root.findViewById(R.id.buttonNetwork)
    buttonWithdraw = root.findViewById(R.id.buttonWithdraw)
    teamLinearLayout = root.findViewById(R.id.teamLinearLayout)
    buttonHistoryRoi = root.findViewById(R.id.buttonHistoryRoi)
    buttonHistoryDoge = root.findViewById(R.id.buttonHistoryDoge)
    buttonHistoryDogeBoge = root.findViewById(R.id.buttonHistoryDogeBoge)
    buttonHistoryBonus = root.findViewById(R.id.buttonHistoryBonus)
    sendDogeButton = root.findViewById(R.id.sendDoge)
    sendDogeBugButton = root.findViewById(R.id.sendDogeBug)

    if (!user.getBoolean("active")) {
      notificationMessage.text = "Your Account is not ready. please upgrade account"
    } else {
      notification.visibility = LinearLayout.GONE
    }

    dollarTextView.text = BitCoinFormat.decimalToDoge(user.getString("balance").toBigDecimal()).multiply(user.getString("dollar").toBigDecimal()).toPlainString()
    balanceTextView.text = BitCoinFormat.decimalToDoge(user.getString("balance").toBigDecimal()).toPlainString()
    balanceDogeBugTextView.text = BitCoinFormat.decimalToDoge(user.getString("balanceDogeBug").toBigDecimal()).toPlainString()

    walletdogeview = root.findViewById(R.id.wallet_doge_view)
    walletdogebogeview = root.findViewById(R.id.wallet_dogebug_view)

    progressBar.progress = user.getInteger("progress")
    progressBarTextVIew.text = BitCoinFormat.decimalToDoge(user.getString("totalLimit").toBigDecimal()).toPlainString()

    registerButton.setOnClickListener {
      move = Intent(parentActivity, AuthRegisterActivity::class.java)
      startActivity(move)
    }
    /*
    stakeButton.setOnClickListener {
      move = Intent(parentActivity, ManualStakeActivity::class.java)
      move.putExtra("balance", user.getString("balance"))
      move.putExtra("balanceView", BitCoinFormat.decimalToDoge(user.getString("balance").toBigDecimal()).toPlainString())
      move.putExtra("balanceDogeBugView", BitCoinFormat.decimalToDoge(user.getString("balanceDogeBug").toBigDecimal()).toPlainString())
      startActivity(move)
    }
     */

    buttonUpgrade.setOnClickListener {
      move = Intent(parentActivity, UpgradeActivity::class.java)
      startActivity(move)
    }

    buttonNetwork.setOnClickListener {
      move = Intent(parentActivity, NetworkActivity::class.java)
      startActivity(move)
    }

    teamLinearLayout.setOnClickListener {
      move = Intent(parentActivity, WebViewActivity::class.java)
      move.putExtra("url", "binary.android.sponsor")
      startActivity(move)
    }

    buttonHistoryRoi.setOnClickListener {
      move = Intent(parentActivity, HistoryActivity::class.java)
      move.putExtra("type", "roi")
      startActivity(move)
    }

    buttonHistoryDoge.setOnClickListener {
      move = Intent(parentActivity, HistoryActivity::class.java)
      move.putExtra("type", "doge")
      startActivity(move)
    }

    buttonHistoryDogeBoge.setOnClickListener {
      move = Intent(parentActivity, HistoryActivity::class.java)
      move.putExtra("type", "dogebug")
      startActivity(move)
    }
    buttonHistoryBonus.setOnClickListener {
      move = Intent(parentActivity, HistoryActivity::class.java)
      move.putExtra("type", "bonus")
      startActivity(move)
    }

    sendDogeButton.setOnClickListener {
      move = Intent(parentActivity, SendDogeActivity::class.java)
      move.putExtra("title", "SEND DOGE")
      move.putExtra("type", 2)
      startActivity(move)
    }

    sendDogeBugButton.setOnClickListener {
      move = Intent(parentActivity, SendDogeActivity::class.java)
      move.putExtra("title", "SEND DOGEBOGE")
      move.putExtra("type", 1)
      startActivity(move)
    }

    buttonWithdraw.setOnClickListener {
      move = Intent(parentActivity, WithdrawActivity::class.java)
      startActivity(move)
    }

    walletdogeview.setOnClickListener {
      WalletDialog.show(parentActivity, user.getString("wallet"), false)
    }

    walletdogebogeview.setOnClickListener {
      WalletDialog.show(parentActivity, user.getString("wallet"), true)
    }

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

  override fun onStop() {
    super.onStop()
    LocalBroadcastManager.getInstance(parentActivity).unregisterReceiver(broadcastReceiverDataUser)
  }

  override fun onPause() {
    super.onPause()
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

        dollarTextView.text = BitCoinFormat.toDollar(user.getString("balance").toBigDecimal()).multiply(user.getString("dollar").toBigDecimal()).toPlainString()
        balanceTextView.text = BitCoinFormat.decimalToDoge(user.getString("balance").toBigDecimal()).toPlainString()
        balanceDogeBugTextView.text = BitCoinFormat.decimalToDoge(user.getString("balanceDogeBug").toBigDecimal()).toPlainString()

        progressBar.progress = user.getInteger("progress")
        progressBarTextVIew.text = BitCoinFormat.decimalToDoge(user.getString("totalLimit").toBigDecimal()).toPlainString()

        if (user.getBoolean("queue")) {
          //stakeButton.isEnabled = false
          sendDogeButton.isEnabled = false
          sendDogeBugButton.isEnabled = false
          buttonUpgrade.isEnabled = false
        } else {
          //stakeButton.isEnabled = true
          sendDogeButton.isEnabled = true
          sendDogeBugButton.isEnabled = true
          buttonUpgrade.isEnabled = true
        }
      }
    }
  }
}
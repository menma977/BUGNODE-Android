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
import info.bugnode.view.doge.SendDogeActivity
import info.bugnode.view.doge.UpgradeActivity
import info.bugnode.view.doge.WithdrawActivity
import java.math.BigDecimal

class HomeFragment : Fragment() {
  private lateinit var move: Intent
  private lateinit var parentActivity: NavigationActivity
  private lateinit var user: User
  private lateinit var loading: Loading
  private lateinit var usernameTextView: TextView
  private lateinit var textLevel: TextView
  private lateinit var balanceTextView: TextView
  private lateinit var balanceDogeBugTextView: TextView
  private lateinit var notification: LinearLayout
  private lateinit var notificationMessage: TextView
  private lateinit var progressBar: ProgressBar
  private lateinit var progressBarTextVIew: TextView
  private lateinit var progressBarTargetTextVIew: TextView
  private lateinit var registerButton: LinearLayout
  private lateinit var buttonUpgrade: LinearLayout
  private lateinit var buttonHistoryRoi: LinearLayout
  private lateinit var buttonHistoryDoge: LinearLayout
  private lateinit var buttonHistoryDogeBoge: LinearLayout
  private lateinit var buttonHistoryBonus: LinearLayout
  private lateinit var buttonNetwork: LinearLayout
  private lateinit var buttonWithdraw: LinearLayout
  private lateinit var buttonRoi: LinearLayout
  private lateinit var teamLinearLayout: LinearLayout
  private lateinit var walletDogeImageView: ImageView
  private lateinit var walletDogeBogeImageView: ImageView
  private lateinit var contentLinearLayout: LinearLayout
  private lateinit var imageUpgrade: ImageView

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val root = inflater.inflate(R.layout.fragment_home, container, false)

    parentActivity = activity as NavigationActivity

    user = User(parentActivity)
    loading = Loading(parentActivity)

    usernameTextView = root.findViewById(R.id.textViewUsername)
    textLevel = root.findViewById(R.id.textViewLevel)
    balanceTextView = root.findViewById(R.id.textViewDogeBalance)
    balanceDogeBugTextView = root.findViewById(R.id.textViewDogeBugBalance)
    notification = root.findViewById(R.id.notification)
    notificationMessage = root.findViewById(R.id.textViewNotification)
    progressBar = root.findViewById(R.id.progressBar)
    progressBarTextVIew = root.findViewById(R.id.textViewProgressBar)
    progressBarTargetTextVIew = root.findViewById(R.id.textViewTarget)
    registerButton = root.findViewById(R.id.buttonRegister)
    buttonUpgrade = root.findViewById(R.id.buttonUpgrade)
    buttonNetwork = root.findViewById(R.id.buttonNetwork)
    buttonWithdraw = root.findViewById(R.id.buttonWithdraw)
    buttonRoi = root.findViewById(R.id.buttonRoi)
    teamLinearLayout = root.findViewById(R.id.teamLinearLayout)
    buttonHistoryRoi = root.findViewById(R.id.buttonHistoryRoi)
    buttonHistoryDoge = root.findViewById(R.id.buttonHistoryDoge)
    buttonHistoryDogeBoge = root.findViewById(R.id.buttonHistoryDogeBoge)
    buttonHistoryBonus = root.findViewById(R.id.buttonHistoryBonus)
    contentLinearLayout = root.findViewById(R.id.contentLinearLayout)
    imageUpgrade = root.findViewById(R.id.upgradeImage)

    if (!user.getBoolean("active")) {
      notificationMessage.text = "Your account is not in a network position yet, please top up. The wallet on this account is working"
    } else {
      notification.visibility = LinearLayout.GONE
    }

    textLevel.text = "Level :${user.getInteger("level")}"
    usernameTextView.text = user.getString("username")
    balanceTextView.text = BitCoinFormat.decimalToDoge(user.getString("balance").toBigDecimal()).toPlainString()
    balanceDogeBugTextView.text = BitCoinFormat.decimalToDoge(user.getString("balanceDogeBug").toBigDecimal()).toPlainString()

    walletDogeImageView = root.findViewById(R.id.wallet_doge_view)
    walletDogeBogeImageView = root.findViewById(R.id.wallet_dogebug_view)

    progressBar.progress = user.getInteger("progress")
    progressBarTextVIew.text = BitCoinFormat.decimalToDoge(user.getString("totalLimit").toBigDecimal()).toPlainString()
    progressBarTargetTextVIew.text = BitCoinFormat.decimalToDoge(user.getString("lastPackage").toBigDecimal().multiply(BigDecimal(4))).toPlainString()

    buttonUpgrade.setOnClickListener {
      if (!user.getBoolean("active")) {
        move = Intent(parentActivity, UpgradeActivity::class.java)
        startActivity(move)
      } else if (progressBar.progress >= 90) {
        move = Intent(parentActivity, UpgradeActivity::class.java)
        startActivity(move)
      } else {
        Toast.makeText(parentActivity, "You must complete the previous package rations", Toast.LENGTH_LONG).show()
      }
    }

    registerButton.setOnClickListener {
      move = Intent(parentActivity, AuthRegisterActivity::class.java)
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
      move.putExtra("type", "boost")
      startActivity(move)
    }
    buttonHistoryBonus.setOnClickListener {
      move = Intent(parentActivity, HistoryActivity::class.java)
      move.putExtra("type", "bonus")
      startActivity(move)
    }

    buttonWithdraw.setOnClickListener {
      move = Intent(parentActivity, WithdrawActivity::class.java)
      startActivity(move)
    }

    buttonRoi.setOnClickListener {
      move = Intent(parentActivity, GetROIActivity::class.java)
      startActivity(move)
    }

    walletDogeImageView.setOnClickListener {
      move = Intent(parentActivity, SendDogeActivity::class.java)
      move.putExtra("title", "SEND DOGE")
      move.putExtra("type", 2)
      startActivity(move)
    }

    walletDogeBogeImageView.setOnClickListener {
      move = Intent(parentActivity, SendDogeActivity::class.java)
      move.putExtra("title", "SEND BOGE")
      move.putExtra("type", 1)
      startActivity(move)
    }

    onQueue()
    isActive()

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
          val message = "Your account is not in a network position yet, please top up. The wallet on this account is working"
          notificationMessage.text = message
        } else {
          notification.visibility = LinearLayout.GONE
        }

        balanceTextView.text = BitCoinFormat.decimalToDoge(user.getString("balance").toBigDecimal()).toPlainString()
        balanceDogeBugTextView.text = BitCoinFormat.decimalToDoge(user.getString("balanceDogeBug").toBigDecimal()).toPlainString()

        progressBar.progress = user.getInteger("progress")
        progressBarTextVIew.text = BitCoinFormat.decimalToDoge(user.getString("totalLimit").toBigDecimal()).toPlainString()
        progressBarTargetTextVIew.text = BitCoinFormat.decimalToDoge(user.getString("lastPackage").toBigDecimal().multiply(BigDecimal(4))).toPlainString()

        onQueue()
        isActive()
      }
    }
  }

  private fun onQueue() {
    if (user.getBoolean("queue")) {
      //stakeButton.isEnabled = false
      walletDogeImageView.isEnabled = false
      walletDogeBogeImageView.isEnabled = false
      buttonRoi.isEnabled = false
      buttonUpgrade.isEnabled = false
      imageUpgrade.setImageResource(R.drawable.upgrade_disebale)
    } else {
      //stakeButton.isEnabled = true
      walletDogeImageView.isEnabled = true
      walletDogeBogeImageView.isEnabled = true
      buttonRoi.isEnabled = true
      buttonUpgrade.isEnabled = true
      imageUpgrade.setImageResource(R.drawable.upgrade_active)
    }
  }

  private fun isActive() {
    if (user.getBoolean("active") && progressBar.progress in 1..90) {
      imageUpgrade.setImageResource(R.drawable.upgrade_disebale)
    } else {
      imageUpgrade.setImageResource(R.drawable.upgrade_active)
    }

    if (user.getBoolean("active")) {
      //stakeButton.isEnabled = true
      //sendDogeButton.isEnabled = true
      //sendDogeBugButton.isEnabled = true
      buttonUpgrade.isEnabled = true
      buttonRoi.isEnabled = true
      registerButton.isEnabled = true
      buttonNetwork.isEnabled = true
      teamLinearLayout.isEnabled = true
      buttonWithdraw.isEnabled = true

      contentLinearLayout.visibility = LinearLayout.VISIBLE
    } else {
      //stakeButton.isEnabled = false
      //sendDogeButton.isEnabled = false
      //sendDogeBugButton.isEnabled = false
      buttonUpgrade.isEnabled = true
      buttonRoi.isEnabled = false
      registerButton.isEnabled = false
      buttonNetwork.isEnabled = false
      teamLinearLayout.isEnabled = false
      buttonWithdraw.isEnabled = false

      contentLinearLayout.visibility = LinearLayout.GONE
    }
  }
}
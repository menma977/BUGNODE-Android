package info.bugnode.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import info.bugnode.R
import info.bugnode.config.BitCoinFormat
import info.bugnode.config.Loading
import info.bugnode.controller.WebController
import info.bugnode.model.User
import info.bugnode.view.NavigationActivity
import info.bugnode.view.modal.WalletDialog
import kotlinx.android.synthetic.main.fragment_info.*
import java.util.*
import kotlin.concurrent.schedule

class InfoFragment : Fragment() {
  private lateinit var parentActivity: NavigationActivity
  private lateinit var user: User
  private lateinit var loading: Loading
  private lateinit var walletdoge: TextView
  private lateinit var balancedoge: TextView
  private lateinit var walletdogeboge: TextView
  private lateinit var balancedogeboge: TextView
  private lateinit var username: TextView
  private lateinit var name: TextView
  private lateinit var email: TextView
  private lateinit var phone: TextView
  private lateinit var level: TextView
  private lateinit var walletdogeview: LinearLayout
  private lateinit var walletdogebogeview: LinearLayout

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val root = inflater.inflate(R.layout.fragment_info, container, false)

    parentActivity = activity as NavigationActivity

    user = User(parentActivity)
    loading = Loading(parentActivity)

    walletdoge = root.findViewById(R.id.walletdoge)
    balancedoge = root.findViewById(R.id.balancedoge)
    walletdogeboge = root.findViewById(R.id.wallet_dogebug)
    balancedogeboge = root.findViewById(R.id.balancedogebug)
    username = root.findViewById(R.id.username)
    name = root.findViewById(R.id.name)
    email = root.findViewById(R.id.email)
    phone = root.findViewById(R.id.phone)
    level = root.findViewById(R.id.level)

    walletdogeview = root.findViewById(R.id.wallet_doge_view)
    walletdogebogeview = root.findViewById(R.id.wallet_dogebug_view)

    walletdoge.text = user.getString("wallet")
    balancedoge.text = BitCoinFormat.decimalToDoge(user.getString("balance").toBigDecimal()).toPlainString()
    walletdogeboge.text = user.getString("wallet")
    balancedogeboge.text = BitCoinFormat.decimalToDoge(user.getString("balanceDogeBug").toBigDecimal()).toPlainString()
    username.text = user.getString("username")
    name.text = user.getString("name")
    email.text = user.getString("email")
    phone.text = user.getString("phone")
    level.text = user.getInteger("level").toString()

    walletdogeview.setOnClickListener {
      WalletDialog.show(parentActivity, user.getString("wallet"), false)
    }

    walletdogebogeview.setOnClickListener {
      WalletDialog.show(parentActivity, user.getString("wallet"), true)
    }

    loading.openDialog()
    loadLog()

    return root
  }

  private fun loadLog() {
    Timer().schedule(100) {
      val response = WebController.Get("user.log", user.getString("token")).call()
      if (response.getInt("code") == 200) {
        parentActivity.runOnUiThread {
          countTopUp.text = response.getJSONObject("data").getString("totalTopUp")
          dogeBalance.text = BitCoinFormat.decimalToDoge(response.getJSONObject("data").getString("logDoge").toBigDecimal()).toPlainString()
          bogeBalance.text = BitCoinFormat.decimalToDoge(response.getJSONObject("data").getString("logBoge").toBigDecimal()).toPlainString()
          dogeWithdraw.text = BitCoinFormat.decimalToDoge(response.getJSONObject("data").getString("logDogeWithdraw").toBigDecimal()).toPlainString()
          bogeWithdraw.text = BitCoinFormat.decimalToDoge(response.getJSONObject("data").getString("logBogeWithdraw").toBigDecimal()).toPlainString()
          totalTopUp.text = response.getJSONObject("data").getString("dailyPool")
          sumTopReferrer.text = response.getJSONObject("data").getString("dataReferrer")
          loading.closeDialog()
        }
      } else {
        parentActivity.runOnUiThread {
          countTopUp.text = "0"
          dogeBalance.text = "0"
          bogeBalance.text = "0"
          dogeWithdraw.text = "0"
          bogeWithdraw.text = "0"
          totalTopUp.text = "0"
          sumTopReferrer.text = "0"
          loading.closeDialog()
        }
      }
    }
  }
}
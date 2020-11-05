package info.bugnode.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import info.bugnode.MainActivity
import info.bugnode.R
import info.bugnode.background.Balance999Doge
import info.bugnode.background.DataUser
import info.bugnode.config.Loading
import info.bugnode.controller.WebController
import info.bugnode.model.User
import info.bugnode.view.fragment.HomeFragment
import info.bugnode.view.fragment.InfoFragment
import info.bugnode.view.fragment.SettingFragment
import info.bugnode.view.modal.WalletDialog
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.schedule

class NavigationActivity : AppCompatActivity() {
  private lateinit var intentGetUser: Intent
  private lateinit var intentGetBalance: Intent
  private lateinit var user: User
  private lateinit var loading: Loading
  private lateinit var jsonObject: JSONObject
  private lateinit var move: Intent
  private lateinit var linearHome: LinearLayout
  private lateinit var linearBugChain: LinearLayout
  private lateinit var linearInfo: LinearLayout
  private lateinit var linearSetting: LinearLayout
  private lateinit var qrButton: ImageButton

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_navigation)

    user = User(this)
    loading = Loading(this)

    linearHome = findViewById(R.id.linearLayoutHome)
    linearBugChain = findViewById(R.id.linearLayoutBugChain)
    linearInfo = findViewById(R.id.linearLayoutInfo)
    linearSetting = findViewById(R.id.linearLayoutSetting)
    qrButton = findViewById(R.id.imageButtonQr)

    setNavigation()
    val fragment = HomeFragment()
    addFragment(fragment)
  }

  private fun setNavigation() {
    linearHome.setOnClickListener {
      val fragment = HomeFragment()
      addFragment(fragment)
    }

    linearBugChain.setOnClickListener {
      val intent = Intent()
      intent.action = Intent.ACTION_SEND
      intent.putExtra(Intent.EXTRA_TEXT, "Link Yang di share")
      intent.type = "text/plain"
      startActivity(Intent.createChooser(intent, "Share To:"))
    }

    linearInfo.setOnClickListener {
      val fragment = InfoFragment()
      addFragment(fragment)
    }

    linearSetting.setOnClickListener {
      val fragment = SettingFragment()
      addFragment(fragment)
    }

    qrButton.setOnClickListener {
      WalletDialog.show(this, user.getString("wallet"), false)
    }
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
    if (supportFragmentManager.backStackEntryCount == 1) {
      stopService(intentGetUser)
      stopService(intentGetBalance)
      finishAffinity()
    } else {
      super.onBackPressed()
    }
  }

  private var broadcastReceiverDataUser: BroadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
      if (user.getBoolean("isLogout")) {
        onLogout()
      }
    }
  }

  fun onLogout() {
    Timer().schedule(100) {
      jsonObject = WebController.Get("user.logout", user.getString("token")).call()
      runOnUiThread {
        user.clear()
        move = Intent(applicationContext, MainActivity::class.java)
        loading.closeDialog()
        startActivity(move)
        finishAffinity()
      }
    }
  }

  private fun addFragment(fragment: Fragment) {
    val backStateName = fragment.javaClass.simpleName
    val fragmentManager = supportFragmentManager
    val fragmentPopped = fragmentManager.popBackStackImmediate(backStateName, 0)

    if (!fragmentPopped && fragmentManager.findFragmentByTag(backStateName) == null) {
      val fragmentTransaction = fragmentManager.beginTransaction()
      fragmentTransaction.setCustomAnimations(R.anim.design_bottom_sheet_slide_in, R.anim.design_bottom_sheet_slide_out)
      fragmentTransaction.replace(R.id.contentFragment, fragment, backStateName)
      fragmentTransaction.addToBackStack(backStateName)
      fragmentTransaction.commit()
    }
  }
}
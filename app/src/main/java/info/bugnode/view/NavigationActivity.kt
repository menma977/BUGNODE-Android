package info.bugnode.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import info.bugnode.MainActivity
import info.bugnode.R
import info.bugnode.background.Balance999Doge
import info.bugnode.background.DataUser
import info.bugnode.config.Loading
import info.bugnode.controller.WebController
import info.bugnode.model.User
import info.bugnode.view.fragment.HomeFragment
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
  private lateinit var sendDoge: FloatingActionButton
  private lateinit var linearHome: LinearLayout
  private lateinit var linearBugChain: LinearLayout
  private lateinit var linearInfo: LinearLayout
  private lateinit var linearSetting: LinearLayout

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_navigation)

    user = User(this)

    sendDoge = findViewById(R.id.floatingActionButtonSendDoge)
    linearHome = findViewById(R.id.linearLayoutHome)
    linearBugChain = findViewById(R.id.linearLayoutBugChain)
    linearInfo = findViewById(R.id.linearLayoutInfo)
    linearSetting = findViewById(R.id.linearLayoutSetting)

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
      move = Intent(Intent.ACTION_VIEW, Uri.parse("https://dogechain.info/address/${user.getString("wallet")}"))
      startActivity(move)
    }

    linearInfo.setOnClickListener {
      val fragment = HomeFragment()
      addFragment(fragment)
    }

    linearSetting.setOnClickListener {
      val fragment = HomeFragment()
      addFragment(fragment)
    }

    sendDoge.setOnClickListener {}
  }

  override fun onStart() {
    super.onStart()
    intentGetUser = Intent(applicationContext, DataUser::class.java)
    startService(intentGetUser)

    intentGetBalance = Intent(applicationContext, Balance999Doge::class.java)
    startService(intentGetBalance)

    LocalBroadcastManager.getInstance(applicationContext).registerReceiver(broadcastReceiverDataUser, IntentFilter("api.web"))
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
    LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(broadcastReceiverDataUser)
    stopService(intentGetUser)
    stopService(intentGetBalance)
    Timer().schedule(100) {
      jsonObject = WebController.Get("user.logout", user.getString("token")).call()
      runOnUiThread {
        if (jsonObject.getInt("code") == 200) {
          user.clear()
          move = Intent(applicationContext, MainActivity::class.java)
          loading.closeDialog()
          startActivity(move)
          finishAffinity()
        } else {
          if (jsonObject.getString("data").contains("Unauthenticated.")) {
            user.clear()
            move = Intent(applicationContext, MainActivity::class.java)
            loading.closeDialog()
            startActivity(move)
            finishAffinity()
          }
        }
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
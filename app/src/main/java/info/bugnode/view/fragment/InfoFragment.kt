package info.bugnode.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import info.bugnode.R
import info.bugnode.config.BitCoinFormat
import info.bugnode.config.Loading
import info.bugnode.model.User
import info.bugnode.view.NavigationActivity
import info.bugnode.view.modal.WalletDialog

class InfoFragment : Fragment() {
  private lateinit var parentActivity: NavigationActivity
  private lateinit var user: User
  private lateinit var loading: Loading
  //private lateinit var qrcode: ImageView
  private lateinit var walletdoge: TextView
  private lateinit var balancedoge: TextView
  private lateinit var walletdogeboge: TextView
  private lateinit var balancedogeboge: TextView
  private lateinit var username: TextView
  private lateinit var name: TextView
  private lateinit var email: TextView
  private lateinit var phone: TextView

  private lateinit var walletdogeview: LinearLayout
  private lateinit var walletdogebogeview: LinearLayout

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val root = inflater.inflate(R.layout.fragment_info, container, false)

    parentActivity = activity as NavigationActivity

    user = User(parentActivity)
    loading = Loading(parentActivity)

    //qrcode = root.findViewById(R.id.qr_image)
    walletdoge = root.findViewById(R.id.walletdoge)
    balancedoge = root.findViewById(R.id.balancedoge)
    walletdogeboge = root.findViewById(R.id.wallet_dogebug)
    balancedogeboge = root.findViewById(R.id.balancedogebug)
    username = root.findViewById(R.id.username)
    name = root.findViewById(R.id.name)
    email = root.findViewById(R.id.email)
    phone = root.findViewById(R.id.phone)

    walletdogeview = root.findViewById(R.id.wallet_doge_view)
    walletdogebogeview = root.findViewById(R.id.wallet_dogebug_view)

    walletdoge.text = user.getString("wallet")
    balancedoge.text = BitCoinFormat.decimalToDoge(user.getString("balance").toBigDecimal()).toPlainString()
    walletdogeboge.text = user.getString("wallet")
    balancedogeboge.text = BitCoinFormat.decimalToDoge(user.getString("balance").toBigDecimal()).toPlainString()
    username.text = user.getString("username")
    name.text = user.getString("name")
    email.text = user.getString("email")
    phone.text = user.getString("phone")

    walletdogeview.setOnClickListener {
      WalletDialog.show(parentActivity, user.getString("wallet"), false)
    }

    walletdogebogeview.setOnClickListener {
      WalletDialog.show(parentActivity, user.getString("wallet"), true)
    }

    //val barcodeEncoder = BarcodeEncoder()
    //val bitmap = barcodeEncoder.encodeBitmap(user.getString("wallet"), BarcodeFormat.QR_CODE, 500, 500)
    //qrcode.setImageBitmap(bitmap)

    return root
  }

}
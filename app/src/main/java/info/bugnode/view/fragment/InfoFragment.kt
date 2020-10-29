package info.bugnode.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import info.bugnode.R
import info.bugnode.config.BitCoinFormat
import info.bugnode.config.Loading
import info.bugnode.model.User
import info.bugnode.view.NavigationActivity

class InfoFragment : Fragment() {
  private lateinit var parentActivity: NavigationActivity
  private lateinit var user: User
  private lateinit var loading: Loading
  private lateinit var qrcode: ImageView
  private lateinit var wallet: TextView
  private lateinit var balance: TextView
  private lateinit var username: TextView
  private lateinit var name: TextView
  private lateinit var email: TextView
  private lateinit var phone: TextView

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val root = inflater.inflate(R.layout.fragment_info, container, false)

    parentActivity = activity as NavigationActivity

    user = User(parentActivity)
    loading = Loading(parentActivity)

    qrcode = root.findViewById(R.id.qr_image)
    wallet = root.findViewById(R.id.wallet)
    balance = root.findViewById(R.id.balance)
    username = root.findViewById(R.id.username)
    name = root.findViewById(R.id.name)
    email = root.findViewById(R.id.email)
    phone = root.findViewById(R.id.phone)

    wallet.text = user.getString("wallet")
    balance.text = BitCoinFormat.decimalToDoge(user.getString("balance").toBigDecimal()).toPlainString()
    username.text = user.getString("username")
    name.text = user.getString("name")
    email.text = user.getString("email")
    phone.text = user.getString("phone")

    val barcodeEncoder = BarcodeEncoder()
    val bitmap = barcodeEncoder.encodeBitmap(user.getString("wallet"), BarcodeFormat.QR_CODE, 500, 500)
    qrcode.setImageBitmap(bitmap)

    return root
  }

}
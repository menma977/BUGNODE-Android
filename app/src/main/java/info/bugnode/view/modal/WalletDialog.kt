package info.bugnode.view.modal

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import info.bugnode.R

object WalletDialog {
  private val negativeButtonClick = { _: DialogInterface, _: Int ->
  }

  fun show(context: Context, wallet: String, isGold: Boolean) {
    val builder = AlertDialog.Builder(context)
    val layout: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val layoutWithdraw = layout.inflate(R.layout.modal_barcode_dialog, null)
    val textWallet = layoutWithdraw.findViewById(R.id.wallet_text) as TextView
    val imgWallet = layoutWithdraw.findViewById(R.id.qrcode) as ImageView
    val title = layoutWithdraw.findViewById(R.id.title) as TextView
    val icon = layoutWithdraw.findViewById(R.id.icon) as ImageView
    val barcodeEncoder = BarcodeEncoder()
    val bitmap = barcodeEncoder.encodeBitmap(wallet, BarcodeFormat.QR_CODE, 500, 500)
    if (isGold) {
      title.text = "DOGE BOOST Wallet"
      icon.setImageResource(R.drawable.ltc)
    } else {
      title.text = "DOGE BOOST Wallet"
      icon.setImageResource(R.drawable.ltc_symbol)
    }
    imgWallet.setImageBitmap(bitmap)
    textWallet.text = wallet
    val positiveButtonClick = { _: DialogInterface, _: Int ->
      val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
      val clipData = ClipData.newPlainText("Wallet", wallet)
      clipboardManager.setPrimaryClip(clipData)
      Toast.makeText(context, "Doge wallet has been copied", Toast.LENGTH_SHORT).show()
    }
    builder.setView(layoutWithdraw)
    builder.setPositiveButton("Copy", DialogInterface.OnClickListener(positiveButtonClick))
    builder.setNegativeButton("Cancel", DialogInterface.OnClickListener(negativeButtonClick))
    builder.show()
  }

}
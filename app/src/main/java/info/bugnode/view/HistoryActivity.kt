package info.bugnode.view

import android.os.Bundle
import android.util.Log
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import info.bugnode.R
import info.bugnode.config.BitCoinFormat
import info.bugnode.config.Loading
import info.bugnode.controller.DogeController
import info.bugnode.controller.WebController
import info.bugnode.model.User
import okhttp3.FormBody
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.schedule

class HistoryActivity : AppCompatActivity() {
  private lateinit var user: User
  private lateinit var loading: Loading
  private lateinit var table: TableLayout

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.layout_info_table)

    user = User(this)
    loading = Loading(this)
    loading.openDialog()

    table = findViewById(R.id.table)

    findViewById<TextView>(R.id.title).text = user.getString("Bonus History")
    findViewById<TextView>(R.id.name).text = user.getString("username")
    findViewById<TextView>(R.id.total).text = "0"

    Timer().schedule(100) {
      val res: JSONObject = when {
        intent.getStringExtra("type") == "ltc" -> fetchDogeHistory()
        intent.getStringExtra("type") == "boost" -> WebController.Get("doge.index", user.getString("token")).call()
        intent.getStringExtra("type") == "roi" -> WebController.Get("roi.log", user.getString("token")).call()
        else -> WebController.Get("bonus.index", user.getString("token")).call()
      }
      Log.i("response", res.toString())
      runOnUiThread {
        if (res.getInt("code") == 200) {
          if (intent.getStringExtra("type") == "ltc") {
            findViewById<TextView>(R.id.total).text = BitCoinFormat.decimalToDoge(user.getString("balance").toBigDecimal()).toPlainString()
          } else {
            val total = res.getJSONObject("data").getString("total").toBigDecimal()
            findViewById<TextView>(R.id.total).text = BitCoinFormat.decimalToDoge(total).toPlainString()
          }
          val entries = when {
            intent.getStringExtra("type") == "ltc" -> res.getJSONArray("history")
            intent.getStringExtra("type") == "roi" -> res.getJSONObject("data").getJSONArray("history")
            intent.getStringExtra("type") == "boost" -> res.getJSONObject("data").getJSONArray("dogeBugList")
            else -> res.getJSONObject("data").getJSONArray("bonus")
          }
          for (i in 0 until entries.length()) {
            val row = layoutInflater.inflate(R.layout.table_row_bonus, null)
            val layoutRow = row.findViewById<TableRow>(R.id.row)
            val entry = entries.getJSONObject(i)
            row.findViewById<TextView>(R.id.date).text = entry.getString("date")
            if (entry.has("description")) row.findViewById<TextView>(R.id.desc).text = entry.getString("description")
            if (!entry.has("debit") || entry.getString("debit").isEmpty() || entry.getString("debit").toBigDecimal() == BigDecimal(0.0)) {
              if (!entry.has("description")) row.findViewById<TextView>(R.id.desc).text = resources.getString(R.string.income)
              row.findViewById<TextView>(R.id.total).text = BitCoinFormat.decimalToDoge(entry.getString("credit").toBigDecimal()).toPlainString()
              layoutRow.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.Danger))
            } else {
              if (!entry.has("description")) row.findViewById<TextView>(R.id.desc).text = resources.getString(R.string.outcome)
              row.findViewById<TextView>(R.id.total).text = BitCoinFormat.decimalToDoge(entry.getString("debit").toBigDecimal()).toPlainString()
              layoutRow.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.Success))
            }
            table.addView(row)
          }
        } else {
          Toast.makeText(applicationContext, res.getString("data"), Toast.LENGTH_LONG).show()
          finish()
        }
        loading.closeDialog()
      }
    }
  }

  private fun fetchDogeHistory(): JSONObject {
    val body = FormBody.Builder()
    body.addEncoded("a", "GetDeposits")
    body.addEncoded("s", user.getString("cookie"))
    var depores = DogeController.Post(body).call()
    val body1 = FormBody.Builder()
    body1.addEncoded("a", "GetWithdrawals")
    body1.addEncoded("s", user.getString("cookie"))
    var wdres = DogeController.Post(body1).call()
    val ret = JSONArray()
    val returnObj = JSONObject()
    if (depores.getInt("code") == 200) {
      depores = depores.getJSONObject("data")
      val deposits = depores.getJSONArray("Deposits")
      if (deposits.length() > 0) {
        for (i in 0 until deposits.length()) {
          val deposit = deposits[i] as JSONObject
          val v = JSONObject()
          v.put("date", formatDate(deposit.getString("Date")))
          v.put("description", deposit.getString("Address").replace("XFER", "Internal LTC Boost / " + " | " + deposit.getString("TransactionHash")))
          v.put("debit", deposit.getString("Value").toBigDecimal().toPlainString())
          ret.put(v)
        }
      }
      val transfers = depores.getJSONArray("Transfers")
      if (transfers.length() > 0) {
        for (i in 0 until transfers.length()) {
          val transfer = transfers[i] as JSONObject
          val v = JSONObject()
          v.put("date", formatDate(transfer.getString("Date")))
          v.put("description", transfer.getString("Address").replace("XFER", "Internal LTC Boost"))
          v.put("debit", transfer.getString("Value").toBigDecimal().toPlainString())
          ret.put(v)
        }
      }
    } else {
      val v = JSONObject()
      v.put("date", "-")
      v.put("description", "Error occurred while fetching Deposits")
      v.put("debit", "-")
      ret.put(v)
    }
    if (wdres.getInt("code") == 200) {
      wdres = wdres.getJSONObject("data")
      val withdrawals = wdres.getJSONArray("Withdrawals")
      if (withdrawals.length() > 0) {
        for (i in 0 until withdrawals.length()) {
          val withdrawal = withdrawals[i] as JSONObject
          val v = JSONObject()
          v.put("date", formatDate(withdrawal.getString("Completed")))
          v.put("description", withdrawal.getString("Address").replace("XFER", "Internal LTC Boost / " + " | " + withdrawal.getString("TransactionHash")))
          v.put("credit", withdrawal.getString("Value").toBigDecimal().toPlainString())
          ret.put(v)
        }
      }
      val transfers = wdres.getJSONArray("Transfers")
      if (transfers.length() > 0) {
        for (i in 0 until transfers.length()) {
          val transfer = transfers[i] as JSONObject
          val v = JSONObject()
          v.put("date", formatDate(transfer.getString("Completed")))
          v.put("description", transfer.getString("Address").replace("XFER", "Internal LTC Boost"))
          v.put("credit", transfer.getString("Value").toBigDecimal().toPlainString())
          ret.put(v)
        }
      }
    } else {
      val v = JSONObject()
      v.put("date", "-")
      v.put("description", "Error occurred while fetching Withdrawals")
      v.put("credit", "-")
      ret.put(v)
    }

    returnObj.put("code", 200)
    returnObj.put("history", ret)

    return returnObj
  }

  private fun formatDate(date: String): String {
    if (date == "null") return "-/-"
    val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    val formatter = SimpleDateFormat("dd/MM")
    return formatter.format(parser.parse(date)!!)
  }

}
package info.bugnode.view

import android.os.Bundle
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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.schedule

class HistoryActivity : AppCompatActivity() {
    private lateinit var user: User
    private lateinit var loading: Loading
    private lateinit var table: TableLayout
    private lateinit var layout_row: TableRow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_info_table)

        user = User(this)
        loading = Loading(this)
        loading.openDialog()

        table = findViewById(R.id.table)
        layout_row = findViewById(R.id.row)

        findViewById<TextView>(R.id.title).text = user.getString("Bonus History")
        findViewById<TextView>(R.id.name).text = user.getString("name")

        Timer().schedule(100) {
            val res: JSONObject =
                if(savedInstanceState?.getString("type") == "doge")
                    fetchDogeHistory()
                else if (savedInstanceState?.getString("type") == "dogebug")
                    WebController.Get("doge.index", user.getString("token")).call()
                else
                    WebController.Get("bonus.index", user.getString("token")).call()
            runOnUiThread {
                if (res.getInt("code") == 200) {
                    val entries =
                        if(savedInstanceState?.getString("type") == "doge")
                            res.getJSONObject("data").getJSONArray("history")
                        else if (savedInstanceState?.getString("type") == "dogebug")
                            res.getJSONObject("data").getJSONArray("dogeBugList")
                        else
                            res.getJSONObject("data").getJSONArray("bonus")
                    for (i in 0 until entries.length()) {
                        val row = layoutInflater.inflate(R.layout.table_row_bonus, null)
                        val entry = entries.getJSONObject(i)
                        row.findViewById<TextView>(R.id.date).text = entry.getString("date")
                        if(entry.has("description"))
                            row.findViewById<TextView>(R.id.desc).text = entry.getString("description")
                        if(entry.getString("debit").isEmpty() || entry.getDouble("debit") == 0.0){
                            if(!entry.has("description"))
                                row.findViewById<TextView>(R.id.desc).text = resources.getString(R.string.income)
                            row.findViewById<TextView>(R.id.total).text = BitCoinFormat.decimalToDoge(
                                entry.getString(
                                    "credit"
                                ).toBigDecimal()
                            ).toPlainString()
                            layout_row.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.Danger))
                        }else{
                            if(!entry.has("description"))
                                row.findViewById<TextView>(R.id.desc).text = resources.getString(R.string.outcome)
                            row.findViewById<TextView>(R.id.total).text = BitCoinFormat.decimalToDoge(
                                entry.getString(
                                    "debit"
                                ).toBigDecimal()
                            ).toPlainString()
                            layout_row.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.Success))
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

    private fun fetchDogeHistory(): JSONObject{
        val body = FormBody.Builder()
        body.addEncoded("a", "GetDeposits")
        body.addEncoded("s", user.getString("sessionKey"))
        val depores = DogeController.Post(body).call()
        val body1 = FormBody.Builder()
        body1.addEncoded("a", "GetDeposits")
        body1.addEncoded("s", user.getString("sessionKey"))
        body1.addEncoded("a", "GetWithdrawals")
        val wdres = DogeController.Post(body1).call()

        val ret = JSONArray()
        val return_obj = JSONObject()
        if(depores.getInt("code") == 200){
            val deposits = depores.getJSONArray("Deposits")
            for(i in 0..deposits.length()){
                val deposit = deposits[i] as JSONObject
                var v  = JSONObject()
                v.put("date", formatDate(deposit.getString("Date")))
                v.put("description", "Deposit")
                v.put("debit", BitCoinFormat.decimalToDoge(deposit.getString("Value").toBigDecimal()).toPlainString())
                ret.put(v)
            }
            val transfers = depores.getJSONArray("Transfers")
            for(i in 0..transfers.length()){
                val transfer = transfers[i] as JSONObject
                var v  = JSONObject()
                v.put("date", formatDate(transfer.getString("Date")))
                v.put("description", "Transfer Inbound")
                v.put("debit", BitCoinFormat.decimalToDoge(transfer.getString("Value").toBigDecimal()).toPlainString())
                ret.put(v)
            }
        }else{
            var v  = JSONObject()
            v.put("date", "-")
            v.put("description", "Error occurred while fetching Deposits")
            v.put("debit", "-")
            ret.put(v)
        }
        if(wdres.getInt("code") == 200){
            val withdrawals = depores.getJSONArray("Withdrawals")
            for(i in 0..withdrawals.length()){
                val withdrawal = withdrawals[i] as JSONObject
                var v  = JSONObject()
                v.put("date", formatDate(withdrawal.getString("Date")))
                v.put("description", "Withdrawal")
                v.put("credit", BitCoinFormat.decimalToDoge(withdrawal.getString("Value").toBigDecimal()).toPlainString())
                ret.put(v)
            }
            val transfers = depores.getJSONArray("Transfers")
            for(i in 0..transfers.length()){
                val transfer = transfers[i] as JSONObject
                var v  = JSONObject()
                v.put("date", formatDate(transfer.getString("Date")))
                v.put("description", "Transfer Outbound")
                v.put("credit", BitCoinFormat.decimalToDoge(transfer.getString("Value").toBigDecimal()).toPlainString())
                ret.put(v)
            }
        }else{
            var v  = JSONObject()
            v.put("date", "-")
            v.put("description", "Error occurred while fetching Withdrawals")
            v.put("credit", "-")
            ret.put(v)
        }

        return_obj.put("code", 200)
        return_obj.put("history", ret)

        return return_obj
    }

    private fun formatDate(date: String): String{
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val formatter = SimpleDateFormat("dd/MM")
        return formatter.format(parser.parse(date))
    }

}
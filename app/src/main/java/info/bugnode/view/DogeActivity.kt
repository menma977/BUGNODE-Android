package info.bugnode.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import info.bugnode.R
import info.bugnode.config.BitCoinFormat
import info.bugnode.config.Loading
import info.bugnode.controller.WebController
import info.bugnode.model.User
import java.util.*
import kotlin.concurrent.schedule

class DogeActivity : AppCompatActivity() {
    private lateinit var user: User
    private lateinit var table: TableLayout
    private lateinit var loading: Loading

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_info_table)

        user = User(this)
        loading = Loading(this)
        loading.openDialog()

        table = findViewById(R.id.table)

        findViewById<TextView>(R.id.title).text = user.getString("Doge History")
        findViewById<TextView>(R.id.name).text = user.getString("name")
        findViewById<TableRow>(R.id.table_header_doge).visibility = View.VISIBLE
        findViewById<LinearLayout>(R.id.additional_info).visibility = View.VISIBLE
        findViewById<TextView>(R.id.additional_info_title).text = "Balance"

        Timer().schedule(100) {
            val res = WebController.Get("doge.index", user.getString("token")).call()
            runOnUiThread {
                if (res.getInt("code") == 200) {
                    findViewById<TextView>(R.id.additional_info_desc).text = BitCoinFormat.decimalToDoge(res.getJSONObject("data").getString("balanceDogeBug").toBigDecimal()).toPlainString()
                    val dogeBugList = res.getJSONObject("data").getJSONArray("dogeBugList")
                    for (i in 0 until dogeBugList.length()) {
                        val row = layoutInflater.inflate(R.layout.table_row_doge, null)
                        val dogeBug = dogeBugList.getJSONObject(i)
                        row.findViewById<TextView>(R.id.date).text = dogeBug.getString("date")
                        row.findViewById<TextView>(R.id.income).text = BitCoinFormat.decimalToDoge(dogeBug.getString("debit").toBigDecimal()).toPlainString()
                        row.findViewById<TextView>(R.id.outcome).text = BitCoinFormat.decimalToDoge(dogeBug.getString("credit").toBigDecimal()).toPlainString()
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
}
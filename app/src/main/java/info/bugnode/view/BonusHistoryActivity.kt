package info.bugnode.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import info.bugnode.R
import info.bugnode.config.BitCoinFormat
import info.bugnode.config.Loading
import info.bugnode.controller.WebController
import info.bugnode.model.User
import java.util.*
import kotlin.concurrent.schedule

class BonusHistoryActivity : AppCompatActivity() {
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
        findViewById<TextView>(R.id.name).text = user.getString("name")
        findViewById<TableRow>(R.id.table_header_bonus).visibility = View.VISIBLE

        Timer().schedule(100) {
            val res = WebController.Get("bonus.index", user.getString("token")).call()
            runOnUiThread {
                if (res.getInt("code") == 200) {
                    val bonuses = res.getJSONObject("data").getJSONArray("bonus")
                    for (i in 0 until bonuses.length()) {
                        val row = layoutInflater.inflate(R.layout.table_row_bonus, null)
                        val bonus = bonuses.getJSONObject(i)
                        row.findViewById<TextView>(R.id.date).text = bonus.getString("date")
                        row.findViewById<TextView>(R.id.income).text = BitCoinFormat.decimalToDoge(bonus.getString("debit").toBigDecimal()).toPlainString()
                        row.findViewById<TextView>(R.id.outcome).text = BitCoinFormat.decimalToDoge(bonus.getString("credit").toBigDecimal()).toPlainString()
                        row.findViewById<TextView>(R.id.desc).text = bonus.getString("description")
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
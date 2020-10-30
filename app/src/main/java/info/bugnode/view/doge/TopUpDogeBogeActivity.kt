package info.bugnode.view.doge

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import info.bugnode.R
import info.bugnode.config.Loading
import info.bugnode.model.User

class TopUpDogeBogeActivity : AppCompatActivity() {
  private lateinit var user: User
  private lateinit var loading: Loading
  private lateinit var packageList: ArrayList<String>
  private lateinit var spinnerPackage: Spinner
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_top_up_doge_boge)

    user = User(this)
    loading = Loading(this)

    spinnerPackage = findViewById(R.id.packageSpinner)

    setSpinner()
  }

  private fun setSpinner() {
    packageList = ArrayList()
    var firstPackage = 1000
    while (true) {
      packageList.add("$firstPackage.00000000")
      firstPackage += if (firstPackage in 10000..99999) {
        10000
      } else if (firstPackage in 100000..999999) {
        100000
      } else if (firstPackage == 1000000) {
        break
      } else {
        1000
      }
    }

    spinnerPackage.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, packageList)
  }
}
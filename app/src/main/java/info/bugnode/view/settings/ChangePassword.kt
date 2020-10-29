package info.bugnode.view.settings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import info.bugnode.MainActivity
import info.bugnode.R
import info.bugnode.controller.WebController
import info.bugnode.model.User
import info.bugnode.view.LoginActivity
import kotlinx.android.synthetic.main.activity_profile_change.*
import okhttp3.FormBody
import java.util.*
import kotlin.concurrent.schedule

class ChangePassword : AppCompatActivity() {
    private lateinit var user: User

    private lateinit var text_name: EditText
    private lateinit var text_phone: EditText
    private lateinit var text_new_password: EditText
    private lateinit var text_old_password: EditText
    private lateinit var text_confirm_new_password: EditText
    private lateinit var text_secondary_password: EditText
    private lateinit var text_new_secondary_password: EditText
    private lateinit var text_confirm_new_secondary_password: EditText

    private lateinit var submit: Button

    private lateinit var container_secondary: LinearLayout
    private lateinit var container_name: LinearLayout
    private lateinit var container_phone: LinearLayout
    private lateinit var container_password: LinearLayout
    private lateinit var container_secondary_password: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_change)

        user = User(this)

        text_name = findViewById(R.id.edit_name)
        text_phone = findViewById(R.id.edit_phone)
        text_new_password = findViewById(R.id.new_password)
        text_old_password = findViewById(R.id.old_password)
        text_confirm_new_password = findViewById(R.id.new_password_2)
        text_secondary_password = findViewById(R.id.secondary_password)
        text_new_secondary_password = findViewById(R.id.new_secondary_password)
        text_confirm_new_secondary_password = findViewById(R.id.new_secondary_password_2)
        submit = findViewById(R.id.sumbit)

        container_secondary = findViewById(R.id.container_secondary)
        container_name = findViewById(R.id.container_name)
        container_phone = findViewById(R.id.container_phone)
        container_password = findViewById(R.id.container_password)
        container_secondary_password = findViewById(R.id.container_secondary_password)

        container_name.visibility = View.GONE
        container_phone.visibility = View.GONE
        container_secondary_password.visibility = View.GONE

        submit.setOnClickListener{
            changePassword()
        }
    }

    private fun changePassword(){
        if(text_secondary_password.text.toString().isBlank())
            return Toast.makeText(applicationContext, "Secondary password cannot be blank", Toast.LENGTH_SHORT).show()
        if(text_old_password.text.toString().isBlank())
            return Toast.makeText(applicationContext, "Old password cannot be blank", Toast.LENGTH_SHORT).show()
        if(text_new_password.text.toString().isBlank())
            return Toast.makeText(applicationContext, "New password cannot be blank", Toast.LENGTH_SHORT).show()
        if(text_new_password.text.toString() != text_confirm_new_password.text.toString())
            return Toast.makeText(applicationContext, "Password mismatch", Toast.LENGTH_SHORT).show()
        val body = FormBody.Builder()
        body.addEncoded("second_password_key", text_secondary_password.text.toString())
        body.addEncoded("old_password", text_old_password.text.toString())
        body.addEncoded("password", text_new_password.text.toString())
        Timer().schedule(100) {
            val res =
                WebController.Post("user.changePassword", user.getString("token"), body).call()
            runOnUiThread {
                if (res.getInt("code") == 200) {
                    Toast.makeText(applicationContext,"Password Changed successfully, please login to continue",Toast.LENGTH_LONG).show()
                    logout()
                } else {
                    Toast.makeText(applicationContext, res.getString("data"), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun logout(){
        user.clear()
        finish()
    }
}
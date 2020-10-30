package info.bugnode.view.settings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import info.bugnode.R
import info.bugnode.controller.WebController
import info.bugnode.model.User
import info.bugnode.view.LoginActivity
import kotlinx.android.synthetic.main.activity_profile_change.*
import kotlinx.android.synthetic.main.fragment_info.*
import okhttp3.FormBody
import java.util.*
import kotlin.concurrent.schedule

class ChangePhone : AppCompatActivity() {
    private lateinit var user: User

    private lateinit var text_phone: EditText
    private lateinit var text_email: EditText
    private lateinit var text_wa: EditText
    private lateinit var text_secondary_password: EditText

    private lateinit var submit: Button

    private lateinit var container_name: LinearLayout
    private lateinit var container_phone: LinearLayout
    private lateinit var container_password: LinearLayout
    private lateinit var container_secondary_password: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_change)

        user = User(this)

        text_phone = findViewById(R.id.edit_phone)
        text_email = findViewById(R.id.edit_email)
        text_wa = findViewById(R.id.edit_wa)
        text_secondary_password = findViewById(R.id.secondary_password)
        submit = findViewById(R.id.sumbit)

        container_name = findViewById(R.id.container_name)
        container_phone = findViewById(R.id.container_phone)
        container_password = findViewById(R.id.container_password)
        container_secondary_password = findViewById(R.id.container_secondary_password)

        text_phone.setText(user.getString("phone"))
        text_email.setText(user.getString("email"))
        text_wa.setText(user.getString("whatsapp"))

        container_name.visibility = View.GONE
        container_password.visibility = View.GONE
        container_secondary_password.visibility = View.GONE

        submit.setOnClickListener{
            changeContact()
        }
    }

    private fun changeContact(){
        if(text_secondary_password.text.toString().isBlank())
            return Toast.makeText(applicationContext, "Secondary password cannot be blank", Toast.LENGTH_SHORT).show()
        if(text_phone.text.toString().isBlank())
            return Toast.makeText(applicationContext, "Phone number cannot be blank", Toast.LENGTH_SHORT).show()
        if(text_email.text.toString().isBlank())
            return Toast.makeText(applicationContext, "Email cannot be blank", Toast.LENGTH_SHORT).show()
        if(text_wa.text.toString().isBlank())
            return Toast.makeText(applicationContext, "Whatsapp number cannot be blank", Toast.LENGTH_SHORT).show()
        if(!Patterns.PHONE.matcher(text_phone.text.toString()).matches())
            return Toast.makeText(applicationContext, "Phone number using invalid format", Toast.LENGTH_SHORT).show()
        if(!Patterns.EMAIL_ADDRESS.matcher(text_email.text.toString()).matches())
            return Toast.makeText(applicationContext, "Invalid email format", Toast.LENGTH_SHORT).show()
        if(!Patterns.PHONE.matcher(text_wa.text.toString()).matches())
            return Toast.makeText(applicationContext, "Whatsapp number using invalid format", Toast.LENGTH_SHORT).show()
        val body = FormBody.Builder()
        body.addEncoded("second_password_key", text_secondary_password.text.toString())
        body.addEncoded("phone", cleanupNumber(text_phone.text.toString(), true))
        body.addEncoded("email", text_email.text.toString())
        body.addEncoded("whatsapp", cleanupNumber(text_wa.text.toString()))
        Timer().schedule(100) {
            val res =
                WebController.Post("user.changeContact", user.getString("token"), body).call()
            runOnUiThread {
                if (res.getInt("code") == 200) {
                    Toast.makeText(applicationContext,"Contact changed!",Toast.LENGTH_LONG).show()
                    user.setString("email", text_email.text.toString())
                    user.setString("phone", text_phone.text.toString())
                    user.setString("whatsapp", text_wa.text.toString())
                } else {
                    Toast.makeText(applicationContext, res.getString("data"), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun cleanupNumber(number: String, zeroLeading: Boolean=false): String {
        val n = number.trim().removePrefix("0").removePrefix("62").removePrefix("+62")
        return if (zeroLeading) "0${n}" else "62${n}"
    }

}
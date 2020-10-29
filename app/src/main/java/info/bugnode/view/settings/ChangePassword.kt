package info.bugnode

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import info.bugnode.controller.WebController
import info.bugnode.model.User
import info.bugnode.view.LoginActivity
import kotlinx.android.synthetic.main.activity_profile_change.*
import okhttp3.FormBody

class ChangePassword : AppCompatActivity() {
    private lateinit var user: User

    private lateinit var text_name: EditText
    private lateinit var text_phone: EditText
    private lateinit var text_new_password: EditText
    private lateinit var text_old_password: EditText
    private lateinit var text_confirm_new_password: EditText
    private lateinit var text_new_secondary_password: EditText
    private lateinit var text_confirm_new_secondary_password: EditText

    private lateinit var submit: Button

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
        text_new_secondary_password = findViewById(R.id.new_password_key)
        text_confirm_new_secondary_password = findViewById(R.id.new_password_key_2)
        submit = findViewById(R.id.sumbit)
    
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
        if(old_password.text.toString().isBlank())
            return Toast.makeText(applicationContext, "Old password cannot be blank", Toast.LENGTH_SHORT).show()
        if(new_password.text.toString().isBlank())
            return Toast.makeText(applicationContext, "New password cannot be blank", Toast.LENGTH_SHORT).show()
        if(new_password.text.toString() == new_password_2.text.toString())
            return Toast.makeText(applicationContext, "Password mismatch", Toast.LENGTH_SHORT).show()
        val body = FormBody.Builder()
        body.addEncoded("old_password", old_password.text.toString())
        body.addEncoded("password", new_password.text.toString())
        val res = WebController.Post("user.changePassword", user.getString("token"), body).call()
        if(res.getInt("code")==200){
            val dialog = AlertDialog.Builder(applicationContext)
            dialog.setMessage("Password Changed successfully, please login to continue")
            dialog.show()
            logout()
        }else{
            Toast.makeText(applicationContext, res.getJSONObject("data").getString("message"), Toast.LENGTH_SHORT).show()
        }
    }

    private fun logout(){
        user.clear()
        val intent = Intent(applicationContext, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
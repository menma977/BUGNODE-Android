package info.bugnode.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import info.bugnode.R

class ChangePassword : AppCompatActivity() {
    private lateinit var text_name: EditText
    private lateinit var text_phone: EditText
    private lateinit var text_new_password: EditText
    private lateinit var text_old_password: EditText
    private lateinit var text_confirm_new_password: EditText
    private lateinit var text_new_secondary_password: EditText
    private lateinit var text_confirm_new_secondary_password: EditText

    private lateinit var container_user: LinearLayout
    private lateinit var container_phone: LinearLayout
    private lateinit var container_password: LinearLayout
    private lateinit var container_secondary_password: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_change)

        text_name = findViewById(R.id.edit_name)
        text_phone = findViewById(R.id.edit_phone)
    }
}
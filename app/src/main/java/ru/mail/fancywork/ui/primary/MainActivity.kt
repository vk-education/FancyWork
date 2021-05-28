package ru.mail.fancywork.ui.primary

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import ru.mail.fancywork.R
import ru.mail.fancywork.controller.Controller
import ru.mail.fancywork.ui.auth.AuthActivity

class MainActivity : AppCompatActivity() {

    private val controller = Controller()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<androidx.appcompat.widget.Toolbar>(R.id.top_bar)
            .setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.logout -> {
                        controller.logOut()
                        startActivity(Intent(this, AuthActivity::class.java))
                        finish()
                        true
                    }
                    else -> false
                }
            }
    }

    // todo google auth
    public fun loginGoogle(view: View) {
        // БОРЕМСЯ С КОДСТАЙЛОМ
    }

    // todo local auth
    public fun loginWithoutGoogle(view: View) {
        // todo open recycle view activity
    }
}

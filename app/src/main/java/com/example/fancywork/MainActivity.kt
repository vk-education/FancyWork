package com.example.fancywork

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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

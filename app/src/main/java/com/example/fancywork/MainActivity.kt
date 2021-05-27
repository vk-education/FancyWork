package com.example.fancywork

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    //todo google auth
    public fun loginGoogle(view:View){}

    //todo local auth
    public fun loginWithoutGoogle(view:View){
        //todo open recycle view activity
    }
}

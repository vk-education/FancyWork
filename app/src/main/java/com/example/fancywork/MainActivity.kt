package com.example.fancywork

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

// obviously we haven't done anything yet
class MainActivity : AppCompatActivity() {
    companion object {
        private const val PICK_IMAGE = 122
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            val inputStream = this.applicationContext.contentResolver.openInputStream(data.data!!)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            // todo: create new Fragment/Activity and pass bitmap to it

            Toast.makeText(
                this.applicationContext,
                "вы загрузили битмап ${bitmap.width}x${bitmap.height}!",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // todo for butten download image
    public fun download(view: View) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)

        Toast.makeText(
            view.context,
            "вы ткнули на загрузку!",
            Toast.LENGTH_LONG
        ).show()
    }

    // todo for button open scheme
    public fun open(view: View) {
        Toast.makeText(
            view.context,
            "вы ткнули на открытие!",
            Toast.LENGTH_LONG
        ).show()
    }
}

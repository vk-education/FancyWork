package com.example.fancywork

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

class WorkEdit : AppCompatActivity() {

    companion object {
        private const val PICK_IMAGE = 122
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_edit)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            val inputStream = this.applicationContext.contentResolver.openInputStream(data.data!!)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            // todo pass bitmap to work_image ImageView


            Toast.makeText(
                this.applicationContext,
                "вы загрузили битмап ${bitmap.width}x${bitmap.height}!",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    public fun download(view: View) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            PICK_IMAGE
        )

        Toast.makeText(
            view.context,
            "вы ткнули на загрузку!",
            Toast.LENGTH_LONG
        ).show()
    }
}

// todo если нам нужна кнопка загрузки вышивки то вот ее лейаут
//<Button
//android:id="@+id/open"
//android:layout_width="120dp"
//android:layout_height="60dp"
//android:onClick="open"
//android:text="@string/open_text"
//android:backgroundTint="@color/background"
//android:textColor="@color/black"
//app:layout_constraintBottom_toBottomOf="parent"
//app:layout_constraintEnd_toEndOf="parent"
//app:layout_constraintHorizontal_bias="0.173"
//app:layout_constraintStart_toStartOf="parent"
//app:layout_constraintTop_toTopOf="parent"
//app:layout_constraintVertical_bias="0.961" />

//<ImageView
//android:layout_width="120dp"
//android:layout_height="95dp"
//app:srcCompat="@drawable/open_pic"
//android:id="@+id/imageView2"
//android:backgroundTint="@color/background"
//app:layout_constraintTop_toTopOf="parent"
//app:layout_constraintBottom_toBottomOf="parent"
//app:layout_constraintEnd_toEndOf="parent"
//app:layout_constraintStart_toStartOf="parent"
//app:layout_constraintHorizontal_bias="0.171"
//app:layout_constraintVertical_bias="0.849" />

//    // todo for button open scheme
//    public fun open(view: View) {
//        Toast.makeText(
//            view.context,
//            "вы ткнули на открытие!",
//            Toast.LENGTH_LONG
//        ).show()
//    }
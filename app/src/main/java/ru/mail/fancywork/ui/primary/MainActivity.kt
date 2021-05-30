package ru.mail.fancywork.ui.primary

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.mail.fancywork.R
import ru.mail.fancywork.controller.Controller
import ru.mail.fancywork.ui.adapter.FancyworkAdapter

class MainActivity : AppCompatActivity() {
    companion object {
        private const val PICK_IMAGE = 122
        const val BITMAP_MESSAGE = "ru.mail.fancywork.BITMAP_MESSAGE"
    }

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

        val rv: RecyclerView = findViewById(R.id.embroidery_list)
        var ar: ArrayList<Int> = ArrayList()
        ar.add(6)
        ar.add(5)
        ar.add(4)
        var adapter = FancyworkAdapter(ar)

        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(this.applicationContext)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {

            startActivity(Intent(this, WorkspaceActivity::class.java).apply {
                putExtra(BITMAP_MESSAGE, data.data)
            })
        }
    }

    fun add(view: View) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            PICK_IMAGE
        )
    }
}

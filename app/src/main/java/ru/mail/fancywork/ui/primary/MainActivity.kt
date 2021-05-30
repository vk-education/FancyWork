package ru.mail.fancywork.ui.primary

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.mail.fancywork.R
import ru.mail.fancywork.controller.Controller
import ru.mail.fancywork.ui.adapter.FancyworkAdapter
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

        val rv: RecyclerView = findViewById(R.id.embroidery_list)
        var ar: ArrayList<Int> = ArrayList()
        ar.add(6)
        ar.add(5)
        ar.add(4)
        var adapter = FancyworkAdapter(ar)

        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(this.applicationContext)
    }

    public fun add(view:View){
        //todo открыть активити просмотра вышивки
    }
}
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
import ru.mail.fancywork.model.datatype.Fancywork
import ru.mail.fancywork.ui.adapter.FancyworkAdapter

class MainActivity : AppCompatActivity() {
    companion object {
        private const val PICK_IMAGE = 122
        private const val BITMAP_ANSWER = 9001
        const val BITMAP_MESSAGE = "ru.mail.fancywork.BITMAP_MESSAGE"
        const val FANCYWORK_MESSAGE = "ru.mail.fancywork.FANCYWORK_MESSAGE"
    }

    private val controller = Controller()
    private val fullEmbroideryList = ArrayList<Fancywork>()
    private val fancyworkAdapter = FancyworkAdapter(fullEmbroideryList)

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
        val ar = fullEmbroideryList
        ar.add(Fancywork("Вышивка 1", "", 6, 6, 5))
        ar.add(Fancywork("Вышивка 2", "", 5, 5, 10))
        ar.add(Fancywork("Вышивка 3", "", 4, 4, 15))

        rv.adapter = fancyworkAdapter
        rv.layoutManager = LinearLayoutManager(this.applicationContext)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK || data == null) return

        if (requestCode == PICK_IMAGE) {
            startActivityForResult(Intent(this, WorkspaceActivity::class.java).apply {
                putExtra(BITMAP_MESSAGE, data.data)
            }, BITMAP_ANSWER)
        } else if (requestCode == BITMAP_ANSWER) {
            fullEmbroideryList.add(data.getParcelableExtra(FANCYWORK_MESSAGE)!!)
            fancyworkAdapter.notifyDataSetChanged()
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

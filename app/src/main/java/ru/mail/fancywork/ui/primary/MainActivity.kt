package ru.mail.fancywork.ui.primary

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import ru.mail.fancywork.R
import ru.mail.fancywork.controller.Controller
import ru.mail.fancywork.model.datatype.Fancywork
import ru.mail.fancywork.ui.adapter.FancyworkAdapter

class MainActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        private const val PICK_IMAGE = 122
        private const val BITMAP_ANSWER = 9001
        const val BITMAP_MESSAGE = "ru.mail.fancywork.BITMAP_MESSAGE"
        const val FANCYWORK_MESSAGE = "ru.mail.fancywork.FANCYWORK_MESSAGE"
    }

    private lateinit var fancyworks: RecyclerView
    private val controller = Controller()
    private val fullEmbroideryList = ArrayList<Fancywork>()
    private val fancyworkAdapter = FancyworkAdapter(fullEmbroideryList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<SwipeRefreshLayout>(R.id.refresh).setOnRefreshListener {
            refresh()
            findViewById<SwipeRefreshLayout>(R.id.refresh).isRefreshing = false
        }

        findViewById<FloatingActionButton>(R.id.add_fancywork).setOnClickListener(this)
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

        fancyworks = findViewById(R.id.embroidery_list)
        refresh()
        fancyworks.adapter = fancyworkAdapter
        fancyworks.layoutManager = LinearLayoutManager(this.applicationContext)
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

    private fun refresh() {
        lifecycleScope.launch {
            fullEmbroideryList.clear()
            fullEmbroideryList.addAll(controller.getFancyworks() as ArrayList<Fancywork>)
            fancyworks.adapter?.notifyDataSetChanged()
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.add_fancywork -> {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(
                    Intent.createChooser(intent, "Select Picture"),
                    PICK_IMAGE
                )
            }
        }
    }
}

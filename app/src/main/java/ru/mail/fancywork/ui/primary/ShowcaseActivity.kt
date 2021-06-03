package ru.mail.fancywork.ui.primary

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_showcase.*
import kotlinx.coroutines.launch
import ru.mail.fancywork.R
import ru.mail.fancywork.controller.Controller
import ru.mail.fancywork.model.datatype.Fancywork
import ru.mail.fancywork.ui.secondary.ColorGridView
import java.io.File
import java.io.FileOutputStream
import kotlin.math.min

class ShowcaseActivity : AppCompatActivity() {

    companion object {
        const val FANCYWORK_MESSAGE = "ru.mail.fancywork.FANCYWORK_MESSAGE"
    }

    private val controller = Controller()
    private lateinit var fancywork: Fancywork
    private lateinit var bitmap: Bitmap
    private lateinit var colorGridView: ColorGridView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_showcase)
         setSupportActionBar(findViewById(R.id.top_bar_showcase))
        colorGridView = findViewById(R.id.color_grid_view)
        fancywork = intent.getParcelableExtra(FANCYWORK_MESSAGE)!!
        val bmp = fancywork.bitmap

        findViewById<Toolbar>(R.id.top_bar_showcase)
            .setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.share -> {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "image/png"
                        val directory = File(externalCacheDir, "fancyworks").also {
                            if (!it.exists())
                                it.mkdir()
                        }
                        try {
                            val file = File(directory, "fancywork.png").also {
                                if (!it.exists())
                                    it.createNewFile()
                            }
                            val out = FileOutputStream(file)
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                            out.apply {
                                flush()
                                close()
                            }
                            val path = FileProvider.getUriForFile(this, "ru.mail.fancywork", file)
                            intent.putExtra(Intent.EXTRA_STREAM, path)
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                            startActivity(Intent.createChooser(intent, "Share image"))
                            true
                        } catch (e: Exception) {
                            Log.d("Share Exception", e.message.toString())
                            false
                        }
                    }
                    else -> false
                }
            }

        if (bmp != null) {
            bitmap = bmp
            colorGridView.setImage(bitmap, min(fancywork.height, fancywork.width))
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            showcase_pb.visibility = View.VISIBLE
            showcase_view.visibility = View.VISIBLE
            lifecycleScope.launch {
                bitmap = controller.downloadImage(fancywork.image_path)
                fancywork.bitmap = bitmap
                colorGridView.setImage(bitmap, min(fancywork.height, fancywork.width))
                showcase_pb.visibility = View.INVISIBLE
                showcase_view.visibility = View.INVISIBLE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_showcase_actionbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

package ru.mail.fancywork.ui.primary

import android.graphics.Bitmap
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import ru.mail.fancywork.R
import ru.mail.fancywork.model.datatype.Fancywork
import ru.mail.fancywork.ui.secondary.ColorGridView
import kotlin.math.min

class ShowcaseActivity : AppCompatActivity() {

    companion object {
        const val FANCYWORK_MESSAGE = "ru.mail.fancywork.FANCYWORK_MESSAGE"
    }

    private lateinit var fancywork: Fancywork
    private lateinit var bitmap: Bitmap
    private lateinit var colorGridView: ColorGridView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_showcase)

        setSupportActionBar(findViewById(R.id.top_bar))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        colorGridView = findViewById(R.id.color_grid_view)

        fancywork = intent.getParcelableExtra(FANCYWORK_MESSAGE)!!
        val bmp = fancywork.bitmap
        if (bmp != null) {
            bitmap = bmp
            colorGridView.setImage(bitmap, min(fancywork.height, fancywork.width))
        } else {
            // todo download bitmap
        }
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

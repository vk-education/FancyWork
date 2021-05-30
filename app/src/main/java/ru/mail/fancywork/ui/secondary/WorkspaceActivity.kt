package ru.mail.fancywork.ui.secondary

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.slider.Slider
import ru.mail.fancywork.R
import ru.mail.fancywork.model.repo.PixelizationRepository
import ru.mail.fancywork.ui.primary.MainActivity
import ru.mail.fancywork.ui.view.ColorGridView

class WorkspaceActivity : AppCompatActivity() {
    companion object {
        private val pixelRepo = PixelizationRepository()
    }

    private lateinit var originalBitmap: Bitmap
    private lateinit var pixelatedBitmap: Bitmap
    private lateinit var colorGridView: ColorGridView
    private lateinit var scaleSlider: Slider
    private lateinit var colorSlider: Slider
    private var scale = 25
    private var colors = 5

    private fun finalizeEmbroidery() {
        // todo (needs data class)
    }

    private fun pixelate() {
        val ratio = originalBitmap.width / originalBitmap.height.toFloat()
        val isVertical = ratio > 1.0
        val width = if (isVertical) (scale * ratio).toInt() else scale
        val height = if (isVertical) scale else (scale / ratio).toInt()

        // todo (needs better pixelRepo)
        pixelatedBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, false)

        colorGridView.setImage(pixelatedBitmap)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_edit)

        val uri = intent.getParcelableExtra<Uri>(MainActivity.BITMAP_MESSAGE)!!
        val inputStream = this.applicationContext.contentResolver.openInputStream(uri)
        originalBitmap = BitmapFactory.decodeStream(inputStream)

        colorGridView = findViewById(R.id.color_grid_view)
        scaleSlider = findViewById(R.id.scaleSlider)
        colorSlider = findViewById(R.id.colorSlider)

        scaleSlider.value = scale.toFloat()
        colorSlider.value = colors.toFloat()

        scaleSlider.addOnChangeListener { _, value, _ ->
            scale = value.toInt()
            pixelate()
        }
        colorSlider.addOnChangeListener { _, value, _ ->
            colors = value.toInt()
            pixelate()
        }

        pixelate()
    }
}

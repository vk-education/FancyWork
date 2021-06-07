package ru.mail.fancywork.ui.primary

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.slider.Slider
import kotlinx.android.synthetic.main.activity_workspace.*
import kotlinx.coroutines.launch
import ru.mail.fancywork.R
import ru.mail.fancywork.controller.Controller
import ru.mail.fancywork.ui.secondary.ColorGridView

class WorkspaceActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        private const val DEFAULT_SCALE = 25
        private const val DEFAULT_COLORS = 5
    }

    private val controller = Controller()

    private lateinit var originalBitmap: Bitmap
    private lateinit var pixelatedBitmap: Bitmap
    private lateinit var colorGridView: ColorGridView
    private lateinit var scaleSlider: Slider
    private lateinit var colorSlider: Slider
    private lateinit var threadColors: List<Pair<String, Triple<Int, Int, Int>>>
    private var scale = DEFAULT_SCALE
    private var colors = DEFAULT_COLORS
    private var isDirty = true

    private fun pixelate() {
        if (!isDirty) return
        isDirty = false

        val ratio = originalBitmap.width / originalBitmap.height.toFloat()
        val isVertical = ratio > 1.0
        val width = if (isVertical) (scale * ratio).toInt() else scale
        val height = if (isVertical) scale else (scale / ratio).toInt()

        lifecycleScope.launch {
            workspace_pb.visibility = View.VISIBLE
            workspace_view.visibility = View.VISIBLE
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            pixelatedBitmap =
                controller.pixelate(originalBitmap, width, height, colors, threadColors)
            colorGridView.setImage(pixelatedBitmap, scale)
            workspace_pb.visibility = View.INVISIBLE
            workspace_view.visibility = View.INVISIBLE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workspace)

        threadColors = controller.initThreadColors(resources)

        val uri = intent.getParcelableExtra<Uri>(MainActivity.BITMAP_MESSAGE)!!
        val inputStream = this.applicationContext.contentResolver.openInputStream(uri)
        originalBitmap = BitmapFactory.decodeStream(inputStream)

        findViewById<Button>(R.id.save_button).setOnClickListener(this)
        findViewById<Button>(R.id.process_button).setOnClickListener(this)
        setSupportActionBar(findViewById(R.id.top_bar))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        colorGridView = findViewById(R.id.color_grid_view)
        scaleSlider = findViewById(R.id.scaleSlider)
        colorSlider = findViewById(R.id.colorSlider)

        scaleSlider.value = scale.toFloat()
        colorSlider.value = colors.toFloat()

        scaleSlider.addOnChangeListener { _, value, _ ->
            scale = value.toInt()
            isDirty = true
        }
        colorSlider.addOnChangeListener { _, value, _ ->
            colors = value.toInt()
            isDirty = true
        }

        pixelate()
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.save_button -> {
                pixelate()
                lifecycleScope.launch {
                    window.setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    )
                    workspace_pb.visibility = View.VISIBLE
                    workspace_view.visibility = View.VISIBLE
                    val result = controller.addFancywork(
                        pixelatedBitmap,
                        colors,
                        fancywork_title.text.toString()
                    )
                    workspace_pb.visibility = View.INVISIBLE
                    workspace_view.visibility = View.INVISIBLE
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    setResult(
                        Activity.RESULT_OK,
                        Intent().apply {
                            putExtra(MainActivity.FANCYWORK_MESSAGE, result)
                        }
                    )
                    finish()
                }
            }
            R.id.process_button -> {
                pixelate()
            }
        }
    }
}

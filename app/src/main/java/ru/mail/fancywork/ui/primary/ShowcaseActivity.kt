package ru.mail.fancywork.ui.primary

import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.mail.fancywork.R
import ru.mail.fancywork.model.datatype.Fancywork
import ru.mail.fancywork.ui.secondary.ColorGridView
import kotlin.math.min

class ShowcaseActivity : AppCompatActivity() {

    private lateinit var fancywork: Fancywork
    private lateinit var bitmap: Bitmap
    private lateinit var colorGridView: ColorGridView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_showcase)

        // form MainActivity:
        // startActivity(Intent(this, ShowcaseActivity::class.java).apply {
        //     putExtra(FANCYWORK_MESSAGE, fullEmbroideryList[0])
        // })

        colorGridView = findViewById(R.id.color_grid_view)

        fancywork = intent.getParcelableExtra(MainActivity.FANCYWORK_MESSAGE)!!
        val bmp = fancywork.bitmap
        if (bmp != null) {
            bitmap = bmp
            colorGridView.setImage(bitmap, min(fancywork.height, fancywork.width))
        } else {
            // todo download bitmap
        }
    }
}

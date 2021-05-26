package com.example.fancywork.fancyLib

import android.graphics.Bitmap

class FancyPicture(pair: Pair<Bitmap, Array<Array<String?>>>, title: String, id: String) {

    // todo id generator

    var image: Bitmap = pair.first
    var colors: Array<Array<String?>> = pair.second

    var title: String = title
    var id: String = id

    var width: Int
    var length: Int

    init {
        width = colors.size
        length = colors[0].size
    }
}

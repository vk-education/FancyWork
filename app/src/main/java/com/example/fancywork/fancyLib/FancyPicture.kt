package com.example.fancywork.fancyLib

import android.graphics.Bitmap

data class FancyPicture(
    val id: String,
    val title: String,
    val image: Bitmap,
    val colors: List<List<String>>
) {
    // todo id generator
    // todo difficulty definer

    var author: String = "unknown"
    var difficulty: Difficulty = Difficulty.UNDEFINED

    constructor(
        id: String,
        title: String,
        image: Bitmap,
        colors: List<List<String>>,
        author: String,
        difficulty: Difficulty
    ) : this(id, title, image,  colors) {
        this.author = author
        this.difficulty = difficulty
    }

    fun getProportions() : Pair<Int, Int>? {
        if (colors.isEmpty() || colors[0].isEmpty()) {
            return null
        }

        return Pair(colors.size, colors[0].size)
    }
}

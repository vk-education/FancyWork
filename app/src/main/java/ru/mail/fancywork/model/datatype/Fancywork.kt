package ru.mail.fancywork.model.datatype

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Fancywork(
    val title: String,
    val image_url: String,
    val colors: List<List<String>>,
    var document_id: String = "",
    var author: String = "unknown",
    var difficulty: Difficulty = Difficulty.UNDEFINED
) :
    Parcelable {

    fun getProportions(): Pair<Int, Int>? {
        if (colors.isEmpty() || colors[0].isEmpty()) {
            return null
        }

        return Pair(colors.size, colors[0].size)
    }
}
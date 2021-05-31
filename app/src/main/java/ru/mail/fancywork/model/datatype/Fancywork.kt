package ru.mail.fancywork.model.datatype

import android.graphics.Bitmap
import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Fancywork(
    val title: String,
    val image_url: String,
    val width: Int,
    val height: Int,
    val colors: Int,
    var document_id: String = "",
    var author: String = "unknown",
    var difficulty: Difficulty = Difficulty.UNDEFINED
) : Parcelable {

    constructor() : this(
        "", "", 0, 0, 0,
        "", "", Difficulty.UNDEFINED
    )

    @Exclude
    var bitmap: Bitmap? = null

    suspend fun downloadImage() {
        if (bitmap != null) return
        // todo
    }
}

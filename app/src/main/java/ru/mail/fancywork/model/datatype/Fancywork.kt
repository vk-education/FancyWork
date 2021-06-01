package ru.mail.fancywork.model.datatype

import android.graphics.Bitmap
import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Fancywork(
    val title: String,
    val image_url: String,
    val image_path: String,
    val width: Int,
    val height: Int,
    val colors: Int,
    var document_id: String = "",
    var author: String = "unknown",
    var difficulty: Difficulty = Difficulty.UNDEFINED,
    @Exclude
    var bitmap: Bitmap? = null
) : Parcelable {

    constructor() : this(
        "", "", "",0, 0, 0,
        "", "", Difficulty.UNDEFINED, null
    )
}

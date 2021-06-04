package ru.mail.fancywork.model.datatype

import android.graphics.Bitmap
import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.android.parcel.Parcelize
fun countDifficulty(work:Fancywork):Difficulty {
    if (work.height >= 50 || work.width >= 50 || work.colors >= 7) {
        work.difficulty = Difficulty.EXTREMELY_HARD
        return Difficulty.EXTREMELY_HARD
    } else if (work.height >= 40 || work.width >= 40) {
        work.difficulty =  Difficulty.HARD
        return Difficulty.HARD
    } else if (work.height >= 25 || work.width >= 25 || work.colors >= 5) {
        work.difficulty =  Difficulty.MEDIUM
        return Difficulty.MEDIUM
    } else if (work.height >= 10 || work.width >= 10) {
        work.difficulty =  Difficulty.EASY
        return Difficulty.EASY
    }
    return Difficulty.UNDEFINED
}

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
        "", "", "", 0, 0, 0,
        "", "", Difficulty.UNDEFINED, null
    )
}

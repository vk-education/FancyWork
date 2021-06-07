package ru.mail.fancywork.model.datatype

import android.graphics.Bitmap
import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.android.parcel.Parcelize

private const val EXTREMELY_HARD_COLORS = 7
private const val MEDIUM_COLORS = 5
private const val EXTREMELY_HARD_SIZE = 50
private const val HARD_SIZE = 40
private const val MEDIUM_SIZE = 25
private const val EASY_SIZE = 10

fun countDifficulty(work: Fancywork): Difficulty {
    return if (
        work.height >= EXTREMELY_HARD_SIZE ||
        work.width >= EXTREMELY_HARD_SIZE ||
        work.colors >= EXTREMELY_HARD_COLORS
    ) {
        work.difficulty = Difficulty.EXTREMELY_HARD
        Difficulty.EXTREMELY_HARD
    } else if (work.height >= HARD_SIZE || work.width >= HARD_SIZE) {
        work.difficulty = Difficulty.HARD
        Difficulty.HARD
    } else if (
        work.height >= MEDIUM_SIZE ||
        work.width >= MEDIUM_SIZE ||
        work.colors >= MEDIUM_COLORS
    ) {
        work.difficulty = Difficulty.MEDIUM
        Difficulty.MEDIUM
    } else if (work.height >= EASY_SIZE || work.width >= EASY_SIZE) {
        work.difficulty = Difficulty.EASY
        Difficulty.EASY
    } else {
        Difficulty.UNDEFINED
    }
}

@Parcelize
data class Fancywork(
    val title: String,
    val imageUrl: String,
    val imagePath: String,
    val width: Int,
    val height: Int,
    val colors: Int,
    var documentId: String = "",
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

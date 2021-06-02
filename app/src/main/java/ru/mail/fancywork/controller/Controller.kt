package ru.mail.fancywork.controller

import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import ru.mail.fancywork.model.datatype.Fancywork
import ru.mail.fancywork.model.repo.AuthRepository
import ru.mail.fancywork.model.repo.CloudStorageRepository
import ru.mail.fancywork.model.repo.FirestoreRepository
import ru.mail.fancywork.model.repo.PixelizationRepository
import ru.mail.fancywork.model.utils.AuthException

class Controller(
    private val fs: FirestoreRepository = FirestoreRepository(),
    private val auth: AuthRepository = AuthRepository(),
    private val cloud: CloudStorageRepository = CloudStorageRepository(),
    private val pixel: PixelizationRepository = PixelizationRepository(),
) {
    companion object {
        const val AUTH_ERROR = "The user isn't authorized"
    }

    fun addUser() {
        auth.getUser()?.let { fs.addUser(it) } ?: throw AuthException(AUTH_ERROR)
    }

    fun getAuthIntent(): Intent {
        return auth.getAuthIntent()
    }

    fun logOut() {
        auth.logOut()
    }

    suspend fun downloadImage(path: String): Bitmap {
        return cloud.downloadImage(path)
    }

    suspend fun addFancywork(
        bitmap: Bitmap,
        colors: Int,
        title: String = "Undefined"
    ): Fancywork {
        val state = cloud.uploadImage(bitmap)
        val fancywork = Fancywork(
            title,
            state.image_url,
            state.image_path,
            bitmap.width,
            bitmap.height,
            colors,
        )
        fs.addFancywork(fancywork, auth.getUid())
        fancywork.bitmap = bitmap
        return fancywork
    }

    fun initThreadColors(resources: Resources): List<Pair<String, Triple<Int, Int, Int>>> {
        return PixelizationRepository.getThreadColors(resources)
    }

    fun pixelate(
        bitmap: Bitmap,
        width: Int,
        height: Int,
        colors: Int,
        threadColors: List<Pair<String, Triple<Int, Int, Int>>>
    ): Bitmap {
        // todo (needs better pixelRepo)
        return Bitmap.createScaledBitmap(bitmap, width, height, false)
    }

    suspend fun getFancyworks(): List<Fancywork>? {
        val fancyworks = fs.getFancyworks(auth.getUid())
        if (fancyworks != null) {
            return fancyworks
        }
        return fancyworks
    }

    fun isAuthorized(): Boolean = auth.isAuthorized()
}

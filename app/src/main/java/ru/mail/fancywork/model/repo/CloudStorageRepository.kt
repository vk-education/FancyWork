package ru.mail.fancywork.model.repo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.*

class CloudStorageRepository(private val cs: StorageReference = FirebaseStorage.getInstance().reference) {
    companion object {
        private const val maxImageSize: Long = (1 shl 20) * 10
    }

    var imageRef: StorageReference = cs.child("images")

    fun uploadImage(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos)
        val data = baos.toByteArray()

        val reference = imageRef.child("${UUID.randomUUID()}.png")
        reference.putBytes(data)
        return reference.path
    }

    suspend fun downloadImage(path: String): Bitmap {
        val data = cs.child(path).getBytes(maxImageSize).await()
        return BitmapFactory.decodeByteArray(data, 0, data.size)
    }
}

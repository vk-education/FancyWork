package ru.mail.fancywork.model.repo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.UUID
import kotlinx.coroutines.tasks.await
import ru.mail.fancywork.model.datatype.BitmapStorageState

class CloudStorageRepository(
    private val cs: StorageReference = FirebaseStorage.getInstance().reference
) {
    companion object {
        private const val maxImageSize: Long = (1 shl 20) * 10
    }

    var imageRef: StorageReference = cs.child("images")

    suspend fun uploadImage(bitmap: Bitmap): BitmapStorageState {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos)
        val data = baos.toByteArray()
        val reference = imageRef.child("${UUID.randomUUID()}.png")
        reference.putBytes(data).await()
        return BitmapStorageState(reference.downloadUrl.await().toString(), reference.path)
    }

    suspend fun downloadImage(path: String): Bitmap {
        val data = cs.child(path).getBytes(maxImageSize).await()
        return BitmapFactory.decodeByteArray(data, 0, data.size)
    }
}

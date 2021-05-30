package ru.mail.fancywork.model.repo

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import ru.mail.fancywork.model.utils.UploadException
import java.io.File

class CloudStorageRepository(private val cs: StorageReference = FirebaseStorage.getInstance().reference) {

    var imageRef: StorageReference = cs.child("images")

    fun uploadImage(path: String): String {
        // "path/to/images/rivers.jpg" example
        var file = Uri.fromFile(File(path))
        val reference = imageRef.child("${file.lastPathSegment}")

        // TODO: не уверен, что таск завершается и инициилизирует переменную downloadUri
        reference.putFile(file)
        return reference.path
    }
}
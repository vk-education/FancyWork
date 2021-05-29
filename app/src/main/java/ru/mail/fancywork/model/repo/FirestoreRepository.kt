package ru.mail.fancywork.model.repo

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirestoreRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    suspend fun checkUser(uid: String) = withContext(Dispatchers.IO) {
        val result = db.collection("users").document(uid).get().await()
        if (!result.exists()) {
            addUser(uid)
        }
    }

    fun addUser(uid: String) {
        db.collection("users")
            .document(uid)
            .set(
                hashMapOf<String, Any>(
                    "uid" to uid
                )
            )
    }

    companion object {
        private const val TAG = "FirebaseRepository"
    }
}
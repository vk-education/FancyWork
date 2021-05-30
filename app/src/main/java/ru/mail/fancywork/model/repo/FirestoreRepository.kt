package ru.mail.fancywork.model.repo

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    fun addUser(user: FirebaseUser) {
        db.collection("users")
            .document(user.uid)
            .set(
                hashMapOf<String, Any>(
                    "uid" to user.uid,
                    "name" to (user.displayName ?: "undefined"),
                    "email" to (user.email ?: "undefined")
                )
            )
    }

    companion object {
        private const val TAG = "FirebaseRepository"
    }
}
package ru.mail.fancywork.model.repo

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.getField
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import ru.mail.fancywork.model.datatype.Fancywork

class FirestoreRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    fun addFancywork(fancywork: Fancywork, owner: String) {
        val document = hashMapOf<String, Any>(
            "owner" to db.collection("users").document(owner),
            "fancywork_info" to fancywork
        )
        val reference = db.collection("fancyworks").document()
        fancywork.document_id = reference.id
        reference.set(document)
    }

    suspend fun getFancyworks(owner: String): List<Fancywork>? =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val userRef = db.collection("users").document(owner)
                val data = db.collection("fancyworks")
                    .whereEqualTo("owner", userRef).get().await()

                val result = ArrayList<Fancywork>()
                for (document in data.documents) {
                    if (document != null) {
                        val fancywork = document.getField<Fancywork>("fancywork_info")!!
                        fancywork.apply {
                            document_id = document.id
                        }
                        result.add(fancywork)
                    }
                }
                result
            } catch (e: Exception) {
                null
            }
        }

    fun addUser(user: FirebaseUser) {
        db.collection("users").document(user.uid)
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
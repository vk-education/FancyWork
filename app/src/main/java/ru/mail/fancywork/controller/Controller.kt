package ru.mail.fancywork.controller

import android.content.Intent
import ru.mail.fancywork.model.repo.AuthRepository
import ru.mail.fancywork.model.repo.FirestoreRepository
import ru.mail.fancywork.model.utils.AuthException

class Controller(
    private val fs: FirestoreRepository = FirestoreRepository(),
    private val auth: AuthRepository = AuthRepository()
) {
    companion object {
        const val AUTH_ERROR = "The user isn't authorized"
    }

    fun addUser() {
        auth.getUserID().let { fs.addUser(it) }
    }

    suspend fun checkUser() {
        auth.getUserID().let { fs.checkUser(it) }
    }

    fun getAuthIntent(): Intent {
        return auth.getAuthIntent()
    }

    fun logOut() {
        auth.logOut()
    }

    fun isAuthorized(): Boolean = auth.isAuthorized()
}

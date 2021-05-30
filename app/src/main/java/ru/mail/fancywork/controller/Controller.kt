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
        auth.getUser()?.let { fs.addUser(it) } ?: throw AuthException(AUTH_ERROR)
    }

    fun getAuthIntent(): Intent {
        return auth.getAuthIntent()
    }

    fun logOut() {
        auth.logOut()
    }

    fun isAuthorized(): Boolean = auth.isAuthorized()
}

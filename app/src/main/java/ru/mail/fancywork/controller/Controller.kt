package ru.mail.fancywork.controller

import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import ru.mail.fancywork.model.repo.AuthRepository

class Controller(
    private val auth: AuthRepository = AuthRepository()
) {
    companion object {
        const val AUTH_ERROR = "The user isn't authorized"
    }

//    fun addUser() {
//        auth.getUserID()?.let { fs.addUser(it) }
//            ?: throw AuthException(AUTH_ERROR)
//    }

    fun getAuthIntent(): Intent {
        return auth.getAuthIntent()
    }

    fun logOut() {
        auth.logOut();
    }

    fun isAuthorized(): Boolean = auth.isAuthorized()
}

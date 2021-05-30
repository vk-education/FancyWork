package ru.mail.fancywork.model.repo

import android.content.Intent
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import ru.mail.fancywork.model.utils.AuthException

class AuthRepository(private val auth: FirebaseAuth = FirebaseAuth.getInstance()) {
    fun getUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun getAuthIntent(): Intent {
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        return AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
    }

    fun isAuthorized(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }

    fun logOut() {
        FirebaseAuth.getInstance().signOut()
    }

    companion object {
        const val AUTH_ERROR = "The user isn't authorized"
        private const val TAG = "FirebaseRepository"
    }
}
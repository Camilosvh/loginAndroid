package com.example.smilecare.utils

/** Simula un backend. Se sustituiría por Firebase Auth o una API en una fase posterior. */
object AuthRepository {

    const val MAX_ATTEMPTS = 3
    const val DEMO_OTP = "123456"

    private data class User(val name: String, var password: String)

    private val users = mutableMapOf(
        "demo@smilecare.com" to User("Demo", "Demo1234")
    )

    /** null = invitado */
    var currentUserName: String? = null
        private set

    var failedAttempts = 0

    fun register(name: String, email: String, password: String): Boolean {
        val key = email.trim().lowercase()
        if (users.containsKey(key)) return false
        users[key] = User(name.trim(), password)
        currentUserName = name.trim()
        return true
    }

    fun login(email: String, password: String): Boolean {
        val user = users[email.trim().lowercase()]
        return if (user != null && user.password == password) {
            currentUserName = user.name
            failedAttempts = 0
            true
        } else false
    }

    fun updatePassword(email: String, newPassword: String) {
        users[email.trim().lowercase()]?.password = newPassword
    }

    fun logout() {
        currentUserName = null
    }
}
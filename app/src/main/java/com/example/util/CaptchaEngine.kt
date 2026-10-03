package com.example.util

data class CaptchaChallenge(
    val code: String,
    val timestamp: Long = System.currentTimeMillis()
)

object CaptchaEngine {
    // Alphanumeric characters excluding ambiguous characters (0, O, 1, l, I)
    private val chars = "23456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz"

    fun generateChallenge(): CaptchaChallenge {
        val code = (1..5).map { chars.random() }.joinToString("")
        return CaptchaChallenge(code = code)
    }

    fun verify(input: String, actualCode: String): Boolean {
        if (input.isBlank() || actualCode.isBlank()) return false
        return input.trim().equals(actualCode.trim(), ignoreCase = true)
    }
}

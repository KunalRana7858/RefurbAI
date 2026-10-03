package com.example.util

import java.security.MessageDigest

object SecurityUtils {
    private const val SALT = "PhoneKhoj_Salt_#9920!"

    fun hashPassword(password: String): String {
        val input = password + SALT
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun verifyPassword(password: String, storedHash: String): Boolean {
        return hashPassword(password) == storedHash
    }

    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    }

    fun formatCurrency(amount: Double): String {
        val longVal = amount.toLong()
        return "₹%,d".format(longVal)
    }

    fun formatImei(imei: String): String {
        val clean = imei.filter { it.isDigit() }
        return if (clean.length == 15) {
            "${clean.substring(0, 2)} ${clean.substring(2, 8)} ${clean.substring(8, 14)} ${clean.substring(14)}"
        } else {
            imei
        }
    }
}

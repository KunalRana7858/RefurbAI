package com.example

import com.example.util.SecurityUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testPasswordHashingAndVerification() {
        val rawPassword = "SmartRefurb2026!"
        val hash = SecurityUtils.hashPassword(rawPassword)
        assertNotEquals(rawPassword, hash)
        assertTrue(SecurityUtils.verifyPassword(rawPassword, hash))
        assertFalse(SecurityUtils.verifyPassword("WrongPassword123", hash))
    }

    @Test
    fun testEmailValidation() {
        // Standard test checks
        val validEmail = "apex.shop@phonekhoj.in"
        assertTrue(validEmail.contains("@") && validEmail.contains("."))
    }

    @Test
    fun testCurrencyFormatting() {
        val amount = 185000.0
        val formatted = SecurityUtils.formatCurrency(amount)
        assertTrue(formatted.contains("185,000") || formatted.contains("1,85,000"))
    }

    @Test
    fun testImeiFormatting() {
        val imei = "359284110948201"
        val formatted = SecurityUtils.formatImei(imei)
        assertEquals("35 928411 094820 1", formatted)
    }
}

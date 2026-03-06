package com.paisapulse.app

import com.paisapulse.app.utils.SMSUtils
import org.junit.Assert.assertEquals
import org.junit.Test

class SMSUtilsTest {

    @Test
    fun testGlobalBankFormats() {
        val testCases = listOf(
            "HDFC: Rs 500.00 spent at ZOMATO." to 500.0,
            "Chase: You made a $45.99 purchase." to 45.99,
            "Santander: Compra de 25,50 €." to 25.5,
            "SBI: INR 1,250.50 debited." to 1250.5
        )

        testCases.forEach { (sms, expected) ->
            val actual = SMSUtils.parseAmount(sms)
            assertEquals("Failed for: $sms", expected, actual, 0.01)
        }
    }

    @Test
    fun testCategoryMapping() {
        // Matches your MainActivity Chips: Food, Travel, Bills, Shopping
        assertEquals("Food", SMSUtils.getCategory("ZOMATO INDIA"))
        assertEquals("Travel", SMSUtils.getCategory("UBER TRIP"))
        assertEquals("Bills", SMSUtils.getCategory("JIO RECHARGE"))
        assertEquals("Shopping", SMSUtils.getCategory("AMAZON"))
    }
}
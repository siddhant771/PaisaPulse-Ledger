package com.paisapulse.app

import com.paisapulse.app.data.utils.SmsParser
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SmsParserTest {
    @Test
    fun recognizesDebitTransactionSms() {
        assertTrue(SmsParser.isLikelyTransaction("HDFC: Rs 500.00 debited for UPI payment to Zomato."))
    }

    @Test
    fun ignoresNonFinancialSms() {
        assertFalse(SmsParser.isLikelyTransaction("Your OTP is 123456. Do not share it."))
    }
}

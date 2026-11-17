package com.delacruz.payrollcalculator

import org.junit.Assert.assertEquals
import org.junit.Test

class PayrollCalculatorTest {

    private val delta = 0.0001

    @Test
    fun under40Hours_noOvertime() {
        val result = PayrollCalculator.calculate(hours = 30.0, hourlyRate = 20.0, taxRate = 0.10)

        assertEquals(600.0, result.pay, delta)
        assertEquals(0.0, result.overtimePay, delta)
        assertEquals(600.0, result.totalPay, delta)
        assertEquals(60.0, result.tax, delta)
    }

    @Test
    fun exactly40Hours_noOvertime() {
        val result = PayrollCalculator.calculate(hours = 40.0, hourlyRate = 15.0, taxRate = 0.13)

        assertEquals(600.0, result.pay, delta)
        assertEquals(0.0, result.overtimePay, delta)
        assertEquals(600.0, result.totalPay, delta)
        assertEquals(78.0, result.tax, delta)
    }

    @Test
    fun over40Hours_overtimeAtTimeAndAHalf() {
        val result = PayrollCalculator.calculate(hours = 45.0, hourlyRate = 20.0, taxRate = 0.10)

        assertEquals(800.0, result.pay, delta)
        assertEquals(150.0, result.overtimePay, delta)
        assertEquals(950.0, result.totalPay, delta)
    }

    @Test
    fun taxIsCalculatedOnPay_notOnTotalPay() {
        val result = PayrollCalculator.calculate(hours = 45.0, hourlyRate = 20.0, taxRate = 0.10)

        assertEquals(80.0, result.tax, delta)
    }

    @Test
    fun fractionalOvertimeHours() {
        val result = PayrollCalculator.calculate(hours = 40.5, hourlyRate = 10.0, taxRate = 0.20)

        assertEquals(400.0, result.pay, delta)
        assertEquals(7.5, result.overtimePay, delta)
        assertEquals(407.5, result.totalPay, delta)
        assertEquals(80.0, result.tax, delta)
    }

    @Test
    fun zeroHours_everythingIsZero() {
        val result = PayrollCalculator.calculate(hours = 0.0, hourlyRate = 25.0, taxRate = 0.15)

        assertEquals(0.0, result.pay, delta)
        assertEquals(0.0, result.overtimePay, delta)
        assertEquals(0.0, result.totalPay, delta)
        assertEquals(0.0, result.tax, delta)
    }
}

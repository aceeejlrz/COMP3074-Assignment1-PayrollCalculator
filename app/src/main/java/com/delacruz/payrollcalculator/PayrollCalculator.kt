package com.delacruz.payrollcalculator

data class PayResult(
    val pay: Double,
    val overtimePay: Double,
    val totalPay: Double,
    val tax: Double
)

object PayrollCalculator {

    const val REGULAR_HOURS = 40.0
    const val OVERTIME_MULTIPLIER = 1.5

    fun calculate(hours: Double, hourlyRate: Double, taxRate: Double): PayResult {
        val pay: Double
        val overtimePay: Double

        if (hours <= REGULAR_HOURS) {
            pay = hours * hourlyRate
            overtimePay = 0.0
        } else {
            pay = REGULAR_HOURS * hourlyRate
            overtimePay = (hours - REGULAR_HOURS) * hourlyRate * OVERTIME_MULTIPLIER
        }

        val totalPay = pay + overtimePay

        // Per the assignment, tax applies to pay, not total_pay.
        val tax = pay * taxRate

        return PayResult(pay, overtimePay, totalPay, tax)
    }
}

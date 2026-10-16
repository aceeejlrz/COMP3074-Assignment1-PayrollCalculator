package com.delacruz.payrollcalculator

/**
 * Holds the four values that are shown to the user after a calculation.
 */
data class PayResult(
    val pay: Double,
    val overtimePay: Double,
    val totalPay: Double,
    val tax: Double
)

/**
 * Pay and tax formulas from the assignment.
 *
 * Kept separate from the UI so the math can be unit tested on its own
 * (see PayrollCalculatorTest).
 */
object PayrollCalculator {

    /** Hours paid at the normal rate before overtime starts. */
    const val REGULAR_HOURS = 40.0

    /** Overtime hours are paid at "time and a half". */
    const val OVERTIME_MULTIPLIER = 1.5

    /**
     * @param hours number of hours worked
     * @param hourlyRate pay per hour
     * @param taxRate tax rate as a decimal (0.13 means 13%)
     */
    fun calculate(hours: Double, hourlyRate: Double, taxRate: Double): PayResult {
        val pay: Double
        val overtimePay: Double

        if (hours <= REGULAR_HOURS) {
            // pay = no_of_hours * hourly_rate, overtime_pay = 0
            pay = hours * hourlyRate
            overtimePay = 0.0
        } else {
            // pay = 40 * hourly_rate
            // overtime_pay = (no_of_hours - 40) * hourly_rate * 1.5
            pay = REGULAR_HOURS * hourlyRate
            overtimePay = (hours - REGULAR_HOURS) * hourlyRate * OVERTIME_MULTIPLIER
        }

        // total_pay = pay + overtime_pay
        val totalPay = pay + overtimePay

        // tax = pay * tax_rate (the assignment applies tax to pay, not to total_pay)
        val tax = pay * taxRate

        return PayResult(pay, overtimePay, totalPay, tax)
    }
}

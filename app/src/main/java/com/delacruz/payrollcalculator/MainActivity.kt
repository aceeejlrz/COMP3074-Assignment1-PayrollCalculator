package com.delacruz.payrollcalculator

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.delacruz.payrollcalculator.ui.theme.PayrollCalculatorTheme
import java.text.NumberFormat
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PayrollCalculatorTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PayrollScreen()
                }
            }
        }
    }

    // Adds the "About" item to the ActionBar (res/menu/main_menu.xml).
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    // Opens AboutActivity when the "About" item is tapped.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_about) {
            startActivity(Intent(this, AboutActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}

// Lets the result survive screen rotation (rememberSaveable can only store simple types).
private val PayResultSaver = listSaver<PayResult?, Double>(
    save = { result ->
        if (result == null) emptyList()
        else listOf(result.pay, result.overtimePay, result.totalPay, result.tax)
    },
    restore = { values ->
        if (values.isEmpty()) null
        else PayResult(values[0], values[1], values[2], values[3])
    }
)

@Composable
fun PayrollScreen() {
    // ----- Input state (what the user typed) -----
    var hoursText by rememberSaveable { mutableStateOf("") }
    var rateText by rememberSaveable { mutableStateOf("") }
    var taxRateText by rememberSaveable { mutableStateOf("") }

    // ----- Error messages (null = no error) -----
    var hoursError by rememberSaveable { mutableStateOf<String?>(null) }
    var rateError by rememberSaveable { mutableStateOf<String?>(null) }
    var taxRateError by rememberSaveable { mutableStateOf<String?>(null) }

    // ----- Output state (null = nothing calculated yet) -----
    var result by rememberSaveable(stateSaver = PayResultSaver) { mutableStateOf(null) }

    val focusManager = LocalFocusManager.current

    val errorRequired = stringResource(R.string.error_required)
    val errorNegative = stringResource(R.string.error_negative)
    val errorTaxRange = stringResource(R.string.error_tax_range)

    fun calculate() {
        // 1. Read the input. toDoubleOrNull() returns null instead of crashing on bad text.
        val hours = hoursText.trim().toDoubleOrNull()
        val rate = rateText.trim().toDoubleOrNull()
        val taxRate = taxRateText.trim().toDoubleOrNull()

        // 2. Validate each field and remember the error to show under it.
        hoursError = when {
            hours == null || !hours.isFinite() -> errorRequired
            hours < 0 -> errorNegative
            else -> null
        }
        rateError = when {
            rate == null || !rate.isFinite() -> errorRequired
            rate < 0 -> errorNegative
            else -> null
        }
        taxRateError = when {
            taxRate == null || !taxRate.isFinite() -> errorRequired
            taxRate < 0 || taxRate > 1 -> errorTaxRange
            else -> null
        }

        // 3. Only calculate when all three inputs are valid.
        if (hours != null && rate != null && taxRate != null &&
            hoursError == null && rateError == null && taxRateError == null
        ) {
            result = PayrollCalculator.calculate(hours, rate, taxRate)
            focusManager.clearFocus() // hides the keyboard so the results are visible
        } else {
            result = null
        }
    }

    fun clear() {
        hoursText = ""
        rateText = ""
        taxRateText = ""
        hoursError = null
        rateError = null
        taxRateError = null
        result = null
        focusManager.clearFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            // keeps content away from the navigation bar and the keyboard
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.main_heading),
            style = MaterialTheme.typography.headlineSmall
        )

        NumberField(
            value = hoursText,
            onValueChange = { hoursText = it },
            label = stringResource(R.string.label_hours),
            error = hoursError,
            imeAction = ImeAction.Next
        )
        NumberField(
            value = rateText,
            onValueChange = { rateText = it },
            label = stringResource(R.string.label_rate),
            error = rateError,
            imeAction = ImeAction.Next
        )
        NumberField(
            value = taxRateText,
            onValueChange = { taxRateText = it },
            label = stringResource(R.string.label_tax_rate),
            error = taxRateError,
            hint = stringResource(R.string.hint_tax_rate),
            imeAction = ImeAction.Done,
            onDone = { calculate() }
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { calculate() }, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.button_calculate))
            }
            OutlinedButton(onClick = { clear() }, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.button_clear))
            }
        }

        result?.let { ResultCard(it) }
    }
}

@Composable
private fun NumberField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    imeAction: ImeAction,
    hint: String? = null,
    onDone: () -> Unit = {}
) {
    // shows the error if there is one, otherwise the optional hint
    val message = error ?: hint

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        isError = error != null,
        supportingText = if (message != null) {
            { Text(message) }
        } else {
            null
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal, // number keyboard with a decimal point
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun ResultCard(result: PayResult) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.results_heading),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            ResultRow(stringResource(R.string.label_pay), result.pay)
            ResultRow(stringResource(R.string.label_overtime_pay), result.overtimePay)
            ResultRow(stringResource(R.string.label_total_pay), result.totalPay, bold = true)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            ResultRow(stringResource(R.string.label_tax), result.tax)
        }
    }
}

@Composable
private fun ResultRow(label: String, amount: Double, bold: Boolean = false) {
    val weight = if (bold) FontWeight.Bold else FontWeight.Normal
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = weight)
        Text(text = formatMoney(amount), fontWeight = weight)
    }
}

// Formats a number as Canadian dollars with 2 decimals, e.g. 1234.5 -> "$1,234.50".
fun formatMoney(amount: Double): String =
    NumberFormat.getCurrencyInstance(Locale.CANADA).format(amount)

@Preview(showBackground = true)
@Composable
fun PayrollScreenPreview() {
    PayrollCalculatorTheme {
        PayrollScreen()
    }
}

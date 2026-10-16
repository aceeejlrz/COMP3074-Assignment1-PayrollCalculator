package com.delacruz.payrollcalculator

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.delacruz.payrollcalculator.ui.theme.PayrollCalculatorTheme

class AboutActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Shows the back (Up) arrow in the ActionBar.
        actionBar?.setDisplayHomeAsUpEnabled(true)

        setContent {
            PayrollCalculatorTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AboutScreen()
                }
            }
        }
    }

    // Called when the back arrow in the ActionBar is tapped.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            // finish() closes this screen and returns to the MainActivity that is
            // already on the back stack, so no new MainActivity is created.
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}

@Composable
fun AboutScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.about_name_label),
            style = MaterialTheme.typography.labelLarge
        )
        Text(
            text = stringResource(R.string.student_full_name),
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = stringResource(R.string.about_id_label),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = stringResource(R.string.student_id),
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = stringResource(R.string.about_course),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 32.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AboutScreenPreview() {
    PayrollCalculatorTheme {
        AboutScreen()
    }
}

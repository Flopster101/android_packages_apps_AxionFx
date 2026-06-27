/*
 * Copyright 2025-2026 AxionOS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.axion.axionfx.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.android.axion.compose.preferences.LocalPreferencePosition
import com.android.axion.compose.preferences.PreferencePosition
import com.android.axion.compose.preferences.SliderPreference
import kotlin.math.roundToInt

@Composable
fun EffectSlider(
    title: String,
    summary: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    unit: String = "",
    position: PreferencePosition = LocalPreferencePosition.current,
    onReset: (() -> Unit)? = null,
) {
    var showDialog by remember { mutableStateOf(false) }

    val displayValue = if (unit.isNotEmpty()) {
        "${value.toInt()} $unit"
    } else {
        value.toInt().toString()
    }

    if (showDialog) {
        var textValue by remember { mutableStateOf(value.roundToInt().toString()) }
        val parsedValue = textValue.toFloatOrNull()
        val isValid = parsedValue != null && parsedValue in valueRange

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(title) },
            text = {
                Column {
                    Text(
                        text = summary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = textValue,
                        onValueChange = { textValue = it },
                        label = { Text("Value (${valueRange.start.toInt()} .. ${valueRange.endInclusive.toInt()})") },
                        isError = !isValid,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (parsedValue != null) {
                            onValueChange(parsedValue.coerceIn(valueRange.start, valueRange.endInclusive))
                        }
                        showDialog = false
                    },
                    enabled = isValid
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    SliderPreference(
        title = title,
        summary = summary,
        value = value,
        onValueChange = onValueChange,
        onValueChangeFinished = {},
        valueRange = valueRange,
        displayValue = displayValue,
        modifier = modifier.clickable(enabled = enabled) {
            showDialog = true
        },
        enabled = enabled,
        position = position,
        onReset = onReset,
    )
}

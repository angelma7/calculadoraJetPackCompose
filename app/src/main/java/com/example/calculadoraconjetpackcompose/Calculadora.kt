package com.example.calculadoraconjetpackcompose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

class CalculatorViewModel : ViewModel() {
    var displayValue by mutableStateOf("0")
        private set

    private var firstOperand: Double? = null
    private var pendingOperation: String? = null
    private var clearOnNextInput = false

    fun onNumberClicked(number: String) {
        if (displayValue == "0" || clearOnNextInput) {
            displayValue = number
            clearOnNextInput = false
        } else {
            displayValue += number
        }
    }

    fun onOperationClicked(operation: String) {
        firstOperand = displayValue.toDoubleOrNull()
        pendingOperation = operation
        clearOnNextInput = true
    }

    fun onEqualsClicked() {
        val secondOperand = displayValue.toDoubleOrNull()
        if (firstOperand != null && pendingOperation != null && secondOperand != null) {
            val result = when (pendingOperation) {
                "+" -> firstOperand!! + secondOperand
                "-" -> firstOperand!! - secondOperand
                "*" -> firstOperand!! * secondOperand
                "/" -> {
                    if (secondOperand == 0.0) {
                        "Error: División por cero"
                    } else {
                        firstOperand!! / secondOperand
                    }
                }
                else -> secondOperand
            }

            displayValue = if (result is Double && result % 1.0 == 0.0) {
                result.toInt().toString()
            } else {
                result.toString()
            }

            firstOperand = null
            pendingOperation = null
            clearOnNextInput = true
        }
    }

    fun onClearClicked() {
        displayValue = "0"
        firstOperand = null
        pendingOperation = null
        clearOnNextInput = false
    }

    fun onDeleteClicked() {
        if (displayValue.length > 1 && !displayValue.startsWith("Error")) {
            displayValue = displayValue.dropLast(1)
        } else {
            displayValue = "0"
        }
    }
}

@Composable
fun CalculatorScreen(
    modifier: Modifier = Modifier,
    viewModel: CalculatorViewModel = viewModel()
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(
            text = viewModel.displayValue,
            style = TextStyle(fontSize = 48.sp, textAlign = TextAlign.End),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        )

        val buttons = listOf(
            listOf("C", "⌫", "/", "*"),
            listOf("7", "8", "9", "-"),
            listOf("4", "5", "6", "+"),
            listOf("1", "2", "3", "="),
            listOf("0", ".")
        )

        buttons.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { label ->
                    CalculatorButton(
                        text = label,
                        modifier = Modifier.weight(1f)
                    ) {
                        when (label) {
                            "C" -> viewModel.onClearClicked()
                            "⌫" -> viewModel.onDeleteClicked()
                            "=" -> viewModel.onEqualsClicked()
                            "+", "-", "*", "/" -> viewModel.onOperationClicked(label)
                            else -> viewModel.onNumberClicked(label)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(72.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text = text, fontSize = 24.sp)
    }
}
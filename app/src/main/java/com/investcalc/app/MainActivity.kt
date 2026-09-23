package com.investcalc.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.DecimalFormat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InvestCalcTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppRoot()
                }
            }
        }
    }
}

// ---------- Theme ----------

private val NavyBg = androidx.compose.ui.graphics.Color(0xFF0F2E2B)
private val CardBg = androidx.compose.ui.graphics.Color(0xFF16403C)
private val Gold = androidx.compose.ui.graphics.Color(0xFFC9A227)
private val Cream = androidx.compose.ui.graphics.Color(0xFFF6F5F1)
private val Coral = androidx.compose.ui.graphics.Color(0xFFC1584B)
private val Green = androidx.compose.ui.graphics.Color(0xFF5FA66B)

@Composable
fun InvestCalcTheme(content: @Composable () -> Unit) {
    val colors = darkColorScheme(
        primary = Gold,
        background = NavyBg,
        surface = CardBg,
        onPrimary = NavyBg,
        onBackground = Cream,
        onSurface = Cream
    )
    MaterialTheme(colorScheme = colors, content = content)
}

// ---------- Root with two tabs ----------

@Composable
fun AppRoot() {
    var tab by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Text(
                "InvestCalc",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Cream
            )
            Text(
                "Returns and profit/loss, worked out fast",
                fontSize = 14.sp,
                color = Cream.copy(alpha = 0.7f)
            )
        }

        Text(
            "Made by Arun Kushwaha :)",
            fontSize = 12.sp,
            color = Gold.copy(alpha = 0.7f),
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 8.dp)
        )

        TabRow(
            selectedTabIndex = tab,
            containerColor = NavyBg,
            contentColor = Gold
        ) {
            Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Return on %") })
            Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Profit / Loss") })
        }

        Box(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            if (tab == 0) ReturnCalculator() else ProfitLossCalculator()
        }
    }
}

// ---------- Calculator 1: Capital + % -> Return + Total value ----------

@Composable
fun ReturnCalculator() {
    var capital by remember { mutableStateOf("") }
    var percent by remember { mutableStateOf("") }

    val cap = capital.toDoubleOrNull()
    val pct = percent.toDoubleOrNull()
    val returnAmount = if (cap != null && pct != null) cap * pct / 100.0 else null
    val totalValue = if (cap != null && returnAmount != null) cap + returnAmount else null

    Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        LabeledField("Capital invested", capital, { capital = it })
        LabeledField("Expected return (%)", percent, { percent = it }, allowNegative = true)

        ResultCard(
            rows = listOf(
                "Capital invested" to formatMoney(cap),
                "Return (%)" to (pct?.let { "${trim(it)}%" } ?: "—"),
                "Return amount" to formatMoney(returnAmount),
                "Total value" to formatMoney(totalValue)
            ),
            highlightIndex = 3
        )
    }
}

// ---------- Calculator 2: Invested + Current -> Profit/Loss amount + % ----------

@Composable
fun ProfitLossCalculator() {
    var invested by remember { mutableStateOf("") }
    var current by remember { mutableStateOf("") }

    val inv = invested.toDoubleOrNull()
    val cur = current.toDoubleOrNull()
    val diff = if (inv != null && cur != null) cur - inv else null
    val pct = if (inv != null && inv != 0.0 && diff != null) diff / inv * 100.0 else null

    val isProfit = (diff ?: 0.0) >= 0.0
    val label = if (isProfit) "Profit" else "Loss"

    Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        LabeledField("Invested value", invested, { invested = it })
        LabeledField("Current value", current, { current = it })

        ResultCard(
            rows = listOf(
                "Invested value" to formatMoney(inv),
                "Current value" to formatMoney(cur),
                "$label amount" to formatMoney(diff?.let { if (it < 0) -it else it }),
                "$label (%)" to (pct?.let { "${trim(if (it < 0) -it else it)}%" } ?: "—")
            ),
            highlightIndex = 3,
            highlightColor = if (diff == null) Gold else if (isProfit) Green else Coral
        )
    }
}

// ---------- Shared UI pieces ----------

@Composable
fun LabeledField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    allowNegative: Boolean = false
) {
    Column {
        Text(label, fontSize = 13.sp, color = Cream.copy(alpha = 0.7f))
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = { new ->
                val filtered = if (allowNegative) {
                    new.filter { it.isDigit() || it == '.' || it == '-' }
                } else {
                    new.filter { it.isDigit() || it == '.' }
                }
                onChange(filtered)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Gold,
                unfocusedBorderColor = Cream.copy(alpha = 0.3f),
                focusedTextColor = Cream,
                unfocusedTextColor = Cream
            )
        )
    }
}

@Composable
fun ResultCard(
    rows: List<Pair<String, String>>,
    highlightIndex: Int,
    highlightColor: androidx.compose.ui.graphics.Color = Gold
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            rows.forEachIndexed { index, (label, value) ->
                val isHighlight = index == highlightIndex
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        label,
                        fontSize = if (isHighlight) 15.sp else 14.sp,
                        color = Cream.copy(alpha = if (isHighlight) 1f else 0.75f),
                        fontWeight = if (isHighlight) FontWeight.SemiBold else FontWeight.Normal
                    )
                    Text(
                        value,
                        fontSize = if (isHighlight) 20.sp else 15.sp,
                        color = if (isHighlight) highlightColor else Cream,
                        fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal
                    )
                }
                if (index != rows.lastIndex) {
                    Divider(color = Cream.copy(alpha = 0.1f))
                }
            }
        }
    }
}

// ---------- Formatting helpers ----------

private val moneyFormat = DecimalFormat("#,##0.00")

fun formatMoney(value: Double?): String {
    if (value == null) return "—"
    return moneyFormat.format(value)
}

fun trim(value: Double): String {
    return if (value == value.toLong().toDouble()) {
        value.toLong().toString()
    } else {
        DecimalFormat("#,##0.##").format(value)
    }
}

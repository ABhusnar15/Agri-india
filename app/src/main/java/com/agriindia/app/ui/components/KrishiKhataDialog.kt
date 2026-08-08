package com.agriindia.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agriindia.app.model.AppLanguage
import com.agriindia.app.model.FarmExpenseEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KrishiKhataDialog(
    language: AppLanguage,
    onDismiss: () -> Unit
) {
    val isHi = language == AppLanguage.HINDI
    val isMr = language == AppLanguage.MARATHI

    var selectedSeason by remember { mutableStateOf("Rabi 2025-26") }
    var newCategory by remember { mutableStateOf("Seeds") }
    var newAmount by remember { mutableStateOf("") }
    var newNotes by remember { mutableStateOf("") }
    var newType by remember { mutableStateOf("Expense") }

    var entries by remember {
        mutableStateOf(
            listOf(
                FarmExpenseEntry("1", "Rabi 2025-26", "Wheat (गहू / गेहूं)", "Expense", "Seeds (बियाणे)", 4200.0, "15 Nov 2025", "Certified HD-3226 2 Quintal"),
                FarmExpenseEntry("2", "Rabi 2025-26", "Wheat (गहू / गेहूं)", "Expense", "Fertilizer (खते)", 3850.0, "20 Nov 2025", "DAP + Urea 3 Bags"),
                FarmExpenseEntry("3", "Rabi 2025-26", "Wheat (गहू / गेहूं)", "Expense", "Tractor / नांगरणी", 5000.0, "12 Nov 2025", "Rotavator plowing 4 acres"),
                FarmExpenseEntry("4", "Rabi 2025-26", "Wheat (गहू / गेहूं)", "Revenue", "Harvest Sale (उत्पन्न विक्री)", 54000.0, "28 Mar 2026", "24 Quintal sold @ ₹2275 MSP")
            )
        )
    }

    val totalExpenses = entries.filter { it.entryType == "Expense" }.sumOf { it.amount }
    val totalRevenue = entries.filter { it.entryType == "Revenue" }.sumOf { it.amount }
    val netProfit = totalRevenue - totalExpenses

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFDCFCE7),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = Color(0xFF059669),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = when (language) {
                                AppLanguage.MARATHI -> "डिजिटल शेतकरी खातेवही"
                                AppLanguage.HINDI -> "डिजिटल कृषि बही-खाता"
                                else -> "Smart Krishi Khata Ledger"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = when (language) {
                                AppLanguage.MARATHI -> "पिकाचा खर्च, नफा व आय-व्यय हिशोब"
                                AppLanguage.HINDI -> "फसल लागत, मुनाफा व आय-व्यय हिसाब"
                                else -> "Farm Expense, Harvest Income & Profit"
                            },
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Summary profit card
                Card(
                    colors = CardDefaults.cardColors(containerColor = if (netProfit >= 0) Color(0xFFF0FDF4) else Color(0xFFFEF2F2)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (netProfit >= 0) Color(0xFF86EFAC) else Color(0xFFFECACA)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(if (isHi) "कुल लागत (खर्च):" else "Total Expenses:", fontSize = 11.sp, color = Color(0xFFDC2626))
                                Text("₹${totalExpenses.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                            }
                            Column {
                                Text(if (isHi) "कुल फसल आय (राजस्व):" else "Gross Revenue:", fontSize = 11.sp, color = Color(0xFF16A34A))
                                Text("₹${totalRevenue.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(if (isHi) "शुद्ध लाभ (मुनाफा):" else "Net Profit:", fontSize = 11.sp, color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                                Text("₹${netProfit.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = if (netProfit >= 0) Color(0xFF15803D) else Color(0xFFB91C1C))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Add entry form
                Text(
                    text = if (isHi) "नया लेन-देन दर्ज करें:" else "Record New Entry:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155)
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = newType == "Expense",
                        onClick = { newType = "Expense" },
                        label = { Text(if (isHi) "खर्च (Expense)" else "Expense", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = newType == "Revenue",
                        onClick = { newType = "Revenue" },
                        label = { Text(if (isHi) "आय (Revenue)" else "Income/Revenue", fontSize = 11.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newAmount,
                        onValueChange = { newAmount = it },
                        placeholder = { Text(if (isHi) "राशि (₹)" else "Amount (₹)", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = newNotes,
                        onValueChange = { newNotes = it },
                        placeholder = { Text(if (isHi) "विवरण (जैसे: बीज/खाद)" else "Notes (Seeds, Labor)", fontSize = 11.sp) },
                        modifier = Modifier.weight(1.5f),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val amt = newAmount.toDoubleOrNull() ?: 0.0
                        if (amt > 0) {
                            entries = entries + FarmExpenseEntry(
                                id = (entries.size + 1).toString(),
                                season = selectedSeason,
                                crop = "Wheat",
                                entryType = newType,
                                category = newNotes.ifBlank { if (newType == "Expense") "Farm Input" else "Harvest Sale" },
                                amount = amt,
                                date = "Today",
                                notes = newNotes
                            )
                            newAmount = ""
                            newNotes = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isHi) "खाता में जोड़ें" else "Add Entry to Ledger")
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Entry List
                Text(
                    text = if (isHi) "हाल के लेन-देन:" else "Recent Transactions:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155)
                )
                Spacer(modifier = Modifier.height(6.dp))

                entries.takeLast(5).reversed().forEach { entry ->
                    Surface(
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = entry.category, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF1E293B))
                                Text(text = "${entry.season} • ${entry.date}", fontSize = 10.sp, color = Color(0xFF64748B))
                            }
                            Text(
                                text = "${if (entry.entryType == "Expense") "-" else "+"}₹${entry.amount.toInt()}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (entry.entryType == "Expense") Color(0xFFDC2626) else Color(0xFF16A34A)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {}
    )
}

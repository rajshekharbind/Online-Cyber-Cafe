package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    jobTitle: String,
    officialFee: String,
    serviceFee: String,
    totalAmount: String,
    onPaymentSuccess: (String, String, String, String, String) -> Unit,
    onBack: () -> Unit
) {
    var isProcessing by remember { mutableStateOf(false) }
    var selectedMethod by remember { mutableStateOf("UPI") }
    val scope = rememberCoroutineScope()

    // Unique Order and Payment IDs
    val orderId = remember { "ORD-${(100000..999999).random()}" }
    val paymentId = remember { "PAY-${(100000..999999).random()}" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Secure Checkout") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Payment Summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Payment Summary", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(12.dp))
                    PaymentDetailItem("Job", jobTitle)
                    PaymentDetailItem("Order ID", orderId)
                    PaymentDetailItem("Official Fee", officialFee)
                    PaymentDetailItem("Service Fee", serviceFee)
                    HorizontalDivider(Modifier.padding(vertical = 12.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Payable", fontWeight = FontWeight.Bold)
                        Text(totalAmount, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50), fontSize = 20.sp)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
            Text("Select Payment Method", fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
            Spacer(Modifier.height(16.dp))

            PaymentMethodItem("UPI (GPay, PhonePe, Paytm)", selectedMethod == "UPI") { selectedMethod = "UPI" }
            PaymentMethodItem("Debit / Credit Card", selectedMethod == "CARD") { selectedMethod = "CARD" }
            PaymentMethodItem("Net Banking", selectedMethod == "NET") { selectedMethod = "NET" }

            Spacer(Modifier.height(40.dp))

            if (isProcessing) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
                Spacer(Modifier.height(16.dp))
                Text("Securing your payment...", color = Color.Gray)
            } else {
                Button(
                    onClick = {
                        isProcessing = true
                        scope.launch {
                            delay(2500) // Simulate gateway latency
                            onPaymentSuccess(orderId, paymentId, officialFee, serviceFee, totalAmount)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Lock, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Pay $totalAmount Safely", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            
            Spacer(Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Shield, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("SSL Encrypted & Secure", fontSize = 12.sp, color = Color.Gray)
            }
            
            Spacer(Modifier.height(24.dp))
            
            // Trust Features (Req 50)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Trust & Transparency", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Text("Terms", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, modifier = Modifier.clickable { })
                        Text("Privacy Policy", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, modifier = Modifier.clickable { })
                        Text("Refund Policy", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, modifier = Modifier.clickable { })
                    }
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.SupportAgent, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Human Support: +91-800-123-4567", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentMethodItem(label: String, selected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onClick() },
        border = if (selected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        colors = CardDefaults.cardColors(containerColor = if(selected) Color(0xFFE3F2FD) else Color.White)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = selected, onClick = onClick)
            Spacer(Modifier.width(12.dp))
            Text(label, fontWeight = if(selected) FontWeight.Bold else FontWeight.Normal)
        }
    }
}

@Composable
fun PaymentDetailItem(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray, fontSize = 13.sp)
        Text(value, fontWeight = FontWeight.Medium, fontSize = 13.sp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentHistoryScreen(onBack: () -> Unit) {
    val transactions = TransactionStore.getTransactions()
    var selectedTransactionForReceipt by remember { mutableStateOf<TransactionRecord?>(null) }

    if (selectedTransactionForReceipt != null) {
        ReceiptDialog(
            transaction = selectedTransactionForReceipt!!,
            onDismiss = { selectedTransactionForReceipt = null }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment History") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(transactions) { tx ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { selectedTransactionForReceipt = tx },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(tx.applicationId, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Surface(
                                color = tx.status.color.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(4.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, tx.status.color)
                            ) {
                                Text(text = tx.status.label, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = tx.status.color)
                            }
                        }
                        Text(tx.timestamp, color = Color.Gray, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        HorizontalDivider(color = Color(0xFFEEEEEE))
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Transaction ID: ${tx.paymentId}", fontSize = 12.sp, color = Color.Gray)
                            Text(tx.totalAmount, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF4CAF50))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptDialog(transaction: TransactionRecord, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = { /* Simulate Download */ }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Download, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("Download Receipt")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) { Text("Close") }
        },
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Verified, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(48.dp))
                Spacer(Modifier.height(8.dp))
                Text("Payment Receipt", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("CyberCafe Online Services", color = Color.Gray, fontSize = 14.sp)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                PaymentDetailItem("Date", transaction.timestamp)
                PaymentDetailItem("Transaction ID", transaction.paymentId)
                PaymentDetailItem("Application ID", transaction.applicationId)
                PaymentDetailItem("Status", transaction.status.label)
                
                Spacer(Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(Modifier.height(12.dp))

                PaymentDetailItem("Official Fee", transaction.officialFee)
                PaymentDetailItem("Service Fee", transaction.serviceFee)
                
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Paid", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(transaction.totalAmount, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF4CAF50))
                }
            }
        }
    )
}

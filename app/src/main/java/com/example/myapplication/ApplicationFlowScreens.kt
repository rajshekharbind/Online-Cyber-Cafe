package com.example.myapplication

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationSummaryScreen(
    jobTitle: String,
    officialFee: String,
    serviceFee: String,
    totalAmount: String,
    onConfirm: () -> Unit,
    onBack: () -> Unit
) {
    var profileConfirmed by remember { mutableStateOf(false) }
    var docsConfirmed by remember { mutableStateOf(false) }
    var detailsConfirmed by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(false) }
    var consentGiven by remember { mutableStateOf(false) }

    val allConfirmed = profileConfirmed && docsConfirmed && detailsConfirmed && termsAccepted && consentGiven

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Application Summary") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Job & Fee Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(jobTitle, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color(0xFF1976D2))
                    Spacer(Modifier.height(16.dp))
                    
                    SummaryRow("Official Application Fee", officialFee)
                    SummaryRow("Our Service Fee", serviceFee)
                    HorizontalDivider(Modifier.padding(vertical = 12.dp), color = Color.White)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Amount", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(totalAmount, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF388E3C))
                    }
                }
            }

            Text("Verification & Consent", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("Please confirm the following before proceeding to payment:", fontSize = 13.sp, color = Color.Gray)

            // 2. Checkboxes for Confirmation
            ConfirmationCheckbox("I confirm my Profile Information is accurate.", profileConfirmed) { profileConfirmed = it }
            ConfirmationCheckbox("I have uploaded all Required Documents in Vault.", docsConfirmed) { docsConfirmed = it }
            ConfirmationCheckbox("I have verified the Application Details & Deadlines.", detailsConfirmed) { detailsConfirmed = it }
            ConfirmationCheckbox("I agree to the Service Terms & Conditions.", termsAccepted) { termsAccepted = it }
            ConfirmationCheckbox("I authorize the Cyber Cafe to process my application.", consentGiven) { consentGiven = it }

            Spacer(Modifier.weight(1f))
            Spacer(Modifier.height(24.dp))

            // 3. Action Button
            Button(
                onClick = onConfirm,
                enabled = allConfirmed,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (allConfirmed) Color(0xFF1976D2) else Color.LightGray
                )
            ) {
                if (allConfirmed) {
                    Icon(Icons.Default.Payment, null)
                    Spacer(Modifier.width(12.dp))
                }
                Text(
                    text = if (allConfirmed) "Proceed to Payment" else "Confirm all items to continue",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            
            AnimatedVisibility(visible = !allConfirmed) {
                Text(
                    "You must confirm all details to enable payment.",
                    color = Color(0xFFD32F2F),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp).align(Alignment.CenterHorizontally)
                )
            }
            
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.DarkGray, fontSize = 14.sp)
        Text(value, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}

@Composable
fun ConfirmationCheckbox(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if(checked) Color(0xFFF1F8E9) else Color(0xFFF5F5F5)),
        onClick = { onCheckedChange(!checked) }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF388E3C))
            )
            Spacer(Modifier.width(8.dp))
            Text(label, fontSize = 14.sp, color = if(checked) Color.Black else Color.DarkGray)
        }
    }
}

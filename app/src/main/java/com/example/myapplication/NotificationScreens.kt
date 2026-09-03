package com.example.myapplication

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// ════════════════════════════════════════════════════════════
//  Student Notification Center
// ════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationCenterScreen(
    onBack: () -> Unit,
    onOpenApplication: (String) -> Unit = {}
) {
    val notifications = NotificationStore.notifications
    var showUnreadOnly by remember { mutableStateOf(false) }

    val displayed = if (showUnreadOnly) notifications.filter { !it.isRead } else notifications

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Notifications", fontWeight = FontWeight.Bold)
                        if (NotificationStore.unreadCount > 0) {
                            Spacer(Modifier.width(8.dp))
                            Surface(
                                color = Color(0xFFD32F2F), shape = CircleShape,
                                modifier = Modifier.size(22.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        NotificationStore.unreadCount.toString(),
                                        color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { NotificationStore.markAllRead() }) {
                        Text("Mark all read", fontSize = 13.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            // Filter chip
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = !showUnreadOnly,
                    onClick = { showUnreadOnly = false },
                    label = { Text("All (${notifications.size})", fontSize = 12.sp) }
                )
                FilterChip(
                    selected = showUnreadOnly,
                    onClick = { showUnreadOnly = true },
                    label = { Text("Unread (${NotificationStore.unreadCount})", fontSize = 12.sp) }
                )
            }

            if (displayed.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.NotificationsNone, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                        Spacer(Modifier.height(12.dp))
                        Text("No notifications", color = Color.Gray, fontSize = 16.sp)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(displayed, key = { it.id }) { notif ->
                        NotificationCard(
                            notification = notif,
                            onClick = {
                                NotificationStore.markRead(notif.id)
                                if (notif.appId.isNotBlank()) onOpenApplication(notif.appId)
                            }
                        )
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
fun NotificationCard(notification: AppNotification, onClick: () -> Unit) {
    val bgColor by animateColorAsState(
        if (notification.isRead) MaterialTheme.colorScheme.surface
        else Color(notification.type.color).copy(alpha = 0.06f),
        label = "notif_bg"
    )
    val accentColor = Color(notification.type.color)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(if (notification.isRead) 0.dp else 2.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = if (!notification.isRead)
            androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.3f))
        else null
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
            // Icon circle
            Surface(
                color = accentColor.copy(alpha = 0.12f),
                shape = CircleShape,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(notification.type.emoji, fontSize = 20.sp)
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        notification.title,
                        fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier.size(8.dp)
                                .background(accentColor, CircleShape)
                        )
                    }
                }
                Spacer(Modifier.height(3.dp))
                Text(notification.body, fontSize = 12.sp, color = Color.Gray, lineHeight = 17.sp)
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(notification.timestamp, fontSize = 10.sp, color = Color.Gray)
                    if (notification.appId.isNotBlank()) {
                        Surface(
                            color = accentColor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "View Application →",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                color = accentColor, fontSize = 10.sp, fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════
//  Admin Notification Templates Screen
// ════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminNotificationTemplatesScreen(onBack: () -> Unit) {
    val templates = NotificationStore.templates
    var expandedIndex by remember { mutableStateOf<Int?>(null) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = { Text("Notification Templates", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A237E),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                // Channel Legend
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8EAF6)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Notification Channels", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1A237E))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ChannelTag("📱 In-App", Color(0xFF1565C0))
                            ChannelTag("📧 Email", Color(0xFF2E7D32))
                            ChannelTag("💬 SMS", Color(0xFFE65100))
                            ChannelTag("🟢 WhatsApp", Color(0xFF1B5E20))
                        }
                    }
                }
            }

            items(templates.size) { i ->
                val t = templates[i]
                val accentColor = Color(t.type.color)
                var localTitle by remember(t.type) { mutableStateOf(t.titleTemplate) }
                var localBody by remember(t.type) { mutableStateOf(t.bodyTemplate) }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    onClick = { expandedIndex = if (expandedIndex == i) null else i }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = accentColor.copy(alpha = 0.12f),
                                shape = CircleShape,
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(t.type.emoji, fontSize = 18.sp)
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(t.type.displayName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Channels: ${t.enabledChannels.size} active", fontSize = 11.sp, color = Color.Gray)
                            }
                            Icon(
                                if (expandedIndex == i) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                null, tint = Color.Gray
                            )
                        }

                        AnimatedVisibility(expandedIndex == i) {
                            Column(
                                modifier = Modifier.padding(top = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                HorizontalDivider()
                                OutlinedTextField(
                                    value = localTitle,
                                    onValueChange = { localTitle = it },
                                    label = { Text("Title Template") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = localBody,
                                    onValueChange = { localBody = it },
                                    label = { Text("Body Template") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    minLines = 2
                                )
                                Text("Variables: {jobTitle}, {appNo}, {amount}, {employeeName}, {deadline}, {date}, {days}, {percentage}", fontSize = 10.sp, color = Color.Gray)

                                // Channel toggles
                                Text("Enabled Channels", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf(
                                        NotificationChannel.IN_APP to "In-App",
                                        NotificationChannel.EMAIL to "Email",
                                        NotificationChannel.SMS to "SMS",
                                        NotificationChannel.WHATSAPP to "WhatsApp"
                                    ).forEach { (ch, label) ->
                                        val enabled = t.enabledChannels.contains(ch)
                                        FilterChip(
                                            selected = enabled,
                                            onClick = {
                                                if (enabled) t.enabledChannels.remove(ch)
                                                else t.enabledChannels.add(ch)
                                            },
                                            label = { Text(label, fontSize = 11.sp) }
                                        )
                                    }
                                }

                                Button(
                                    onClick = {
                                        NotificationStore.updateTemplate(t.type, localTitle, localBody, t.enabledChannels)
                                        scope.launch { snackbar.showSnackbar("Template saved for '${t.type.displayName}'") }
                                        expandedIndex = null
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Save, null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Save Template")
                                }
                            }
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun ChannelTag(label: String, color: Color) {
    Surface(color = color.copy(alpha = 0.12f), shape = RoundedCornerShape(8.dp)) {
        Text(label, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), fontSize = 11.sp, color = color, fontWeight = FontWeight.Bold)
    }
}

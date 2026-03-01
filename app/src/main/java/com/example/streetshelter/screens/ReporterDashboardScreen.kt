package com.example.streetshelter.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.streetshelter.AuthManager
import com.example.streetshelter.ReportManager
import com.example.streetshelter.models.DogReport
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReporterDashboardScreen(
    authManager: AuthManager,
    reportManager: ReportManager,
    onLogout: () -> Unit
) {
    var showReportDialog by remember { mutableStateOf(false) }
    var myReports by remember { mutableStateOf<List<DogReport>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val userEmail = authManager.getCurrentUserEmail() ?: "Unknown"

    LaunchedEffect(Unit) {
        val userId = authManager.getCurrentUserId()
        if (userId != null) {
            reportManager.getMyReports(userId) { reports, error ->
                isLoading = false
                if (reports != null) {
                    myReports = reports
                } else {
                    Toast.makeText(context, "Error loading reports: $error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    CircleShape
                                )
                                .padding(6.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Reporter Dashboard", style = MaterialTheme.typography.titleLarge)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        authManager.logout()
                        onLogout()
                    }, modifier = Modifier.testTag("logout_button")) {
                        Icon(Icons.Default.Close, contentDescription = "Logout")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showReportDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                icon = { Icon(Icons.Default.Add, "Report Dog") },
                text = { Text("Report Dog") },
                modifier = Modifier.testTag("report_dog_fab")
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                WelcomeHeader(userEmail)
            }

            item {
                ReporterStatisticsRow(myReports)
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My Reports",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "${myReports.size}",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
            else if (myReports.isEmpty()) {
                item {
                    EmptyStateCard()
                }
            }
            else {
                items(myReports) { report ->
                    EnhancedReportCard(report)
                }
            }
        }
    }

    if (showReportDialog) {
        ReportDogDialog(
            authManager = authManager,
            reportManager = reportManager,
            onDismiss = { showReportDialog = false },
            onSuccess = {
                showReportDialog = false
                val userId = authManager.getCurrentUserId()
                if (userId != null) {
                    reportManager.getMyReports(userId) { reports, _ ->
                        if (reports != null) {
                            myReports = reports
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun WelcomeHeader(userEmail: String) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("welcome_header"),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary,
                            MaterialTheme.colorScheme.tertiary
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.9f),
                                    Color.White.copy(alpha = 0.7f)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(modifier = Modifier.width(20.dp))
                Column {
                    Text(
                        text = "Welcome Back!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = userEmail,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.95f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Help rescue stray dogs",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReporterStatisticsRow(reports: List<DogReport>) {
    val pendingCount = reports.count { it.status == "PENDING" }
    val rescuedCount = reports.count { it.status == "RESCUED" }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Build,
            title = "Pending",
            count = pendingCount,
            color = MaterialTheme.colorScheme.tertiaryContainer
        )
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.CheckCircle,
            title = "Rescued",
            count = rescuedCount,
            color = MaterialTheme.colorScheme.secondaryContainer
        )
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    count: Int,
    color: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun EmptyStateCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Reports Yet",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tap the + button below to report a stray dog and help them get rescued!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun EnhancedReportCard(report: DogReport) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(report.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth().testTag("report_card_${report.id}"),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ID: ${report.id.take(8)}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
                EnhancedStatusChip(status = report.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = report.location,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CategoryBadge(category = report.category)
                PriorityBadge(priority = report.priority)
            }

            if (report.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = report.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun EnhancedStatusChip(status: String) {
    val (color, icon, textColor) = when (status) {
        "PENDING" -> Triple(
            MaterialTheme.colorScheme.tertiaryContainer,
            Icons.Default.Build,
            MaterialTheme.colorScheme.onTertiaryContainer
        )
        "RESCUED" -> Triple(
            MaterialTheme.colorScheme.primaryContainer,
            Icons.Default.CheckCircle,
            MaterialTheme.colorScheme.onPrimaryContainer
        )
        else -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            Icons.Default.Warning,
            MaterialTheme.colorScheme.onErrorContainer
        )
    }

    Surface(
        color = color,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = textColor
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = status,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val color = when (status) {
        "PENDING" -> MaterialTheme.colorScheme.tertiary
        "RESCUED" -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.error
    }

    Surface(
        color = color,
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDogDialog(
    authManager: AuthManager,
    reportManager: ReportManager,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("STRAY") }
    var selectedPriority by remember { mutableStateOf("MEDIUM") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var priorityExpanded by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val categories = listOf(
        "STRAY" to "🐕 Stray Dog",
        "INJURED" to "🏥 Injured Dog",
        "LOST" to "🔍 Lost Dog"
    )

    val priorities = listOf(
        "HIGH" to "⚠️ High Priority",
        "MEDIUM" to "📌 Medium Priority",
        "LOW" to "✅ Low Priority"
    )

    AlertDialog(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        ),
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                    )
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Report a Dog",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location *") },
                    placeholder = { Text("e.g., Main Street Park") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier.fillMaxWidth().testTag("location_field"),
                    enabled = !isSubmitting,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { if (!isSubmitting) categoryExpanded = !categoryExpanded }
                ) {
                    OutlinedTextField(
                        value = categories.find { it.first == selectedCategory }?.second ?: "STRAY",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        enabled = !isSubmitting,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categories.forEach { (value, label) ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        label,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                },
                                onClick = {
                                    selectedCategory = value
                                    categoryExpanded = false
                                },
                                leadingIcon = {
                                    Icon(
                                        when (value) {
                                            "STRAY" -> Icons.Default.Home
                                            "INJURED" -> Icons.Default.Star
                                            "LOST" -> Icons.Default.Search
                                            else -> Icons.Default.Info
                                        },
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = priorityExpanded,
                    onExpandedChange = { if (!isSubmitting) priorityExpanded = !priorityExpanded }
                ) {
                    OutlinedTextField(
                        value = priorities.find { it.first == selectedPriority }?.second ?: "MEDIUM",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Priority *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        enabled = !isSubmitting,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = priorityExpanded,
                        onDismissRequest = { priorityExpanded = false }
                    ) {
                        priorities.forEach { (value, label) ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        label,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                },
                                onClick = {
                                    selectedPriority = value
                                    priorityExpanded = false
                                },
                                leadingIcon = {
                                    Icon(
                                        when (value) {
                                            "HIGH" -> Icons.Default.Warning
                                            "MEDIUM" -> Icons.Default.Info
                                            "LOW" -> Icons.Default.CheckCircle
                                            else -> Icons.Default.Info
                                        },
                                        contentDescription = null,
                                        tint = when (value) {
                                            "HIGH" -> MaterialTheme.colorScheme.error
                                            "MEDIUM" -> MaterialTheme.colorScheme.primary
                                            "LOW" -> MaterialTheme.colorScheme.tertiary
                                            else -> MaterialTheme.colorScheme.primary
                                        }
                                    )
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    placeholder = { Text("Additional details about the dog...") },
                    modifier = Modifier.fillMaxWidth().testTag("description_field"),
                    minLines = 3,
                    maxLines = 5,
                    enabled = !isSubmitting,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Text(
                    text = "* Required fields",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (location.isBlank()) {
                        Toast.makeText(context, "Please enter a location", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isSubmitting = true
                    val userId = authManager.getCurrentUserId()
                    val userEmail = authManager.getCurrentUserEmail()

                    if (userId != null && userEmail != null) {
                        reportManager.submitReport(
                            reporterId = userId,
                            reporterEmail = userEmail,
                            location = location,
                            dogType = selectedCategory,
                            category = selectedCategory,
                            priority = selectedPriority,
                            description = description
                        ) { success, error ->
                            isSubmitting = false
                            if (success) {
                                Toast.makeText(context, "✅ Report submitted successfully! Owner will be notified.", Toast.LENGTH_LONG).show()
                                onSuccess()
                            } else {
                                Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                enabled = !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("submit_report_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Submit Report",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                enabled = !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Cancel",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
private fun CategoryBadge(category: String) {
    val (emoji, color, textColor) = when (category) {
        "STRAY" -> Triple("🐕", MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer)
        "LOST" -> Triple("🔍", MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
        "INJURED" -> Triple("🏥", MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer)
        else -> Triple("🐶", MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
    }

    Surface(
        color = color,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = category,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Composable
private fun PriorityBadge(priority: String) {
    val (icon, color, textColor) = when (priority) {
        "HIGH" -> Triple(Icons.Default.Warning, MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer)
        "MEDIUM" -> Triple(Icons.Default.Info, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer)
        "LOW" -> Triple(Icons.Default.CheckCircle, MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer)
        else -> Triple(Icons.Default.Info, MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
    }

    Surface(
        color = color,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = textColor
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = priority,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}


package com.example.streetshelter.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.streetshelter.AuthManager
import com.example.streetshelter.ReportManager
import com.example.streetshelter.models.DogReport
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerDashboardScreen(
    authManager: AuthManager,
    reportManager: ReportManager,
    onLogout: () -> Unit
) {
    var allReports by remember { mutableStateOf<List<DogReport>>(emptyList()) }
    var filteredReports by remember { mutableStateOf<List<DogReport>>(emptyList()) }
    var selectedFilter by remember { mutableStateOf("ALL") }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val userEmail = authManager.getCurrentUserEmail() ?: "Unknown"

    LaunchedEffect(Unit) {
        reportManager.getAllReports { reports, error ->
            isLoading = false
            if (reports != null) {
                allReports = reports
                filteredReports = reports
            } else {
                Toast.makeText(context, "Error loading reports: $error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(selectedFilter, allReports) {
        filteredReports = when (selectedFilter) {
            "PENDING" -> allReports.filter { it.status == "PENDING" }
            "RESCUED" -> allReports.filter { it.status == "RESCUED" }
            else -> allReports
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    MaterialTheme.colorScheme.secondaryContainer,
                                    CircleShape
                                )
                                .padding(6.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Rescue Dashboard", style = MaterialTheme.typography.titleLarge)
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
                OwnerWelcomeHeader(userEmail)
            }

            item {
                OwnerStatisticsRow(allReports)
            }

            item {
                Column {
                    Text(
                        text = "Filter Reports",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ModernFilterChips(
                        selectedFilter = selectedFilter,
                        onFilterSelected = { selectedFilter = it },
                        allReports = allReports
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Dog Reports",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = "${filteredReports.size}",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
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
            else if (filteredReports.isEmpty()) {
                item {
                    OwnerEmptyStateCard(selectedFilter)
                }
            }
            else {
                items(filteredReports) { report ->
                    EnhancedOwnerReportCard(
                        report = report,
                        reportManager = reportManager,
                        onReportUpdated = {
                            reportManager.getAllReports { reports, _ ->
                                if (reports != null) {
                                    allReports = reports
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun OwnerWelcomeHeader(userEmail: String) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("welcome_header"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.secondary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Rescue Owner",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = userEmail,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "Make a difference, save lives 🐾",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun OwnerStatisticsRow(reports: List<DogReport>) {
    val totalCount = reports.size
    val pendingCount = reports.count { it.status == "PENDING" }
    val rescuedCount = reports.count { it.status == "RESCUED" }
    val urgentCount = reports.count { it.priority == "HIGH" && it.status == "PENDING" }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OwnerStatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Menu,
                title = "Total",
                count = totalCount,
                color = MaterialTheme.colorScheme.primaryContainer
            )
            OwnerStatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Build,
                title = "Pending",
                count = pendingCount,
                color = MaterialTheme.colorScheme.tertiaryContainer
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OwnerStatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.CheckCircle,
                title = "Rescued",
                count = rescuedCount,
                color = MaterialTheme.colorScheme.secondaryContainer
            )
            OwnerStatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Warning,
                title = "Urgent",
                count = urgentCount,
                color = MaterialTheme.colorScheme.errorContainer
            )
        }
    }
}

@Composable
fun OwnerStatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    count: Int,
    color: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun ModernFilterChips(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    allReports: List<DogReport>
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        ModernFilterChip(
            modifier = Modifier.weight(1f),
            label = "All",
            count = allReports.size,
            selected = selectedFilter == "ALL",
            onClick = { onFilterSelected("ALL") },
            icon = Icons.Default.Menu
        )
        ModernFilterChip(
            modifier = Modifier.weight(1f),
            label = "Pending",
            count = allReports.count { it.status == "PENDING" },
            selected = selectedFilter == "PENDING",
            onClick = { onFilterSelected("PENDING") },
            icon = Icons.Default.Build
        )
        ModernFilterChip(
            modifier = Modifier.weight(1f),
            label = "Rescued",
            count = allReports.count { it.status == "RESCUED" },
            selected = selectedFilter == "RESCUED",
            onClick = { onFilterSelected("RESCUED") },
            icon = Icons.Default.CheckCircle
        )
    }
}

@Composable
fun ModernFilterChip(
    modifier: Modifier = Modifier,
    label: String,
    count: Int,
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector
) {
    val containerColor = if (selected)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.surfaceVariant

    val contentColor = if (selected)
        MaterialTheme.colorScheme.onPrimary
    else
        MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(12.dp),
        elevation = if (selected) CardDefaults.cardElevation(4.dp) else CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = contentColor
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun OwnerEmptyStateCard(filter: String) {
    val message = when (filter) {
        "PENDING" -> "No pending reports"
        "RESCUED" -> "No rescued dogs yet"
        else -> "No reports available"
    }

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
                text = message,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Check back later or try a different filter",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun EnhancedOwnerReportCard(
    report: DogReport,
    reportManager: ReportManager,
    onReportUpdated: () -> Unit
) {
    var showRescueDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
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
                OwnerEnhancedStatusChip(status = report.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
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

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Reported by: ${report.reporterEmail}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))


            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = report.dogType,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.SemiBold
                    )
                }
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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
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

                if (report.status == "PENDING") {
                    FilledTonalButton(
                        onClick = { showRescueDialog = true },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.testTag("rescue_button_${report.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Rescue")
                    }
                }
            }
        }
    }

    if (showRescueDialog) {
        AlertDialog(
            onDismissRequest = { showRescueDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = { Text("Confirm Rescue") },
            text = { Text("Are you sure you want to mark this dog as rescued? This will notify the reporter.") },
            confirmButton = {
                Button(
                    onClick = {
                        reportManager.updateReportStatus(report.id, "RESCUED") { success, error ->
                            if (success) {
                                Toast.makeText(context, "✅ Dog marked as rescued!", Toast.LENGTH_SHORT).show()
                                showRescueDialog = false
                                onReportUpdated()
                            } else {
                                Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.testTag("confirm_rescue_dialog_button")
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRescueDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun OwnerEnhancedStatusChip(status: String) {
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


package com.example.tmrcatalog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.tmrcatalog.viewmodel.CatalogViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryTableScreen(
    viewModel: CatalogViewModel,
    onBackToHome: () -> Unit
) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val todayStr = remember { LocalDate.now().format(dateFormatter) }
    val availableDates = viewModel.getAvailableDates()

    var selectedDate by remember {
        mutableStateOf(if (availableDates.isNotEmpty()) availableDates.first() else todayStr)
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Registro Diario de TMR", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackToHome) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver al Menú",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Registros guardados (Mantenimiento de 12 días)",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Selector Horizontal de Fechas
            if (availableDates.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    items(availableDates) { dateStr ->
                        FilterChip(
                            selected = (dateStr == selectedDate),
                            onClick = { selectedDate = dateStr },
                            label = { Text(text = dateStr) }
                        )
                    }
                }
            } else {
                Text(
                    text = "No hay registros guardados aún. Guarda el corte desde el catálogo.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Encabezado de la Tabla para el Día Seleccionado
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .border(1.dp, Color.Gray)
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TableCell(text = "Código", weight = 1.2f, isHeader = true)
                TableCell(text = "Producto", weight = 2f, isHeader = true)
                TableCell(text = "12:00", weight = 1f, isHeader = true)
                TableCell(text = "15:00", weight = 1f, isHeader = true)
                TableCell(text = "18:00", weight = 1f, isHeader = true)
            }

            // Filas de Productos para la Fecha Seleccionada
            LazyColumn {
                items(viewModel.productList, key = { it.id }) { product ->
                    val recordForDate = product.recordsByDate[selectedDate]

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .border(0.5.dp, Color.Gray)
                            .padding(vertical = 8.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TableCell(text = product.code, weight = 1.2f)
                        TableCell(text = product.name, weight = 2f)
                        TableCell(text = recordForDate?.qty12?.toString() ?: "-", weight = 1f)
                        TableCell(text = recordForDate?.qty15?.toString() ?: "-", weight = 1f)
                        TableCell(text = recordForDate?.qty18?.toString() ?: "-", weight = 1f)
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float,
    isHeader: Boolean = false
) {
    Text(
        text = text,
        modifier = Modifier.weight(weight),
        textAlign = TextAlign.Center,
        fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
        color = Color.White,
        style = if (isHeader) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall
    )
}
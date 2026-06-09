package com.umas97.hiceram.ui.screens

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.onFocusChanged
import android.location.Geocoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.zIndex
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.umas97.hiceram.data.Moment
import com.umas97.hiceram.ui.viewmodel.MomentViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    moment: Moment,
    viewModel: MomentViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    // Osserva la lista dei ricordi dal ViewModel per avere aggiornamenti in tempo reale (es. preferiti)
    val moments by viewModel.moments.collectAsState()
    val liveMoment = moments.find { it.id == moment.id } ?: moment

    // Pager state per il carosello di immagini
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { liveMoment.imagePaths.size }
    )

    // Mappa per memorizzare l'aspect ratio (larghezza / altezza) di ogni immagine caricata
    var aspectRatios by remember { mutableStateOf(mapOf<Int, Float>()) }
    var isZoomed by remember { mutableStateOf(false) }
    
    // Legge il ratio corrente del Pager in base alla pagina visibile (default 1f/quadrato)
    val currentRatio = aspectRatios[pagerState.currentPage] ?: 1f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState(), enabled = !isZoomed)
        ) {
            // Spacer iniziale dinamico per la TopAppBar (64.dp + altezza della status bar di sistema)
            Spacer(modifier = Modifier.height(WindowInsets.systemBars.asPaddingValues().calculateTopPadding() + 64.dp + 4.dp))

            // 1. Carosello Immagini con Proporzioni ed Altezza Dinamica Originale
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(currentRatio) // Imposta l'altezza in base al formato reale dell'immagine corrente
                    .zIndex(if (isZoomed) 100f else 1f)
                    .background(Color.Black.copy(alpha = 0.05f))
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    userScrollEnabled = !isZoomed
                ) { page ->
                    val imagePath = liveMoment.imagePaths[page]
                    
                    var targetScale by remember { mutableStateOf(1f) }
                    var targetOffsetX by remember { mutableStateOf(0f) }
                    var targetOffsetY by remember { mutableStateOf(0f) }

                    val scale by animateFloatAsState(targetValue = targetScale, label = "scale")
                    val offsetX by animateFloatAsState(targetValue = targetOffsetX, label = "offsetX")
                    val offsetY by animateFloatAsState(targetValue = targetOffsetY, label = "offsetY")

                    LaunchedEffect(targetScale) {
                        isZoomed = targetScale > 1f
                    }

                    AsyncImage(
                        model = File(imagePath),
                        contentDescription = "Foto del ricordo",
                        contentScale = ContentScale.Fit, // Mostra l'immagine nella dimensione originale
                        onSuccess = { state ->
                            val drawable = state.result.drawable
                            val width = drawable.intrinsicWidth.toFloat()
                            val height = drawable.intrinsicHeight.toFloat()
                            if (width > 0 && height > 0) {
                                aspectRatios = aspectRatios.toMutableMap().apply {
                                    put(page, width / height)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .zIndex(if (targetScale > 1f) 10f else 1f)
                            .pointerInput(Unit) {
                                awaitEachGesture {
                                    awaitFirstDown(requireUnconsumed = false)
                                    do {
                                        val event = awaitPointerEvent()
                                        val zoomChange = event.calculateZoom()
                                        val panChange = event.calculatePan()

                                        if (event.changes.size > 1) {
                                            targetScale = (targetScale * zoomChange).coerceIn(1f, 5f)
                                            if (targetScale > 1f) {
                                                targetOffsetX += panChange.x
                                                targetOffsetY += panChange.y
                                                event.changes.forEach { it.consume() }
                                            }
                                        } else if (targetScale > 1f) {
                                            targetOffsetX += panChange.x
                                            targetOffsetY += panChange.y
                                            event.changes.forEach { it.consume() }
                                        }
                                    } while (event.changes.any { it.pressed })

                                    // Rilasciate tutte le dita: ripristino istantaneo dei target (che avviano l'animazione di rientro)
                                    targetScale = 1f
                                    targetOffsetX = 0f
                                    targetOffsetY = 0f
                                }
                            }
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                translationX = offsetX
                                translationY = offsetY
                            }
                    )
                }
            }

            // 2. Indicatori Pagina (Puntini) se ci sono più foto
            if (liveMoment.imagePaths.size > 1) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(liveMoment.imagePaths.size) { iteration ->
                        val isSelected = pagerState.currentPage == iteration
                        Box(
                            modifier = Modifier
                                .padding(3.dp)
                                .size(if (isSelected) 6.dp else 4.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4. Descrizione pulita (senza nome utente)
            if (liveMoment.description.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = liveMoment.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // TopAppBar in overlay (zIndex 50f) in modo che lo zoom dell'immagine (zIndex 100f) possa passarci sopra
        TopAppBar(
            modifier = Modifier.zIndex(50f),
            title = {
                Column {
                    Text(
                        text = formatTimestamp(liveMoment.timestamp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (!liveMoment.locationName.isNullOrEmpty()) {
                        Text(
                            text = liveMoment.locationName,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Torna indietro"
                    )
                }
            },
            actions = {
                // Cuore (Preferiti)
                IconButton(onClick = { viewModel.toggleFavorite(liveMoment) }) {
                    Icon(
                        imageVector = if (liveMoment.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Preferiti",
                        tint = if (liveMoment.isFavorite) Color.Red else MaterialTheme.colorScheme.onBackground
                    )
                }
                // Matita (Modifica)
                IconButton(onClick = { showEditDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Modifica ricordo"
                    )
                }
                // Cestino (Elimina) - con colore onBackground per essere bianco/nero a seconda del tema
                IconButton(onClick = { showDeleteConfirm = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Sposta nel cestino"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                titleContentColor = MaterialTheme.colorScheme.onBackground,
                navigationIconContentColor = MaterialTheme.colorScheme.onBackground
            )
        )
    }

    // Dialog di conferma spostamento nel Cestino
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(text = "Elimina ricordo", style = MaterialTheme.typography.titleMedium) },
            text = {
                Text(
                    text = "Vuoi spostare questo ricordo nel Cestino? Potrai ripristinarlo in qualsiasi momento dal menu principale.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        viewModel.setMomentTrashStatus(liveMoment, true) { success ->
                            if (success) {
                                Toast.makeText(context, "Spostato nel Cestino", Toast.LENGTH_SHORT).show()
                                onNavigateBack()
                            } else {
                                Toast.makeText(context, "Errore durante lo spostamento", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                ) {
                    Text(text = "Sposta nel Cestino", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(text = "Annulla")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    // Dialog di Modifica Ricordo (Descrizione e Data)
    if (showEditDialog) {
        var editDescription by remember { mutableStateOf(liveMoment.description) }
        var editLocation by remember { mutableStateOf(liveMoment.locationName ?: "") }
        var editDate by remember { mutableStateOf(liveMoment.timestamp) }
        var showDatePicker by remember { mutableStateOf(false) }
        
        var isLocationFocused by remember { mutableStateOf(false) }
        var locationSuggestions by remember { mutableStateOf(emptyList<String>()) }
        var showSuggestions by remember { mutableStateOf(false) }

        LaunchedEffect(editLocation) {
            if (editLocation.length >= 3 && isLocationFocused) {
                delay(500) // Debounce
                withContext(Dispatchers.IO) {
                    try {
                        val geocoder = Geocoder(context, Locale.getDefault())
                        @Suppress("DEPRECATION")
                        val addresses = geocoder.getFromLocationName(editLocation, 5)
                        val suggestions = addresses?.mapNotNull { address ->
                            val parts = listOfNotNull(address.locality, address.adminArea, address.countryName)
                            if (parts.isNotEmpty()) parts.joinToString(", ") else address.featureName
                        }?.distinct() ?: emptyList()
                        
                        withContext(Dispatchers.Main) {
                            locationSuggestions = suggestions
                            showSuggestions = suggestions.isNotEmpty()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            locationSuggestions = emptyList()
                            showSuggestions = false
                        }
                    }
                }
            } else {
                showSuggestions = false
            }
        }
        
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text(text = "Modifica Ricordo", style = MaterialTheme.typography.titleMedium) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Descrizione TextField
                    TextField(
                        value = editDescription,
                        onValueChange = { editDescription = it },
                        label = { Text("Descrizione") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Selettore Data
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { showDatePicker = true }
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Calendario",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = formatTimestamp(editDate),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    // Posizione TextField con Dropdown
                    Box(modifier = Modifier.fillMaxWidth()) {
                        TextField(
                            value = editLocation,
                            onValueChange = { editLocation = it },
                            label = { Text("Posizione") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { isLocationFocused = it.isFocused },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Place, contentDescription = "Posizione")
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                            )
                        )
                        
                        DropdownMenu(
                            expanded = showSuggestions,
                            onDismissRequest = { showSuggestions = false },
                            modifier = Modifier.fillMaxWidth(0.8f),
                            properties = androidx.compose.ui.window.PopupProperties(focusable = false)
                        ) {
                            locationSuggestions.forEach { suggestion ->
                                DropdownMenuItem(
                                    text = { Text(suggestion) },
                                    onClick = {
                                        editLocation = suggestion
                                        showSuggestions = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showEditDialog = false
                        viewModel.updateMomentDetails(
                            moment = liveMoment,
                            newDescription = editDescription,
                            newTimestamp = editDate,
                            newLocationName = editLocation
                        )
                    }
                ) {
                    Text("Salva")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Annulla")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
        
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = editDate,
                selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        return utcTimeMillis <= System.currentTimeMillis()
                    }
                }
            )
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            editDate = datePickerState.selectedDateMillis ?: editDate
                            showDatePicker = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Annulla")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.ITALIAN)
    return sdf.format(Date(timestamp))
}

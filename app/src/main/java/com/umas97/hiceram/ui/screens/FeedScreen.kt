package com.umas97.hiceram.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.clickable
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FeedScreen(
    viewModel: MomentViewModel,
    onNavigateToAdd: () -> Unit,
    onNavigateToDetail: (Moment) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToMap: () -> Unit
) {
    val context = LocalContext.current
    val moments by viewModel.moments.collectAsState()
    val onThisDayMoments by viewModel.onThisDayMoments.collectAsState()

    // Stati filtri e viste
    var showOnlyFavorites by remember { mutableStateOf(false) }
    var showTrash by remember { mutableStateOf(false) }
    var showOnThisDayFilter by remember { mutableStateOf(false) }
    
    // Stati selezione multipla
    var isMultiSelectMode by remember { mutableStateOf(false) }
    var selectedMoments by remember { mutableStateOf(setOf<Moment>()) }

    // Stati Dialogs
    var showDeletePermanentlyDialog by remember { mutableStateOf(false) }
    var clickedTrashMoment by remember { mutableStateOf<Moment?>(null) }

    // Resetta la selezione quando si cambia vista
    val exitMultiSelect = {
        selectedMoments = emptySet()
        isMultiSelectMode = false
    }

    BackHandler(enabled = isMultiSelectMode || showTrash || showOnlyFavorites || showOnThisDayFilter) {
        if (isMultiSelectMode) {
            exitMultiSelect()
        } else if (showTrash) {
            showTrash = false
        } else if (showOnlyFavorites) {
            showOnlyFavorites = false
        } else if (showOnThisDayFilter) {
            showOnThisDayFilter = false
        }
    }

    // Filtra i momenti in base allo stato attivo/cestino/preferiti
    val displayedMoments = if (showOnThisDayFilter) {
        onThisDayMoments
    } else {
        moments.filter { moment ->
            if (showTrash) {
                moment.isInTrash
            } else {
                !moment.isInTrash && (!showOnlyFavorites || moment.isFavorite)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (!isMultiSelectMode && !showTrash && !showOnThisDayFilter) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = androidx.compose.ui.res.painterResource(id = com.umas97.hiceram.R.drawable.ic_crystal),
                                contentDescription = "Cristallo",
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Hic Eram",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Text(
                            text = when {
                                isMultiSelectMode -> "${selectedMoments.size} selezionati"
                                showTrash -> "Cestino"
                                showOnThisDayFilter -> "Accadde Oggi"
                                else -> ""
                            },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    if (isMultiSelectMode) {
                        IconButton(onClick = exitMultiSelect) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Annulla selezione")
                        }
                    } else if (showTrash || showOnThisDayFilter) {
                        IconButton(onClick = { 
                            showTrash = false
                            showOnThisDayFilter = false
                            exitMultiSelect() 
                        }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Torna al feed")
                        }
                    }
                },
                actions = {
                    if (isMultiSelectMode) {
                        if (showTrash) {
                            // Azioni multiple in modalità Cestino
                            IconButton(
                                onClick = {
                                    viewModel.setMomentsTrashStatus(selectedMoments.toList(), false) { success ->
                                        if (success) {
                                            Toast.makeText(context, "Ricordi ripristinati", Toast.LENGTH_SHORT).show()
                                            exitMultiSelect()
                                        }
                                    }
                                }
                            ) {
                                Icon(imageVector = Icons.Default.Restore, contentDescription = "Ripristina selezionati")
                            }
                            IconButton(onClick = { showDeletePermanentlyDialog = true }) {
                                Icon(imageVector = Icons.Default.DeleteForever, contentDescription = "Elimina definitivamente")
                            }
                        } else {
                            // Azioni multiple in modalità Feed attivo (sposta nel Cestino)
                            IconButton(
                                onClick = {
                                    viewModel.setMomentsTrashStatus(selectedMoments.toList(), true) { success ->
                                        if (success) {
                                            Toast.makeText(context, "Spostati nel cestino", Toast.LENGTH_SHORT).show()
                                            exitMultiSelect()
                                        }
                                    }
                                }
                            ) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Sposta nel cestino")
                            }
                        }
                    } else {
                        // Icone quando NON siamo in selezione multipla
                        if (!showTrash && !showOnThisDayFilter) {
                            // Filtro Preferiti (mostra solo se non siamo nel cestino o accadde oggi)
                            IconButton(onClick = { showOnlyFavorites = !showOnlyFavorites }) {
                                Icon(
                                    imageVector = if (showOnlyFavorites) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Filtra preferiti",
                                    tint = if (showOnlyFavorites) Color.Red else MaterialTheme.colorScheme.onBackground
                                )
                            }
                            // Bottone per entrare nel Cestino
                            IconButton(onClick = { showTrash = true; showOnlyFavorites = false }) {
                                Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Apri cestino")
                            }
                            // Bottone Mappa
                            IconButton(onClick = onNavigateToMap) {
                                Icon(imageVector = Icons.Default.Place, contentDescription = "Mappa dei ricordi")
                            }
                        }
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(imageVector = Icons.Default.Settings, contentDescription = "Impostazioni")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            // Mostra il FAB solo nel feed normale attivo e non in selezione multipla
            AnimatedVisibility(
                visible = !showTrash && !isMultiSelectMode,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                FloatingActionButton(
                    onClick = onNavigateToAdd,
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Aggiungi Ricordo")
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (displayedMoments.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (showTrash) "Il cestino è vuoto" else if (showOnlyFavorites) "Nessun preferito" else "Nessun ricordo ancora.",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (showTrash) "I ricordi eliminati appariranno qui" else if (showOnlyFavorites) "Tocca il cuore sui tuoi ricordi per aggiungerli qui" else "Tocca il tasto + per salvare il tuo primo momento.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (onThisDayMoments.isNotEmpty() && !showTrash && !showOnThisDayFilter && !showOnlyFavorites) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            val allImagesOnThisDay = remember(onThisDayMoments) {
                                onThisDayMoments.flatMap { it.imagePaths }
                            }
                            val pagerState = rememberPagerState(
                                initialPage = 0,
                                pageCount = { allImagesOnThisDay.size }
                            )

                            LaunchedEffect(pagerState) {
                                if (allImagesOnThisDay.isNotEmpty()) {
                                    while (true) {
                                        delay(3000)
                                        val nextPage = (pagerState.currentPage + 1) % allImagesOnThisDay.size
                                        pagerState.animateScrollToPage(nextPage)
                                    }
                                }
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(3f)
                                    .padding(bottom = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { showOnThisDayFilter = true },
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    if (allImagesOnThisDay.isNotEmpty()) {
                                        HorizontalPager(
                                            state = pagerState,
                                            modifier = Modifier.fillMaxSize()
                                        ) { page ->
                                            val imagePath = allImagesOnThisDay[page]
                                            AsyncImage(
                                                model = File(imagePath),
                                                contentDescription = "Ricordo",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color.Black.copy(alpha = 0.45f))
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "Accadde Oggi",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = if (allImagesOnThisDay.isNotEmpty()) Color.White else MaterialTheme.colorScheme.onPrimaryContainer,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Riscopri ${onThisDayMoments.size} ricordi del passato",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (allImagesOnThisDay.isNotEmpty()) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }
                        }
                    }

                    items(displayedMoments, key = { it.id }) { moment ->
                        val isSelected = selectedMoments.contains(moment)
                        MomentGridItem(
                            moment = moment,
                            isSelected = isSelected,
                            isMultiSelectMode = isMultiSelectMode,
                            onClick = {
                                if (isMultiSelectMode) {
                                    selectedMoments = if (isSelected) {
                                        selectedMoments - moment
                                    } else {
                                        selectedMoments + moment
                                    }
                                    if (selectedMoments.isEmpty()) {
                                        isMultiSelectMode = false
                                    }
                                } else {
                                    if (showTrash) {
                                        clickedTrashMoment = moment
                                    } else {
                                        onNavigateToDetail(moment)
                                    }
                                }
                            },
                            onLongClick = {
                                if (!isMultiSelectMode) {
                                    isMultiSelectMode = true
                                    selectedMoments = setOf(moment)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Rimosso Dialog Info Privacy (ora nelle Impostazioni)

    // Dialog Eliminazione Definitiva Multipla
    if (showDeletePermanentlyDialog) {
        AlertDialog(
            onDismissRequest = { showDeletePermanentlyDialog = false },
            title = { Text(text = "Elimina definitivamente", style = MaterialTheme.typography.titleMedium) },
            text = {
                Text(
                    text = "Sei sicuro di voler eliminare definitivamente i ${selectedMoments.size} ricordi selezionati? Questa azione è irreversibile e le foto verranno rimosse dal telefono.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeletePermanentlyDialog = false
                        viewModel.deleteMomentsPermanently(selectedMoments.toList()) { success ->
                            if (success) {
                                Toast.makeText(context, "Ricordi eliminati definitivamente", Toast.LENGTH_SHORT).show()
                                exitMultiSelect()
                            }
                        }
                    }
                ) {
                    Text(text = "Elimina tutto", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeletePermanentlyDialog = false }) {
                    Text(text = "Annulla")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    // Dialog Azione Singola su Ricordo nel Cestino (con Carosello Swipeable delle immagini)
    clickedTrashMoment?.let { moment ->
        AlertDialog(
            onDismissRequest = { clickedTrashMoment = null },
            title = { Text(text = "Dettaglio Ricordo Cestinato", style = MaterialTheme.typography.titleMedium) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val pagerState = rememberPagerState(
                        initialPage = 0,
                        pageCount = { moment.imagePaths.size }
                    )
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.05f))
                    ) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize()
                        ) { page ->
                            val imagePath = moment.imagePaths[page]
                            AsyncImage(
                                model = File(imagePath),
                                contentDescription = "Anteprima immagine",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                    
                    if (moment.imagePaths.size > 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(moment.imagePaths.size) { iteration ->
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
                    if (moment.description.isNotEmpty()) {
                        Text(
                            text = moment.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    Text(
                        text = "Scegli se ripristinare il ricordo nel diario o eliminarlo definitivamente.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.setMomentTrashStatus(moment, false) { success ->
                            if (success) {
                                Toast.makeText(context, "Ricordo ripristinato", Toast.LENGTH_SHORT).show()
                                clickedTrashMoment = null
                            }
                        }
                    }
                ) {
                    Text(text = "Ripristina")
                }
            },
            dismissButton = {
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            viewModel.deleteMoment(moment) { success ->
                                if (success) {
                                    Toast.makeText(context, "Eliminato definitivamente", Toast.LENGTH_SHORT).show()
                                    clickedTrashMoment = null
                                }
                            }
                        }
                    ) {
                        Text(text = "Elimina Definitivamente", color = MaterialTheme.colorScheme.error)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = { clickedTrashMoment = null }) {
                        Text(text = "Annulla")
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MomentGridItem(
    moment: Moment,
    isSelected: Boolean,
    isMultiSelectMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val firstImagePath = moment.imagePaths.firstOrNull() ?: ""
    
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (firstImagePath.isNotEmpty()) {
                AsyncImage(
                    model = File(firstImagePath),
                    contentDescription = moment.description,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Overlay posizione
            if (!moment.locationName.isNullOrEmpty()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                            )
                        )
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = moment.locationName,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }

            // Overlay di selezione stile galleria premium
            if (isMultiSelectMode) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                            else Color.Transparent
                        )
                )

                // Checkbox / Badge visivo
                Box(
                    modifier = Modifier
                        .padding(6.dp)
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else Color.Black.copy(alpha = 0.4f)
                        )
                        .border(
                            width = 1.5.dp,
                            color = if (isSelected) Color.Transparent else Color.White,
                            shape = CircleShape
                        )
                        .align(Alignment.TopEnd),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selezionato",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

package com.umas97.hiceram.ui.screens

import android.content.Context
import androidx.core.content.ContextCompat
import com.umas97.hiceram.R
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.umas97.hiceram.data.Moment
import com.umas97.hiceram.ui.viewmodel.MomentViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MomentViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (Moment) -> Unit
) {
    val context = LocalContext.current
    val moments by viewModel.moments.collectAsState()

    // Setup osmdroid configuration
    remember {
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = context.packageName
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mappa dei Ricordi", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Torna indietro")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            val mapView = remember {
                MapView(context).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(4.0)
                    
                    // Centra sull'Italia o sul primo ricordo disponibile
                    val firstLoc = moments.firstOrNull { it.latitude != null && it.longitude != null && !it.isInTrash }
                    if (firstLoc != null) {
                        controller.setCenter(GeoPoint(firstLoc.latitude!!, firstLoc.longitude!!))
                        controller.setZoom(6.0)
                    } else {
                        controller.setCenter(GeoPoint(41.9028, 12.4964)) // Roma
                    }
                }
            }

            androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    when (event) {
                        Lifecycle.Event.ON_RESUME -> mapView.onResume()
                        Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                        else -> {}
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                    mapView.onDetach()
                }
            }

            AndroidView(
                factory = { mapView },
                update = { view ->
                    view.overlays.clear()
                    
                    moments.filter { !it.isInTrash && it.latitude != null && it.longitude != null }.forEach { moment ->
                        val marker = Marker(view)
                        marker.icon = ContextCompat.getDrawable(context, R.drawable.ic_minimal_marker)
                        marker.position = GeoPoint(moment.latitude!!, moment.longitude!!)
                        marker.title = moment.locationName ?: "Ricordo"
                        marker.snippet = moment.description.takeIf { it.isNotEmpty() } ?: "Tocca per vedere"
                        marker.setOnMarkerClickListener { _, _ ->
                            onNavigateToDetail(moment)
                            true
                        }
                        view.overlays.add(marker)
                    }
                    view.invalidate()
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

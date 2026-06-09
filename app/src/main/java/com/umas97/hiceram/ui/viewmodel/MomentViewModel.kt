package com.umas97.hiceram.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.umas97.hiceram.data.Moment
import com.umas97.hiceram.data.MomentRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import java.util.Calendar
import kotlinx.coroutines.launch

/**
 * ViewModel per gestire lo stato della UI dei ricordi (Momenti) 
 * ed esporre le azioni sul repository.
 */
class MomentViewModel(private val repository: MomentRepository) : ViewModel() {

    /**
     * Flusso di dati di tipo StateFlow che emette la lista dei ricordi in ordine cronologico.
     * Mantiene in cache l'ultimo valore emesso e si disattiva quando non ci sono sottoscrittori.
     */
    val moments: StateFlow<List<Moment>> = repository.allMoments.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    /**
     * Flusso di dati che contiene solo i ricordi di esattamente 1, 2 o 5 anni fa rispetto alla data odierna.
     */
    val onThisDayMoments: StateFlow<List<Moment>> = repository.allMoments.map { momentsList ->
        val today = Calendar.getInstance()
        val todayDay = today.get(Calendar.DAY_OF_MONTH)
        val todayMonth = today.get(Calendar.MONTH)
        val todayYear = today.get(Calendar.YEAR)

        momentsList.filter { moment ->
            if (moment.isInTrash) return@filter false

            val momentCalendar = Calendar.getInstance().apply { timeInMillis = moment.timestamp }
            val momentDay = momentCalendar.get(Calendar.DAY_OF_MONTH)
            val momentMonth = momentCalendar.get(Calendar.MONTH)
            val momentYear = momentCalendar.get(Calendar.YEAR)

            val diffYears = todayYear - momentYear
            momentDay == todayDay && momentMonth == todayMonth && (diffYears == 1 || diffYears == 2 || diffYears == 5)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    /**
     * Salva un nuovo momento copiando la lista di immagini ed inserendo i dettagli nel DB con data manuale.
     */
    fun addMoment(imageUris: List<Uri>, description: String, timestamp: Long, locationName: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.saveMoment(imageUris, description, timestamp, locationName)
            onComplete(success)
        }
    }

    /**
     * Aggiorna lo stato dei preferiti (aggiunge/rimuove).
     */
    fun toggleFavorite(moment: Moment) {
        viewModelScope.launch {
            val updatedMoment = moment.copy(isFavorite = !moment.isFavorite)
            repository.updateMoment(updatedMoment)
        }
    }

    /**
     * Aggiorna un momento esistente nel DB.
     */
    fun updateMoment(moment: Moment, onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val success = repository.updateMoment(moment)
            onComplete(success)
        }
    }

    /**
     * Aggiorna i dettagli di un momento inclusa la posizione.
     */
    fun updateMomentDetails(moment: Moment, newDescription: String, newTimestamp: Long, newLocationName: String, onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val updatedMoment = moment.copy(description = newDescription, timestamp = newTimestamp)
            val success = repository.updateMomentWithLocation(updatedMoment, newLocationName)
            onComplete(success)
        }
    }

    /**
     * Sposta un momento nel cestino o lo ripristina dal cestino.
     */
    fun setMomentTrashStatus(moment: Moment, isInTrash: Boolean, onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val updatedMoment = moment.copy(isInTrash = isInTrash)
            val success = repository.updateMoment(updatedMoment)
            onComplete(success)
        }
    }

    /**
     * Imposta lo stato del cestino per più ricordi contemporaneamente (selezione multipla).
     */
    fun setMomentsTrashStatus(momentsList: List<Moment>, isInTrash: Boolean, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            var allSuccess = true
            for (moment in momentsList) {
                val updatedMoment = moment.copy(isInTrash = isInTrash)
                val success = repository.updateMoment(updatedMoment)
                if (!success) allSuccess = false
            }
            onComplete(allSuccess)
        }
    }

    /**
     * Elimina definitivamente più ricordi contemporaneamente (selezione multipla) cancellando anche i file locali.
     */
    fun deleteMomentsPermanently(momentsList: List<Moment>, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            var allSuccess = true
            for (moment in momentsList) {
                val success = repository.deleteMoment(moment)
                if (!success) allSuccess = false
            }
            onComplete(allSuccess)
        }
    }

    /**
     * Elimina un momento rimuovendo il record dal DB e cancellando tutti i file locali.
     */
    fun deleteMoment(moment: Moment, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.deleteMoment(moment)
            onComplete(success)
        }
    }
}

/**
 * Factory per istanziare correttamente il ViewModel iniettando il repository.
 */
class MomentViewModelFactory(private val repository: MomentRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MomentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MomentViewModel(repository) as T
        }
        throw IllegalArgumentException("Classe ViewModel non riconosciuta")
    }
}

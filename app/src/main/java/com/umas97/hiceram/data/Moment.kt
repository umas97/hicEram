package com.umas97.hiceram.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Rappresenta un singolo momento/ricordo salvato dall'utente.
 * 
 * @property id Identificatore unico autogenerato.
 * @property imagePathsString Percorsi dei file delle immagini salvati nello storage interno, separati da virgola.
 * @property description Descrizione testuale inserita dall'utente.
 * @property timestamp Data del ricordo selezionata dall'utente (in millisecondi).
 * @property isFavorite Indica se il ricordo è stato inserito nei preferiti.
 * @property isInTrash Indica se il ricordo è stato spostato nel cestino.
 */
@Entity(tableName = "moments")
data class Moment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val imagePathsString: String,
    val description: String,
    val timestamp: Long,
    val isFavorite: Boolean = false,
    val isInTrash: Boolean = false,
    val locationName: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
) {
    /**
     * Ritorna la lista dei singoli percorsi delle immagini del ricordo.
     */
    val imagePaths: List<String>
        get() = if (imagePathsString.isEmpty()) emptyList() else imagePathsString.split(",")
}

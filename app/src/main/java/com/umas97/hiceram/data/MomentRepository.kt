package com.umas97.hiceram.data

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

/**
 * Repository per gestire la sorgente dati dei momenti e la logica del file system locale.
 */
class MomentRepository(
    private val context: Context,
    private val momentDao: MomentDao
) {
    /**
     * Flusso di dati osservabile con la lista di tutti i momenti salvati.
     */
    val allMoments: Flow<List<Moment>> = momentDao.getAllMoments()

    /**
     * Copia le immagini selezionate dall'URI nello storage interno privato e salva le info nel DB.
     * 
     * @param imageUris Lista degli URI delle immagini selezionate (es. tramite PhotoPicker).
     * @param description Testo descrittivo del momento.
     * @param timestamp Data del ricordo selezionata dall'utente (in millisecondi).
     * @return true se il salvataggio è avvenuto con successo, false altrimenti.
     */
    suspend fun saveMoment(imageUris: List<Uri>, description: String, timestamp: Long): Boolean = withContext(Dispatchers.IO) {
        try {
            val savedPaths = mutableListOf<String>()
            
            for (uri in imageUris) {
                // Genera un nome file univoco e crea il file nello storage interno privato per ciascun URI
                val fileName = "moment_${UUID.randomUUID()}.jpg"
                val internalFile = File(context.filesDir, fileName)

                // Copia il flusso di byte dall'URI al file locale
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    FileOutputStream(internalFile).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                } ?: return@withContext false
                
                savedPaths.add(internalFile.absolutePath)
            }

            // Unisce i percorsi salvati in una stringa separata da virgola
            val imagePathsString = savedPaths.joinToString(",")

            // Crea e inserisce l'entità nel Database Room con il timestamp personalizzato
            val moment = Moment(
                imagePathsString = imagePathsString,
                description = description,
                timestamp = timestamp,
                isFavorite = false,
                isInTrash = false
            )
            momentDao.insertMoment(moment)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Aggiorna lo stato di un momento esistente nel DB (es. preferiti, cestino, ecc.).
     */
    suspend fun updateMoment(moment: Moment): Boolean = withContext(Dispatchers.IO) {
        try {
            momentDao.updateMoment(moment)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Elimina il momento dal Database Room e cancella tutte le immagini associate dal file system.
     * 
     * @param moment L'oggetto Moment da eliminare.
     * @return true se l'eliminazione completa è riuscita, false altrimenti.
     */
    suspend fun deleteMoment(moment: Moment): Boolean = withContext(Dispatchers.IO) {
        try {
            // Cancella tutti i file fisici salvati nello storage interno per questo ricordo
            for (path in moment.imagePaths) {
                val file = File(path)
                if (file.exists()) {
                    file.delete()
                }
            }
            // Elimina la riga dal Database
            momentDao.deleteMoment(moment)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

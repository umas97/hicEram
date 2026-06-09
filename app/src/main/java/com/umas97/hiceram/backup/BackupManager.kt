package com.umas97.hiceram.backup

import android.content.Context
import android.net.Uri
import com.umas97.hiceram.data.MomentDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object BackupManager {

    /**
     * Crea un file ZIP contenente il database e tutte le immagini salvate.
     * Il file ZIP viene scritto nell'URI di output fornito (tramite SAF).
     */
    suspend fun createBackup(context: Context, outputUri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val dbPath = context.getDatabasePath("moment_database")
            val walPath = File(dbPath.parent, "moment_database-wal")
            val shmPath = File(dbPath.parent, "moment_database-shm")

            val filesToBackup = mutableListOf<File>()
            
            if (dbPath.exists()) filesToBackup.add(dbPath)
            if (walPath.exists()) filesToBackup.add(walPath)
            if (shmPath.exists()) filesToBackup.add(shmPath)

            // Aggiunge tutte le immagini salvate nella filesDir
            val images = context.filesDir.listFiles()?.filter { it.name.startsWith("moment_") && it.extension == "jpg" }
            if (images != null) {
                filesToBackup.addAll(images)
            }

            context.contentResolver.openOutputStream(outputUri)?.use { outputStream ->
                ZipOutputStream(outputStream).use { zipOut ->
                    for (file in filesToBackup) {
                        FileInputStream(file).use { fis ->
                            val zipEntry = ZipEntry(file.name)
                            zipOut.putNextEntry(zipEntry)
                            fis.copyTo(zipOut)
                            zipOut.closeEntry()
                        }
                    }
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Ripristina un file ZIP precedentemente creato.
     * Sovrascrive il database e le immagini correnti.
     */
    suspend fun restoreBackup(context: Context, inputUri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            // Chiude il database corrente per evitare problemi di concorrenza
            MomentDatabase.getDatabase(context).close()

            val dbDir = context.getDatabasePath("moment_database").parentFile
            val filesDir = context.filesDir

            context.contentResolver.openInputStream(inputUri)?.use { inputStream ->
                ZipInputStream(inputStream).use { zipIn ->
                    var entry: ZipEntry? = zipIn.nextEntry
                    while (entry != null) {
                        val fileName = entry.name
                        // Smista il file nella cartella corretta (databases o filesDir)
                        val targetFile = if (fileName.startsWith("moment_database")) {
                            File(dbDir, fileName)
                        } else {
                            File(filesDir, fileName)
                        }

                        FileOutputStream(targetFile).use { fos ->
                            zipIn.copyTo(fos)
                        }
                        zipIn.closeEntry()
                        entry = zipIn.nextEntry
                    }
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

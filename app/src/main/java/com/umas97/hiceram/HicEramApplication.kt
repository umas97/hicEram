package com.umas97.hiceram

import android.app.Application
import com.umas97.hiceram.data.MomentDatabase
import com.umas97.hiceram.data.MomentRepository

/**
 * Classe Application personalizzata per inizializzare in modo pigro (lazy) 
 * il Database Room ed il Repository.
 */
class HicEramApplication : Application() {

    // Inizializzazione lazy per evitare il blocco del thread principale all'avvio dell'app
    private val database by lazy { MomentDatabase.getDatabase(this) }
    
    val repository by lazy { MomentRepository(this, database.momentDao()) }
}

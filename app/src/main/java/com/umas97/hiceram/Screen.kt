package com.umas97.hiceram

import com.umas97.hiceram.data.Moment

/**
 * Rappresenta le schermate dell'applicazione per la navigazione state-based.
 */
sealed class Screen {
    /**
     * Schermata principale contenente la lista cronologica dei ricordi.
     */
    object Feed : Screen()

    /**
     * Schermata per la creazione e il salvataggio di un nuovo ricordo.
     */
    object Add : Screen()

    /**
     * Schermata di visualizzazione a schermo intero del singolo ricordo con opzione di eliminazione.
     * 
     * @property moment Il ricordo da mostrare in dettaglio.
     */
    data class Detail(val moment: Moment) : Screen()

    /**
     * Schermata delle impostazioni.
     */
    object Settings : Screen()
}

package com.umas97.hiceram.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) per gestire le operazioni sul database della tabella 'moments'.
 */
@Dao
interface MomentDao {
    /**
     * Recupera tutti i momenti ordinati cronologicamente (dal più recente al più vecchio).
     */
    @Query("SELECT * FROM moments ORDER BY timestamp DESC")
    fun getAllMoments(): Flow<List<Moment>>

    /**
     * Inserisce un nuovo momento nel database.
     * @return L'ID autogenerato del momento inserito.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoment(moment: Moment): Long

    /**
     * Aggiorna un momento esistente nel database (es. preferito o spostato nel cestino).
     */
    @Update
    suspend fun updateMoment(moment: Moment)

    /**
     * Elimina definitivamente un momento dal database.
     */
    @Delete
    suspend fun deleteMoment(moment: Moment)
}

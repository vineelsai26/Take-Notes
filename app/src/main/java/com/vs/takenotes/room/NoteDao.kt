package com.vs.takenotes.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    fun getNotes(query: String): Flow<List<Note>> =
        getNotesSortedByDateCreated(query)

    @Query("SELECT * FROM note_table WHERE title or description LIKE '%' || :searchQuery || '%' ORDER BY created DESC")
    fun getNotesSortedByDateCreated(searchQuery: String): Flow<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note)

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)
}
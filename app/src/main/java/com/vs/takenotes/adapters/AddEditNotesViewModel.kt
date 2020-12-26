package com.vs.takenotes.adapters

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vs.takenotes.room.Note
import com.vs.takenotes.room.NoteDao
import kotlinx.coroutines.launch

class AddEditNotesViewModel @ViewModelInject constructor(
    private val noteDao: NoteDao,
) : ViewModel() {

    fun insert(note: Note) = viewModelScope.launch {
        noteDao.insert(note)
    }

    fun update(noteTitle: String, noteDescription: String, id: Int, created: Long) =
        viewModelScope.launch {
            noteDao.update(
                Note(
                    title = noteTitle,
                    description = noteDescription,
                    id = id,
                    created = created
                )
            )
        }
}
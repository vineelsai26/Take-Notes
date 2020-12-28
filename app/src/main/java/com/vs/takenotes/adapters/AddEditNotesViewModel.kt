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

    fun update(note: Note) = viewModelScope.launch {
        noteDao.update(note)
    }
}
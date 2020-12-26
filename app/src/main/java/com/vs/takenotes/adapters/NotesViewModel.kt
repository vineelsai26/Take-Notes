package com.vs.takenotes.adapters

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.vs.takenotes.room.Note
import com.vs.takenotes.room.NoteDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class NotesViewModel @ViewModelInject constructor(
    private val noteDao: NoteDao,
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    private val notesFlow = combine(
        searchQuery
    ) { query ->
        query
    }.flatMapLatest { (query) ->
        noteDao.getNotes(query)
    }

    val notes = notesFlow.asLiveData()

    fun deleteNote(note: Note) = viewModelScope.launch {
        noteDao.delete(note)
    }

    fun undoNoteDeletion(note: Note) = viewModelScope.launch {
        noteDao.insert(note)
    }
}
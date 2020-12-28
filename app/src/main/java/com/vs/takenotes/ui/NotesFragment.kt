package com.vs.takenotes.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.vs.takenotes.R
import com.vs.takenotes.adapters.NotesAdapter
import com.vs.takenotes.adapters.NotesViewModel
import com.vs.takenotes.databinding.FragmentNotesBinding
import com.vs.takenotes.room.Note
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotesFragment : Fragment(R.layout.fragment_notes), NotesAdapter.OnItemClickListener {

    private val viewModel: NotesViewModel by viewModels()
    private lateinit var activityForResult: ActivityResultLauncher<Intent>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentNotesBinding.bind(view)

        val noteAdapter = NotesAdapter(this)

        binding.apply {
            notesView.apply {
                adapter = noteAdapter
                layoutManager = GridLayoutManager(requireContext(), 2)
                setHasFixedSize(true)
            }

            notesSearch.addTextChangedListener {
                viewModel.searchQuery.value = notesSearch.text.toString()
            }
        }

        viewModel.notes.observe(viewLifecycleOwner) {
            noteAdapter.submitList(it)
        }

        activityForResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.getStringExtra("result")
            }
        }
    }

    override fun onItemClick(note: Note) {
        val intent = Intent(context, AddEditNotesActivity::class.java)
        intent.putExtra("title", note.title)
        intent.putExtra("description", note.description)
        intent.putExtra("id", note.id)
        intent.putExtra("created", note.created)
        intent.putExtra("color", note.color)
        activityForResult.launch(intent)
    }

    override fun onItemLongClick(note: Note) {
        Snackbar.make(requireView(), "Delete Note", Snackbar.LENGTH_LONG)
            .setAction("Delete") {
                viewModel.deleteNote(note)
                Snackbar.make(requireView(), "Note Deleted", Snackbar.LENGTH_LONG)
                    .setAction("UNDO") {
                        viewModel.undoNoteDeletion(note)
                    }.show()
            }.show()
    }
}
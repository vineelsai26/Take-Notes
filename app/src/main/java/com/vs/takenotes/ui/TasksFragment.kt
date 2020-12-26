package com.vs.takenotes.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.vs.takenotes.R
import com.vs.takenotes.adapters.TasksAdapter
import com.vs.takenotes.adapters.TasksViewModel
import com.vs.takenotes.databinding.FragmentTasksBinding
import com.vs.takenotes.room.Task
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragment_tasks), TasksAdapter.OnItemClickListener {

    private val viewModel: TasksViewModel by viewModels()
    private lateinit var activityForResult: ActivityResultLauncher<Intent>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentTasksBinding.bind(view)

        val taskAdapter = TasksAdapter(this)

        binding.apply {
            tasksView.apply {
                adapter = taskAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            tasksSearch.addTextChangedListener {
                viewModel.searchQuery.value = tasksSearch.text.toString()
            }

            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val task = taskAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.deleteTask(task)
                    Snackbar.make(requireView(), "Task deleted", Snackbar.LENGTH_LONG)
                        .setAction("UNDO") {
                            viewModel.undoTaskDeletion(task)
                        }.show()
                }
            }).attachToRecyclerView(tasksView)
        }

        viewModel.tasks.observe(viewLifecycleOwner) {
            taskAdapter.submitList(it)
        }

        activityForResult = registerForActivityResult(
            StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.getStringExtra("result")
            }
        }

    }

    override fun onItemClick(task: Task) {
        val intent = Intent(context, AddEditTasksActivity::class.java)
        intent.putExtra("description", task.name)
        intent.putExtra("id", task.id)
        intent.putExtra("created", task.created)
        intent.putExtra("status", task.completed)
        activityForResult.launch(intent)
    }

    override fun onItemLongClick(task: Task) {
        Snackbar.make(requireView(), "Delete Task", Snackbar.LENGTH_LONG)
            .setAction("Delete") {
                viewModel.deleteTask(task)
                Snackbar.make(requireView(), "Task Deleted", Snackbar.LENGTH_LONG)
                    .setAction("UNDO") {
                        viewModel.undoTaskDeletion(task)
                    }.show()
            }.show()
    }

    override fun onCheckBoxClick(task: Task, isChecked: Boolean) {
        viewModel.onTaskCheckedChanged(task, isChecked)
    }
}
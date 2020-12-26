package com.vs.takenotes.adapters

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vs.takenotes.room.Task
import com.vs.takenotes.room.TaskDao
import kotlinx.coroutines.launch

class AddEditTasksViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
) : ViewModel() {

    fun insert(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
    }

    fun update(taskName: String, intExtra: Int, created: Long, completed: Boolean) =
        viewModelScope.launch {
            taskDao.update(
                Task(
                    name = taskName,
                    id = intExtra,
                    created = created,
                    completed = completed
                )
            )
        }
}
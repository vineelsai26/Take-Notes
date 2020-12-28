package com.vs.takenotes.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.vs.takenotes.R
import com.vs.takenotes.adapters.TasksViewModel
import com.vs.takenotes.data.SortOrder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private val viewModel: TasksViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        toolbar.title = ""

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar_title.text = getString(R.string.settings)
        setSupportActionBar(toolbar)

        var sortOrder = false

        CoroutineScope(Dispatchers.Main).launch {
            sortOrder = viewModel.preferencesFlow.first().sortOrder.name == SortOrder.BY_NAME.name
        }

        CoroutineScope(Dispatchers.Main).launch {
            hideCompleted.isChecked = viewModel.preferencesFlow.first().hideCompleted
            if (sortOrder) {
                sortByName.isChecked = true
            } else {
                sortByDate.isChecked = true
            }
        }

        sortByName.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.onSortOrderSelected(SortOrder.BY_NAME)
                sortByDate.isChecked = false
            } else {
                sortByDate.isChecked = true
            }
        }

        sortByDate.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.onSortOrderSelected(SortOrder.BY_DATE)
                sortByName.isChecked = false
            } else {
                sortByName.isChecked = true
            }
        }

        hideCompleted.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                viewModel.onHideCompletedClick(isChecked)
            } else {
                viewModel.onHideCompletedClick(isChecked)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
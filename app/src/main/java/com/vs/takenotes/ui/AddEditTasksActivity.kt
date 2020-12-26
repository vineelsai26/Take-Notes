package com.vs.takenotes.ui

import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doOnTextChanged
import com.vs.takenotes.R
import com.vs.takenotes.adapters.AddEditTasksViewModel
import com.vs.takenotes.room.Task
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_add_edit_tasks.*
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddEditTasksActivity : AppCompatActivity() {

    private var isKeyboardOpened = 0
    private lateinit var description: String
    private val viewModel: AddEditTasksViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_tasks)

        taskName.requestFocus()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = ""
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setSupportActionBar(toolbar)

        description = intent.getStringExtra("description").toString()

        if (description.isNotEmpty() && description != "null") {
            taskName.text = Editable.Factory.getInstance().newEditable(description)
        }

        val sdf = SimpleDateFormat("d MMMM hh:mm aa", Locale.getDefault())
        val currentDateTime: String = sdf.format(Date())
        var countLetters = taskName.text.toString().trim().replace(" ", "", false).length
        var timeStampText = "$currentDateTime | $countLetters characters"
        timeStamp.text = timeStampText
        taskName.doOnTextChanged { _, _, _, _ ->
            countLetters = taskName.text.toString().trim().replace(" ", "", false).length
            timeStampText = "$currentDateTime | $countLetters characters"
            timeStamp.text = timeStampText
        }
    }

    private fun keyboardShown(rootView: View): Boolean {
        val softKeyboardHeight = 100
        val r = Rect()
        rootView.getWindowVisibleDisplayFrame(r)
        val dm = rootView.resources.displayMetrics
        val heightDiff: Int = rootView.bottom - r.bottom
        return heightDiff > softKeyboardHeight * dm.density
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        taskName.viewTreeObserver.addOnGlobalLayoutListener {
            if (keyboardShown(taskName.rootView)) {
                taskName.requestFocus()
            } else {
                if (isKeyboardOpened >= 1) {
                    taskName.clearFocus()
                    isKeyboardOpened = 1
                } else {
                    isKeyboardOpened++
                }
            }
        }

        val inflater = menuInflater
        inflater.inflate(R.menu.tasks, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.done -> {
                if (description != "null" && description != taskName.text.toString()) {
                    intent.putExtra("result", taskName.text.toString())
                    val name = taskName.text.toString()
                    val id = intent.getIntExtra("id", -1)
                    val created = intent.getLongExtra("created", -1)
                    val completed = intent.getBooleanExtra("status", false)
                    viewModel.update(name, id, created, completed)
                    setResult(RESULT_OK, intent)
                    finish()
                } else if (description == taskName.text.toString() && description.isNotBlank()) {
                    finish()
                } else {
                    viewModel.insert(Task(taskName.text.toString()))
                    finish()
                }
                false
            }
            else -> {
                false
            }
        }
    }
}
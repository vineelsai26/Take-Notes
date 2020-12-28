package com.vs.takenotes.ui

import android.graphics.Color
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
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.model.ColorSwatch
import com.vs.takenotes.R
import com.vs.takenotes.adapters.AddEditNotesViewModel
import com.vs.takenotes.room.Note
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_add_edit_notes.*
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddEditNotesActivity : AppCompatActivity() {

    private var isKeyboardOpened = 0
    private lateinit var title: String
    private lateinit var description: String
    private var color = "#FFFFFF"
    private val viewModel: AddEditNotesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_notes)

        noteTitle.requestFocus()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = ""
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        setSupportActionBar(toolbar)

        title = intent.getStringExtra("title").toString()
        description = intent.getStringExtra("description").toString()

        if (description.isNotEmpty() && description != "null") {
            noteTitle.text = Editable.Factory.getInstance().newEditable(title)
            noteDescription.text = Editable.Factory.getInstance().newEditable(description)
            notesLayout.setBackgroundColor(
                Color.parseColor(
                    intent.getStringExtra("color").toString()
                )
            )
        }

        val sdf = SimpleDateFormat("d MMMM hh:mm aa", Locale.getDefault())
        val currentDateTime: String = sdf.format(Date())
        var countLetters = noteTitle.text.toString().trim().replace(" ", "", false).length
        var timeStampText = "$currentDateTime | $countLetters characters"
        timeStamp.text = timeStampText
        noteDescription.doOnTextChanged { _, _, _, _ ->
            countLetters = noteDescription.text.toString().trim().replace(" ", "", false).length
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
        noteTitle.viewTreeObserver.addOnGlobalLayoutListener {
            if (keyboardShown(noteTitle.rootView)) {
                menu.findItem(R.id.customize).isVisible = false
            } else {
                if (isKeyboardOpened >= 1) {
                    noteTitle.clearFocus()
                    noteDescription.clearFocus()
                    menu.findItem(R.id.customize).isVisible = true
                    isKeyboardOpened = 1
                } else {
                    isKeyboardOpened++
                }
            }
        }

        val inflater = menuInflater
        inflater.inflate(R.menu.notes, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.done -> {
                if (description != "null") {
                    val title = noteTitle.text.toString()
                    val description = noteDescription.text.toString()
                    val id = intent.getIntExtra("id", -1)
                    val created = intent.getLongExtra("created", -1)

                    viewModel.update(
                        Note(
                            title = title,
                            description = description,
                            id = id,
                            color = color,
                            created = created
                        )
                    )

                    setResult(RESULT_OK, intent)
                    finish()
                } else {
                    if (color == "null") {
                        color = "#FFFFFF"
                    }
                    viewModel.insert(
                        Note(
                            title = noteTitle.text.toString(),
                            description = noteDescription.text.toString(),
                            color = color
                        )
                    )
                }
                finish()
                false
            }
            R.id.customize -> {
                MaterialColorPickerDialog
                    .Builder(this)
                    .setTitle("Pick Theme")
                    .setColorSwatch(ColorSwatch._300)
                    .setColorShape(ColorShape.SQAURE)
                    .setDefaultColor("#FFFFFF")
                    .setColorListener { colorInt, colorHex ->
                        color = colorHex
                        notesLayout.setBackgroundColor(colorInt)
                    }
                    .show()
                false
            }
            else -> {
                false
            }
        }
    }
}
package com.david.qmul_room_timetable_app

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.time.LocalDate

private const val RESULT_FOLDER_NAME = "results"

class ShowResultsActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var prevButton: ImageButton
    private lateinit var nextButton: ImageButton

    private lateinit var autoCompleteTextViewDay: AutoCompleteTextView
    private lateinit var adapterDay: ArrayAdapter<String>
//    private lateinit var dayList:List<String>
    private lateinit var selectedDay: String

    private val results = mutableListOf<String>()
    private var currentIndex = 0

    companion object {
        private val WEEKDAYS = arrayOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_results)

        webView = findViewById(R.id.webView)
        prevButton = findViewById(R.id.prevButton)
        nextButton = findViewById(R.id.nextButton)

        webView.settings.apply {
            allowContentAccess = true
            allowFileAccess = true
            loadWithOverviewMode = true
            useWideViewPort = true
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
        }

        var day = LocalDate.now().dayOfWeek.toString()
        day = day[0] + day.lowercase().substring(1)
        day = if (WEEKDAYS.contains(day)) day else "Monday"

        loadResults(day)
        showResult()

        grayOutButtons(currentIndex)
//        WebView.setWebContentsDebuggingEnabled(true)

        autoCompleteTextViewDay = findViewById(R.id.autoCompleteTextView6)
        adapterDay = ArrayAdapter(this, R.layout.dropdown_item, WEEKDAYS) // todo: Make days more flexible and custom to user.

        autoCompleteTextViewDay.setText(day) // todo: test with more queries(rooms)
        autoCompleteTextViewDay.setAdapter(adapterDay)
        autoCompleteTextViewDay.setOnItemClickListener { _, _, position, _ ->
            selectedDay = WEEKDAYS[position]
            autoCompleteTextViewDay.setText(selectedDay, false)
            loadResults(selectedDay)
            showResult()
        }
    }

    private fun loadResults(day: String) {
        val folder = File(filesDir, RESULT_FOLDER_NAME)
        val resultsFiles = folder.listFiles { _, name -> name.endsWith("${day.uppercase()}.html")}

        results.clear()
        if (resultsFiles != null && resultsFiles.isNotEmpty())
            resultsFiles.forEach { results.add(it.readText()) }
    }

    private fun showResult() {
        val result = results[currentIndex]
        val basePath = "file://${filesDir.absolutePath}/$RESULT_FOLDER_NAME/"

        webView.loadDataWithBaseURL(basePath, result, "text/html", "UTF=8", null)
    }

    fun showPreviousResult(view: View) {
        if (currentIndex > 0) {
            currentIndex--
            showResult()
        }

        grayOutButtons(currentIndex)
    }

    fun showNextResult(view: View) {
        if (currentIndex < results.size - 1) {
            currentIndex++
            showResult()
        }

        grayOutButtons(currentIndex)
    }

    private fun grayOutButtons(index: Int) {
        prevButton.isEnabled = index != 0
        nextButton.isEnabled = index != results.size - 1
    }

}

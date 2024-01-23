package com.david.qmul_room_timetable_app

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import java.io.File

private const val RESULT_FOLDER_NAME = "results"

class ShowResultsActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var prevButton: ImageButton
    private lateinit var nextButton: ImageButton

    private val results = mutableListOf<String>()
    private var currentIndex = 0

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

        loadResults()
        showResult()

        prevButton.isEnabled = false
//        WebView.setWebContentsDebuggingEnabled(true)
    }

    private fun loadResults() {
        val folder = File(filesDir, RESULT_FOLDER_NAME)
        val resultsFiles = folder.listFiles() {_, name -> name.endsWith(".html")}

        resultsFiles?.forEach { results.add(it.readText()) }
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
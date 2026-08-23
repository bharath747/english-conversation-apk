package com.littleenglishfriends.app

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var tts: TextToSpeech
    private var starCount = 0
    private val lessons = mapOf(
        R.id.greetings to listOf("Hello! What is your name?", "My name is Sam.", "Nice to meet you!"),
        R.id.family to listOf("This is my mother.", "I love my family."),
        R.id.school to listOf("Good morning, teacher!", "May I sit here?"),
        R.id.food to listOf("I would like an apple, please.", "Thank you very much!")
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_main); tts = TextToSpeech(this, this)
        lessons.forEach { (id, lines) -> findViewById<Button>(id).setOnClickListener { showLesson(lines) } }
        findViewById<Button>(R.id.practice).setOnClickListener { showLesson(listOf("Hello! My name is ___.", "How are you today?", "I am happy to meet you!")) }
    }
    private fun showLesson(lines: List<String>) { AlertDialog.Builder(this).setTitle("Let's practice! 🌟").setItems(lines.toTypedArray()) { _, which -> speak(lines[which]); addStar() }.setPositiveButton("Close", null).show() }
    private fun speak(text: String) { if (::tts.isInitialized) tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "lesson") }
    private fun addStar() { starCount++; findViewById<TextView>(R.id.stars).text = "⭐ $starCount" }
    override fun onInit(status: Int) { if (status == TextToSpeech.SUCCESS) { tts.language = Locale.US } }
    override fun onDestroy() { tts.stop(); tts.shutdown(); super.onDestroy() }
}

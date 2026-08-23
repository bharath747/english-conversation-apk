package com.littleenglishfriends.app

import android.graphics.Color
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var tts: TextToSpeech
    private var stars = 0
    private var lessonIndex = 0
    private var answer = ""
    private lateinit var lessons: List<Lesson>
    private lateinit var game: LinkedHashMap<String, String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Keep app content clear of Android 15 status and navigation bars.
        val scroll = findViewById<ScrollView>(R.id.rootScroll)
        val density = resources.displayMetrics.density
        val side = (14 * density).toInt()
        ViewCompat.setOnApplyWindowInsetsListener(scroll) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(side, bars.top + side, side, bars.bottom + side)
            insets
        }
        ViewCompat.requestApplyInsets(scroll)

        lessons = OfflineData.lessons(this)
        game = OfflineData.games(this)
        tts = TextToSpeech(this, this)

        findViewById<Button>(R.id.learn).setOnClickListener { showPanel(R.id.learnPanel) }
        findViewById<Button>(R.id.games).setOnClickListener { showPanel(R.id.gamePanel); newGame(); scrollToContent() }
        findViewById<Button>(R.id.talkTab).setOnClickListener { showPanel(R.id.talkPanel); scrollToContent() }
        findViewById<Button>(R.id.nextWord).setOnClickListener { lessonIndex = (lessonIndex + 1) % lessons.size; bindLesson() }
        findViewById<Button>(R.id.prevWord).setOnClickListener { lessonIndex = (lessonIndex - 1 + lessons.size) % lessons.size; bindLesson() }
        findViewById<Button>(R.id.playEnglish).setOnClickListener { speak(lessons[lessonIndex].sentence, Locale.US) }
        findViewById<Button>(R.id.playTelugu).setOnClickListener { speak(lessons[lessonIndex].teluguSentence, Locale("te", "IN")) }
        findViewById<Button>(R.id.practice).setOnClickListener {
            speak("Hello! My name is my friend. How are you today? I am happy to meet you!", Locale.US)
            addStar()
        }
        listOf(R.id.choice1, R.id.choice2, R.id.choice3, R.id.choice4).forEach { id ->
            findViewById<Button>(id).setOnClickListener { checkAnswer((it as Button).text.toString()) }
        }
        findViewById<Button>(R.id.nextGame).setOnClickListener { newGame() }
        bindLesson()
    }

    private fun scrollToContent() {
        findViewById<ScrollView>(R.id.rootScroll).post {
            findViewById<ScrollView>(R.id.rootScroll).smoothScrollTo(0, 0)
        }
    }

    private fun showPanel(id: Int) {
        findViewById<View>(R.id.learnPanel).visibility = if (id == R.id.learnPanel) View.VISIBLE else View.GONE
        findViewById<View>(R.id.gamePanel).visibility = if (id == R.id.gamePanel) View.VISIBLE else View.GONE
        findViewById<View>(R.id.talkPanel).visibility = if (id == R.id.talkPanel) View.VISIBLE else View.GONE
    }

    private fun bindLesson() {
        if (lessons.isEmpty()) return
        val l = lessons[lessonIndex]
        findViewById<TextView>(R.id.wordEnglish).text = l.word
        findViewById<TextView>(R.id.wordTelugu).text = l.telugu
        findViewById<TextView>(R.id.wordSentence).text = l.sentence
        findViewById<TextView>(R.id.wordTeluguSentence).text = l.teluguSentence
        findViewById<TextView>(R.id.wordEmoji).text = l.emoji
        findViewById<TextView>(R.id.lessonProgress).text = "Lesson ${lessonIndex + 1} of ${lessons.size}"
        findViewById<ProgressBar>(R.id.lessonBar).max = lessons.size
        findViewById<ProgressBar>(R.id.lessonBar).progress = lessonIndex + 1
        findViewById<TextView>(R.id.compareText).text = "తెలుగు   ${l.telugu}   →   ${l.word}   English"
    }

    private fun newGame() {
        if (game.isEmpty()) return
        answer = game.keys.random()
        findViewById<TextView>(R.id.gameEmoji).text = game[answer]
        findViewById<TextView>(R.id.gameFeedback).text = "Choose the English word."
        val options = (listOf(answer) + game.keys.filter { it != answer }.shuffled().take(3)).shuffled()
        val ids = listOf(R.id.choice1, R.id.choice2, R.id.choice3, R.id.choice4)
        ids.forEachIndexed { index, id -> findViewById<Button>(id).text = options[index] }
    }

    private fun checkAnswer(choice: String) {
        val feedback = findViewById<TextView>(R.id.gameFeedback)
        if (choice == answer) {
            feedback.text = "🎉 Great job! That is $answer."
            feedback.setTextColor(Color.rgb(53, 126, 90))
            addStar()
            speak("Great job! $answer", Locale.US)
        } else {
            feedback.text = "Try again! 😊"
            feedback.setTextColor(Color.rgb(190, 80, 80))
            speak("Try again", Locale.US)
        }
    }

    private fun speak(text: String, locale: Locale) {
        if (::tts.isInitialized) {
            tts.language = locale
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "voice")
        }
    }

    private fun addStar() {
        stars++
        findViewById<TextView>(R.id.stars).text = stars.toString()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) tts.language = Locale.US
    }

    override fun onDestroy() {
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }
}

package com.littleenglishfriends.app

import android.graphics.Color
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var tts: TextToSpeech
    private var stars = 0
    private var lessonIndex = 0
    private var answer = ""
    private val lessons = listOf(
        arrayOf("happy","సంతోషంగా","I am happy.","నేను సంతోషంగా ఉన్నాను.","😊"),
        arrayOf("water","నీరు","I want water.","నాకు నీళ్లు కావాలి.","💧"),
        arrayOf("apple","ఆపిల్","I like apples.","నాకు ఆపిల్స్ ఇష్టం.","🍎"),
        arrayOf("ball","బంతి","This is my ball.","ఇది నా బంతి.","⚽"),
        arrayOf("book","పుస్తకం","I am reading a book.","నేను పుస్తకం చదువుతున్నాను.","📖"),
        arrayOf("sleep","నిద్ర","I want to sleep.","నాకు నిద్ర కావాలి.","😴"),
        arrayOf("home","ఇల్లు","I am at home.","నేను ఇంట్లో ఉన్నాను.","🏠"),
        arrayOf("play","ఆడటం","I want to play.","నాకు ఆడాలని ఉంది.","🛝"),
        arrayOf("mother","అమ్మ","This is my mother.","ఇది నా అమ్మ.","👩"),
        arrayOf("father","నాన్న","This is my father.","ఇది నా నాన్న.","👨"),
        arrayOf("school","పాఠశాల","I go to school.","నేను పాఠశాలకు వెళ్తాను.","🏫"),
        arrayOf("friend","స్నేహితుడు","He is my friend.","అతను నా friend.","🧒"),
        arrayOf("milk","పాలు","I drink milk.","నేను పాలు తాగుతాను.","🥛"),
        arrayOf("food","ఆహారం","The food is tasty.","ఆహారం రుచిగా ఉంది.","🍚"),
        arrayOf("sun","సూర్యుడు","The sun is bright.","సూర్యుడు ప్రకాశంగా ఉన్నాడు.","☀️"),
        arrayOf("moon","చంద్రుడు","I can see the moon.","నేను చంద్రుడిని చూడగలను.","🌙"),
        arrayOf("dog","కుక్క","The dog is running.","కుక్క పరుగెడుతోంది.","🐶"),
        arrayOf("cat","పిల్లి","The cat is sleeping.","పిల్లి నిద్రపోతోంది.","🐱"),
        arrayOf("banana","అరటిపండు","I like bananas.","నాకు అరటిపండ్లు ఇష్టం.","🍌"),
        arrayOf("orange","నారింజ","I eat an orange.","నేను నారింజ పండు తింటాను.","🍊"),
        arrayOf("mango","మామిడి పండు","I like mangoes.","నాకు మామిడి పండ్లు ఇష్టం.","🥭"),
        arrayOf("car","కారు","This is a car.","ఇది కారు.","🚗"),
        arrayOf("bus","బస్సు","I go by bus.","నేను బస్సులో వెళ్తాను.","🚌"),
        arrayOf("pencil","పెన్సిల్","I have a pencil.","నా దగ్గర పెన్సిల్ ఉంది.","✏️")
    )
    private val game = linkedMapOf("apple" to "🍎","banana" to "🍌","orange" to "🍊","mango" to "🥭","water" to "💧","dog" to "🐶","cat" to "🐱","cow" to "🐄","lion" to "🦁","tiger" to "🐯","elephant" to "🐘","car" to "🚗","bus" to "🚌","bike" to "🚲","train" to "🚂","book" to "📖","bag" to "🎒","sun" to "☀️","moon" to "🌙","happy" to "😊","red" to "🔴","blue" to "🔵","green" to "🟢")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tts = TextToSpeech(this, this)
        findViewById<Button>(R.id.learn).setOnClickListener { showPanel(R.id.learnPanel) }
        findViewById<Button>(R.id.games).setOnClickListener { showPanel(R.id.gamePanel); newGame() }
        findViewById<Button>(R.id.talkTab).setOnClickListener { showPanel(R.id.talkPanel) }
        findViewById<Button>(R.id.nextWord).setOnClickListener { lessonIndex = (lessonIndex + 1) % lessons.size; bindLesson() }
        findViewById<Button>(R.id.prevWord).setOnClickListener { lessonIndex = (lessonIndex - 1 + lessons.size) % lessons.size; bindLesson() }
        findViewById<Button>(R.id.playEnglish).setOnClickListener { speak(lessons[lessonIndex][2], Locale.US) }
        findViewById<Button>(R.id.playTelugu).setOnClickListener { speak(lessons[lessonIndex][3], Locale("te", "IN")) }
        findViewById<Button>(R.id.practice).setOnClickListener { speak("Hello! My name is my friend. How are you today? I am happy to meet you!", Locale.US); addStar() }
        listOf(R.id.choice1, R.id.choice2, R.id.choice3, R.id.choice4).forEach { id -> findViewById<Button>(id).setOnClickListener { checkAnswer((it as Button).text.toString()) } }
        findViewById<Button>(R.id.nextGame).setOnClickListener { newGame() }
        bindLesson()
    }

    private fun showPanel(id: Int) {
        findViewById<View>(R.id.learnPanel).visibility = if (id == R.id.learnPanel) View.VISIBLE else View.GONE
        findViewById<View>(R.id.gamePanel).visibility = if (id == R.id.gamePanel) View.VISIBLE else View.GONE
        findViewById<View>(R.id.talkPanel).visibility = if (id == R.id.talkPanel) View.VISIBLE else View.GONE
    }

    private fun bindLesson() {
        val l = lessons[lessonIndex]
        findViewById<TextView>(R.id.wordEnglish).text = l[0]
        findViewById<TextView>(R.id.wordTelugu).text = l[1]
        findViewById<TextView>(R.id.wordSentence).text = l[2]
        findViewById<TextView>(R.id.wordTeluguSentence).text = l[3]
        findViewById<TextView>(R.id.wordEmoji).text = l[4]
        findViewById<TextView>(R.id.lessonProgress).text = "Lesson ${lessonIndex + 1} of ${lessons.size}"
        findViewById<ProgressBar>(R.id.lessonBar).max = lessons.size
        findViewById<ProgressBar>(R.id.lessonBar).progress = lessonIndex + 1
        findViewById<TextView>(R.id.compareText).text = "తెలుగు   ${l[1]}   →   ${l[0]}   English"
    }

    private fun newGame() {
        answer = game.keys.random()
        findViewById<TextView>(R.id.gameEmoji).text = game[answer]
        findViewById<TextView>(R.id.gameFeedback).text = "Choose the English word."
        val options = (listOf(answer) + game.keys.filter { it != answer }.shuffled().take(3)).shuffled()
        val ids = listOf(R.id.choice1, R.id.choice2, R.id.choice3, R.id.choice4)
        ids.forEachIndexed { index, id -> findViewById<Button>(id).text = options[index] }
    }

    private fun checkAnswer(choice: String) {
        val feedback = findViewById<TextView>(R.id.gameFeedback)
        if (choice == answer) { feedback.text = "🎉 Great job! That is $answer."; feedback.setTextColor(Color.rgb(53, 126, 90)); addStar(); speak("Great job! $answer", Locale.US) }
        else { feedback.text = "Try again! 😊"; feedback.setTextColor(Color.rgb(190, 80, 80)); speak("Try again", Locale.US) }
    }

    private fun speak(text: String, locale: Locale) { if (::tts.isInitialized) { tts.language = locale; tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "voice") } }
    private fun addStar() { stars++; findViewById<TextView>(R.id.stars).text = stars.toString() }
    override fun onInit(status: Int) { if (status == TextToSpeech.SUCCESS) tts.language = Locale.US }
    override fun onDestroy() { tts.stop(); tts.shutdown(); super.onDestroy() }
}
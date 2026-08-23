package com.littleenglishfriends.app

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale
import kotlin.random.Random

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var tts: TextToSpeech
    private var starCount = 0
    private var lessonIndex = 0
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
        arrayOf("friend","స్నేహితుడు","He is my friend.","అతను నా స్నేహితుడు.","🧒"),
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
    private val game = linkedMapOf("apple" to "🍎","banana" to "🍌","orange" to "🍊","mango" to "🥭","water" to "💧","dog" to "🐶","cat" to "🐱","cow" to "🐄","lion" to "🦁","tiger" to "🐯","elephant" to "🐘","car" to "🚗","bus" to "🚌","bike" to "🚲","train" to "🚂","book" to "📖","bag" to "🎒","sun" to "☀️","moon" to "🌙","happy" to "😊","red" to "🔴","blue" to "🔵","green" to "🟢","one" to "1️⃣","two" to "2️⃣")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tts = TextToSpeech(this, this)
        findViewById<Button>(R.id.learn).setOnClickListener { showLearn() }
        findViewById<Button>(R.id.games).setOnClickListener { showGame() }
        findViewById<Button>(R.id.practice).setOnClickListener { showPractice() }
    }
    private fun showLearn() {
        val l = lessons[lessonIndex % lessons.size]
        AlertDialog.Builder(this).setTitle("${l[4]} Learn • ${lessonIndex + 1}/${lessons.size}")
            .setMessage("English word: ${l[0]}\nTelugu: ${l[1]}\n\nEnglish: ${l[2]}\nTelugu: ${l[3]}")
            .setNegativeButton("🔊 English") { _, _ -> speak(l[2], Locale.US) }
            .setNeutralButton("🔊 Telugu") { _, _ -> speak(l[3], Locale("te","IN")) }
            .setPositiveButton("Next ⭐") { _, _ -> lessonIndex++; addStar(); showLearn() }.show()
    }
    private fun showGame() {
        val answer = game.keys.random()
        val choices = (listOf(answer) + game.keys.filter { it != answer }.shuffled().take(3)).shuffled()
        AlertDialog.Builder(this).setTitle("${game[answer]} What is this?")
            .setItems(choices.toTypedArray()) { _, which ->
                if (choices[which] == answer) { addStar(); speak("Great job! $answer", Locale.US); showGame() }
                else { speak("Try again", Locale.US); showGame() }
            }.setNegativeButton("Close", null).show()
    }
    private fun showPractice() { AlertDialog.Builder(this).setTitle("🎤 Practice").setItems(arrayOf("Hello! My name is ___.","How are you today?","I am happy to meet you!","Can we play together?","Thank you very much!")) { _, i -> speak(arrayOf("Hello! My name is ___.","How are you today?","I am happy to meet you!","Can we play together?","Thank you very much!")[i], Locale.US); addStar() }.show() }
    private fun speak(text: String, locale: Locale) { if (::tts.isInitialized) { tts.language = locale; tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "lesson") } }
    private fun addStar() { starCount++; findViewById<TextView>(R.id.stars).text = "⭐ $starCount" }
    override fun onInit(status: Int) { if (status == TextToSpeech.SUCCESS) tts.language = Locale.US }
    override fun onDestroy() { tts.stop(); tts.shutdown(); super.onDestroy() }
}

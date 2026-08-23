package com.littleenglishfriends.app

import android.content.Context

data class Lesson(
    val word: String,
    val telugu: String,
    val sentence: String,
    val teluguSentence: String,
    val emoji: String
)

object OfflineData {
    fun lessons(context: Context): List<Lesson> = try {
        context.assets.open("lessons.tsv").bufferedReader(Charsets.UTF_8).useLines { lines ->
            lines.filter { it.isNotBlank() }.map { line ->
                val p = line.split('\t')
                Lesson(p[0], p[1], p[2], p[3], p[4])
            }.toList()
        }
    } catch (_: Exception) {
        listOf(Lesson("happy", "సంతోషంగా", "I am happy.", "నేను సంతోషంగా ఉన్నాను.", "😊"))
    }

    fun games(context: Context): LinkedHashMap<String, String> = try {
        val result = linkedMapOf<String, String>()
        context.assets.open("games.tsv").bufferedReader(Charsets.UTF_8).useLines { lines ->
            lines.filter { it.isNotBlank() }.forEach { line ->
                val p = line.split('\t', limit = 2)
                if (p.size == 2) result[p[0]] = p[1]
            }
        }
        result
    } catch (_: Exception) {
        linkedMapOf("apple" to "🍎", "dog" to "🐶", "book" to "📖", "happy" to "😊")
    }
}

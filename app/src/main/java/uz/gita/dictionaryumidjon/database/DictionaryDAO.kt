package uz.gita.dictionaryumidjon.database

import android.database.Cursor

interface DictionaryDAO {
    fun getAll(isEnglish: Boolean): Cursor
    fun search(word: String, isEnglish: Boolean): Cursor
    fun addFavorite(id: Int)
    fun removeFavorite(id: Int)
    fun getAllFavorites(): Cursor
}
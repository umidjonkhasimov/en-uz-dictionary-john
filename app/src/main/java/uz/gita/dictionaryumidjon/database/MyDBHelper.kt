package uz.gita.dictionaryumidjon.database

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor

class MyDBHelper private constructor(private val context: Context) : DBHelper(context, NAME, VERSION), DictionaryDAO {

    companion object {
        private const val NAME = "dictionary_uz.db"
        private const val VERSION = 1

        @SuppressLint("StaticFieldLeak")
        private var instance: MyDBHelper? = null

        fun initialize(context: Context) {
            if (instance == null) {
                instance = MyDBHelper(context)
            }
        }

        fun getInstance(): MyDBHelper {
            return instance!!
        }

    }

    override fun getAll(isEnglish: Boolean): Cursor {
        return if (isEnglish) {
            database().rawQuery("SELECT * FROM dictionary ORDER BY english GLOB '[A-Za-z]*' DESC, Upper(english)", null)
        } else {
            database().rawQuery("SELECT * FROM dictionary ORDER BY uzbek GLOB '[A-Za-z]*' DESC, Upper(uzbek)", null)
        }
    }

    override fun search(word: String, isEnglish: Boolean): Cursor {
        return if (isEnglish)
            database().rawQuery("""SELECT * FROM dictionary WHERE english LIKE ?""", arrayOf("$word%"))
        else
            database().rawQuery("""SELECT * FROM dictionary WHERE uzbek LIKE ?""", arrayOf("$word%"))
    }

    override fun addFavorite(id: Int) {
        database().execSQL("UPDATE dictionary SET favourite=1 WHERE id = $id")
    }

    override fun removeFavorite(id: Int) {
        database().execSQL("UPDATE dictionary SET favourite=0 WHERE id = $id")
    }

    override fun getAllFavorites(): Cursor {
        return database().rawQuery("SELECT * FROM dictionary WHERE favourite=1", null)
    }
}
package uz.gita.dictionaryumidjon.ui

import android.app.ActionBar
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import uz.gita.dictionaryumidjon.R
import uz.gita.dictionaryumidjon.adapters.DictionaryAdapter
import uz.gita.dictionaryumidjon.database.MyDBHelper
import uz.gita.dictionaryumidjon.databinding.ScreenFavoritesBinding
import java.util.Locale

class FavoritesScreen : Fragment(R.layout.screen_favorites) {
    private val binding: ScreenFavoritesBinding by viewBinding()
    private val database = MyDBHelper.getInstance()
    private lateinit var adapter: DictionaryAdapter
    private lateinit var mTTS: TextToSpeech

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = DictionaryAdapter(database.getAllFavorites())
        mTTS = TextToSpeech(requireContext(), speechRegistrar())

        adapter.setOnItemClickListener { word, translation, transcript, type ->
            showBottomDialog(word, translation, transcript, type)
        }

        adapter.setOnFavoriteClickListener { id, favState ->
            if (favState == 1) {
                database.removeFavorite(id)
            }
            adapter.updateCursor(database.getAllFavorites(), true)
        }

        binding.apply {
            mRecyclerView.adapter = adapter
            mRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun showBottomDialog(word: String, translation: String, transcript: String, type: String) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottom_sheet_layout)
        dialog.setCancelable(true)

        dialog.findViewById<TextView>(R.id.tv_word_bottom).text = word
        dialog.findViewById<TextView>(R.id.tv_translation_bottom).text = translation
        dialog.findViewById<TextView>(R.id.tv_transcript_bottom).text = transcript
        dialog.findViewById<TextView>(R.id.tv_type_bottom).text = type

        dialog.window?.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.show()
    }

    private fun speechRegistrar(): TextToSpeech.OnInitListener {
        return TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                val res = mTTS.setLanguage(Locale.ENGLISH)
                if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(requireContext(), "This language is not supported", Toast.LENGTH_SHORT).show()
                } else {
                    adapter.setOnSpeechClickListener { word ->
                        mTTS.speak(word, TextToSpeech.QUEUE_FLUSH, null, null)
                    }
                }
            }
        }
    }

}
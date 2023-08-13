package uz.gita.dictionaryumidjon.ui

import android.annotation.SuppressLint
import android.app.ActionBar.LayoutParams
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import uz.gita.dictionaryumidjon.R
import uz.gita.dictionaryumidjon.adapters.DictionaryAdapter
import uz.gita.dictionaryumidjon.database.DictionaryDAO
import uz.gita.dictionaryumidjon.database.MyDBHelper
import uz.gita.dictionaryumidjon.databinding.ScreenAllWordsBinding
import uz.gita.dictionaryumidjon.databinding.ScreenAllWordsCoordinatorBinding
import java.util.Locale

class AllWordsScreen : Fragment(R.layout.screen_all_words_coordinator) {
    private val binding by viewBinding(ScreenAllWordsCoordinatorBinding::bind)
    private val database: DictionaryDAO by lazy { MyDBHelper.getInstance() }
    private lateinit var adapter: DictionaryAdapter
    private var isEnglish = true
    private lateinit var mTTS: TextToSpeech

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = DictionaryAdapter(database.getAll(isEnglish))
        mTTS = TextToSpeech(requireContext(), speechRegistrar())

        adapter.setOnFavoriteClickListener { id, favState ->
            if (favState == 0)
                database.addFavorite(id)
            else
                database.removeFavorite(id)
            adapter.updateCursor(database.getAll(isEnglish), isEnglish)
        }

        adapter.setOnItemClickListener { word, translation, transcript, type ->
            showBottomDialog(word, translation, transcript, type)
        }

        binding.apply {
            mRecyclerView.adapter = adapter
            mRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            btnSwap.setOnClickListener {
                isEnglish = !isEnglish
                if (isEnglish) {
                    tvLeftLang.text = "English"
                    tvRightLang.text = "Uzbek"
                } else {
                    tvLeftLang.text = "Uzbek"
                    tvRightLang.text = "English"
                }
                if (!(etSearch.text.isNullOrEmpty())) {
                    adapter.updateCursor(database.search(etSearch.text.toString(), isEnglish), isEnglish)
                } else
                    adapter.updateCursor(database.getAll(isEnglish), isEnglish)
            }
        }

        binding.apply {
            etSearch.doAfterTextChanged {
                if (!(etSearch.text.isNullOrEmpty())) {
                    adapter.updateCursor(database.search(etSearch.text.toString(), isEnglish), isEnglish)
                } else
                    adapter.updateCursor(database.getAll(isEnglish), isEnglish)
            }
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

        dialog.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.show()
    }

    private fun speechRegistrar(): OnInitListener {
        return OnInitListener { status ->
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
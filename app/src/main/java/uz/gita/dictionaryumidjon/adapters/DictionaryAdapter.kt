package uz.gita.dictionaryumidjon.adapters

import android.annotation.SuppressLint
import android.database.Cursor
import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.gita.dictionaryumidjon.R
import uz.gita.dictionaryumidjon.databinding.ItemWordBinding

class DictionaryAdapter(private var cursor: Cursor) : RecyclerView.Adapter<DictionaryAdapter.ItemViewHolder>() {
    private var isValid = false
    private var isEnglish = true
    private lateinit var onFavoriteClickListener: ((Int, Int) -> Unit)
    private lateinit var onSpeechClickListener: ((String) -> Unit)
    private lateinit var onItemClickListener: (
        (word: String, translation: String, transcript: String, type: String) -> Unit)

    fun setOnItemClickListener(action: ((word: String, translation: String, transcript: String, type: String) -> Unit)) {
        onItemClickListener = action
    }

    fun setOnSpeechClickListener(action: ((String) -> Unit)) {
        onSpeechClickListener = action
    }

    fun setOnFavoriteClickListener(action: (Int, Int) -> Unit) {
        onFavoriteClickListener = action
    }

    @SuppressLint("NotifyDataSetChanged")
    private val dataSetObserver = object : DataSetObserver() {
        override fun onChanged() {
            isValid = true
            notifyDataSetChanged()
            super.onChanged()
        }

        override fun onInvalidated() {
            isValid = false
            notifyDataSetChanged()
            super.onInvalidated()
        }
    }

    init {
        cursor.registerDataSetObserver(dataSetObserver)
        isValid = true
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCursor(newCursor: Cursor, isEnglish: Boolean) {
        isValid = false
        this.isEnglish = isEnglish
        cursor.unregisterDataSetObserver(dataSetObserver)
        cursor.close()


        newCursor.registerDataSetObserver(dataSetObserver)
        cursor = newCursor
        notifyDataSetChanged()
    }

    @SuppressLint("Range")
    inner class ItemViewHolder(private val binding: ItemWordBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                cursor.moveToPosition(adapterPosition)
                if (isEnglish) {
                    val word = cursor.getString(cursor.getColumnIndex("english"))
                    val translation = cursor.getString(cursor.getColumnIndex("uzbek"))
                    val transcript = cursor.getString(cursor.getColumnIndex("transcript"))
                    val type = cursor.getString(cursor.getColumnIndex("type"))

                    onItemClickListener.invoke(word, translation, transcript, type)
                } else {
                    val word = cursor.getString(cursor.getColumnIndex("uzbek"))
                    val translation = cursor.getString(cursor.getColumnIndex("english"))
                    val transcript = cursor.getString(cursor.getColumnIndex("transcript"))
                    val type = cursor.getString(cursor.getColumnIndex("type"))

                    onItemClickListener.invoke(word, translation, transcript, type)
                }
            }

            binding.btnSpeech.setOnClickListener {
                cursor.moveToPosition(adapterPosition)
                if (isEnglish) {
                    val word = cursor.getString(cursor.getColumnIndex("english"))
                    onSpeechClickListener.invoke(word)
                } else {
                    val word = cursor.getString(cursor.getColumnIndex("uzbek"))
                    onSpeechClickListener.invoke(word)
                }
            }

            binding.btnFavorite.setOnClickListener {
                cursor.moveToPosition(adapterPosition)
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val favState = cursor.getInt(cursor.getColumnIndex("favourite"))
                onFavoriteClickListener.invoke(id, favState)
            }
        }

        fun bind() {
            binding.apply {
                cursor.moveToPosition(adapterPosition)
                val favState = cursor.getInt(cursor.getColumnIndex("favourite"))

                if (favState == 1)
                    btnFavorite.setImageResource(R.drawable.ic_bookmark_filled)
                else
                    btnFavorite.setImageResource(R.drawable.ic_bookmark)

                if (isEnglish) {
                    tvWord.text = cursor.getString(cursor.getColumnIndex("english"))
                    tvTranslation.text = cursor.getString(cursor.getColumnIndex("uzbek"))
                    tvType.text = "[" + cursor.getString(cursor.getColumnIndex("type")) + "]"
                } else {
                    tvWord.text = cursor.getString(cursor.getColumnIndex("uzbek"))
                    tvTranslation.text = cursor.getString(cursor.getColumnIndex("english"))
                    tvType.text = "[" + cursor.getString(cursor.getColumnIndex("type")) + "]"
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder = ItemViewHolder(
        ItemWordBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun getItemCount(): Int = cursor.count

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) =
        holder.bind()
}
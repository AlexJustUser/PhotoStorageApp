package com.maveri.figma.main.widget

import android.content.Context
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import com.maveri.figma.databinding.LocationItemBinding
import android.widget.FrameLayout
import com.maveri.figma.databinding.PhotoLocationItemBinding
import com.maveri.figma.model.Location
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LocationItemView
@JvmOverloads constructor(context: Context, deleteView: DeleteView, attr: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attr, defStyleAttr), PhotosAdapter.PhotosView {
    private val binding = LocationItemBinding.inflate(LayoutInflater.from(context), this, true)
    private lateinit var photosAdapter: PhotosAdapter
    private val deleteView: DeleteView? = deleteView
    private val deletingList = mutableListOf<String?>()

    init {
        layoutParams = binding.root.layoutParams
    }

    fun setItem(item: Location, position: Int, locationsAdapter: LocationsAdapter) {
        binding.locationName.setText(item.name)
        binding.locationName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val list = arrayListOf<String>()
                list.add(item.id)
                list.add(binding.locationName.text.toString())
                locationsAdapter.notifyItemChanged(position, list)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        binding.addPhotoButton.setOnClickListener{
            val list = arrayListOf<String>()
            list.add(item.id)
            locationsAdapter.notifyItemChanged(position, list)
            }
        photosAdapter = PhotosAdapter(context, this, item.photos)
        binding.photosGrid.adapter = photosAdapter
        }

    override fun notifyAllElements() {
        photosAdapter.notifyDataSetChanged()
    }

    override fun insertToDelete(photoUrl: String?) {
        deletingList.add(photoUrl)
        deleteView?.deletePhotos(deletingList)
    }

    override fun removeToDelete(photoUrl: String?) {
        deletingList.remove(photoUrl)
        deleteView?.deletePhotos(deletingList)
    }

    override fun checkDeletePressed() : MutableList<String?>{
        return deletingList
    }

    interface DeleteView{
        fun deletePhotos(deleteList: MutableList<String?>)
    }
}


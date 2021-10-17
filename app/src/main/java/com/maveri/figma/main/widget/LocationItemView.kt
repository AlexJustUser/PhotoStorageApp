package com.maveri.figma.main.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import com.maveri.figma.databinding.LocationItemBinding
import android.widget.FrameLayout
import com.maveri.figma.model.Location
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LocationItemView
@JvmOverloads constructor(context: Context, attr: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attr, defStyleAttr) {
    private val binding = LocationItemBinding.inflate(LayoutInflater.from(context), this, true)

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
        val photosAdapter = PhotosAdapter(context, item.photos)
        binding.photosGrid.adapter = photosAdapter
        }
}


package com.maveri.figma.main.widget

import android.content.Context
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

    fun setItem(item: Location, position: (String) -> Unit) {
        binding.locationName.setText(item.name)
        binding.locationName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                position(item.id)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        binding.addPhotoButton.setOnClickListener{

        }
    }

}
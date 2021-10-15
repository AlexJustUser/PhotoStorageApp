package com.maveri.figma.main.widget

import android.content.Context
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

    fun setItem(item: Location) {
        binding.locationName.setText(item.name)
    }

}
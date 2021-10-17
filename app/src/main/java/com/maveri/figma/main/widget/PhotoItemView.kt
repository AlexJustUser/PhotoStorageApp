//package com.maveri.figma.main.widget
//
//import android.content.Context
//import android.util.AttributeSet
//import android.view.LayoutInflater
//import android.view.View
//import android.widget.FrameLayout
//import com.bumptech.glide.Glide
//import com.maveri.figma.databinding.PhotoLocationItemBinding
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class PhotoItemView
//@JvmOverloads constructor(context: Context, attr: AttributeSet? = null, defStyleAttr: Int = 0) :
//    FrameLayout(context, attr, defStyleAttr) {
//    private val binding = PhotoLocationItemBinding.inflate(LayoutInflater.from(context), this, true)
//
//    init {
//        layoutParams = binding.root.layoutParams
//    }
//
//    fun setItem(itemUrl: String, position: Int, photosAdapter: PhotosAdapter) : View {
//        Glide
//            .with(context)
//            .load(itemUrl)
//            .into(binding.photoCard)
//        binding.photoCard.setOnLongClickListener {
//            binding.deletePhotoButton.visibility = View.VISIBLE
//            true
//        }
//        binding.deletePhotoButton.setOnClickListener{
//
//        }
//        return  this
//    }
//
//}
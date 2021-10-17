package com.maveri.figma.main.widget

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import androidx.core.content.ContextCompat.startActivity
import com.maveri.figma.R
import com.squareup.picasso.Picasso

class PhotosAdapter constructor(private val context: Context, private val images: MutableList<String>?) :  BaseAdapter() {

    private var isImageFitToScreen: Boolean = false

    override fun getCount(): Int {
        return images!!.size
    }

    override fun getItem(position: Int): String? {
        return images?.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var view = convertView

        if(view == null) {
            val layoutInflater = LayoutInflater.from(context)
            view = layoutInflater.inflate(R.layout.photo_location_item, parent, false)
        }

        val card = view!!.findViewById<ImageView>(R.id.photo_card)


        card.setOnClickListener {
            val intent = Intent(parent?.context, FullscreenImageActivity::class.java)
            intent.putExtra("imageUrl", images?.get(position))
            parent?.context?.startActivity(intent)
        }

            Picasso.with(context)
                .load(images?.get(position))
                .into(card)

        return view
    }

}

interface PhotoView{
    fun setFullScreenImage(urlImage: String)
}
package com.maveri.figma.main.widget

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.ImageView
import com.maveri.figma.R
import com.squareup.picasso.Picasso

class PhotosAdapter constructor(private val context: Context, photosView: PhotosView, private val images: MutableList<String>?) :  BaseAdapter() {

    private var isDeleting: Boolean = false
    private val photosView: PhotosView? = photosView
    private lateinit var deletePhotoButton: ImageButton

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
        deletePhotoButton = view!!.findViewById<ImageView>(R.id.delete_photo_button) as ImageButton

        if(isDeleting){
            deletePhotoButton.visibility = View.VISIBLE
        }

        card.setOnClickListener {
            if(isDeleting){
                deletePhotoButton = view!!.findViewById<ImageView>(R.id.delete_photo_button) as ImageButton
                    if(photosView?.checkDeletePressed()?.contains(images?.get(position)) == true){
                        deletePhotoButton.setImageDrawable(null)
                        photosView?.removeToDelete(images?.get(position))
                    }else{
                        deletePhotoButton.setImageDrawable(context.resources.getDrawable(R.drawable.ic_cross))
                        photosView?.insertToDelete(images?.get(position))
                    }

            }else {
                val intent = Intent(parent?.context, FullscreenImageActivity::class.java)
                intent.putExtra("imageUrl", images?.get(position))
                parent?.context?.startActivity(intent)
            }
        }

        card.setOnLongClickListener{
            isDeleting = true
            photosView?.notifyAllElements()
            true
        }

            Picasso.with(context)
                .load(images?.get(position))
                .into(card)

        return view
    }



    interface PhotosView{
        fun notifyAllElements()
        fun insertToDelete(photoUrl: String?)
        fun removeToDelete(photoUrl: String?)
        fun checkDeletePressed() : MutableList<String?>
    }
}
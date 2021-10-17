package com.maveri.figma.main.widget

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.maveri.figma.model.Location

class LocationsAdapter(locationView: LocationView) : ListAdapter<Location, LocationViewHolder>(DIFF_CALLBACK), LocationItemView.DeleteView {

    private val locationView: LocationView? = locationView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        return LocationViewHolder(LocationItemView(parent.context, this))

    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        (holder.itemView as? LocationItemView)?.setItem(getItem(position), position, this)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()){
            super.onBindViewHolder(holder, position, payloads)
        }else{
            payloads.let {
                if((it[0] as ArrayList<String>).size==2){
                    locationView?.updateLocationName(it[0] as ArrayList<String>)
                }else{
                    locationView?.updateLocationPhoto(it[0] as ArrayList<String>)
                }
            }
        }
    }

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Location>() {
            override fun areItemsTheSame(oldItem: Location, newItem: Location) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Location, newItem: Location) =
                oldItem.id == newItem.id
        }
    }

    interface LocationView{
        fun updateLocationName(locationInfo: ArrayList<String>)
        fun updateLocationPhoto(locationInfo: ArrayList<String>)
        fun deletePhotos(deleteList: HashMap<String, MutableList<String?>>)
    }

    override fun deletePhotos(deleteList: HashMap<String, MutableList<String?>>) {
        locationView?.deletePhotos(deleteList)
    }
}
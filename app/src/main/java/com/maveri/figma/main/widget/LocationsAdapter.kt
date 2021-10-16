package com.maveri.figma.main.widget

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.maveri.figma.model.Location

class LocationsAdapter(private val itemClick: (String) -> Unit) : ListAdapter<Location, LocationViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        return LocationViewHolder(LocationItemView(parent.context))

    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        (holder.itemView as? LocationItemView)?.setItem(getItem(position), itemClick)
    }

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Location>() {
            override fun areItemsTheSame(oldItem: Location, newItem: Location) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Location, newItem: Location) =
                oldItem.id == newItem.id
        }
    }
}
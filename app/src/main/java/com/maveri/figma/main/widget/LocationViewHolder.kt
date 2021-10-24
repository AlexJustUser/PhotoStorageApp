package com.maveri.figma.main.widget

import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.maveri.figma.R

class LocationViewHolder : RecyclerView.ViewHolder {
    constructor(itemView: LocationItemView) : super(itemView)

    val swipelayout : SwipeRevealLayout =
        itemView.findViewById(R.id.swipe_layout)
}

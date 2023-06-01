package com.example.pokeslots

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso


class PokemonCollectionAdapter(private val mList: ArrayList<Pokemon>) :
    RecyclerView.Adapter<PokemonCollectionAdapter.ViewHolder>() {

    // Holds the views
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView_singlePokeCollection)
        val textView: TextView = itemView.findViewById(R.id.textView_singlePokeCollection)
    }

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pokecollection, parent, false)
        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        RegisterViewForEvents(holder.imageView, mList[position])
        holder.textView.text = mList[position].name
        Picasso.get().load(mList[position].image).into(holder.imageView)
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

}
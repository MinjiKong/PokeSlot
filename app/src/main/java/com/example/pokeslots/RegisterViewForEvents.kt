package com.example.pokeslots

import android.app.Activity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class RegisterViewForEvents(view: View, var pokemon: Pokemon): View.OnClickListener {
    init {
        view.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val activity = v?.context as Activity

        val textViewName = activity.findViewById<TextView>(R.id.textView_pokeCollect_name2)
        val textViewLevel = activity.findViewById<TextView>(R.id.textView_pokeCollect_Level2)
        val imageViewFocus = activity.findViewById<ImageView>(R.id.imageView_PokeFocus2)
        val textViewSpecies = activity.findViewById<TextView>(R.id.textView_pokeCol_species)

        textViewName.text = pokemon.name
        textViewLevel.text = pokemon.affectionLevel.toString()
        textViewSpecies.text = pokemon.species

        Picasso.get().load(pokemon.image).into(imageViewFocus)
    }

}
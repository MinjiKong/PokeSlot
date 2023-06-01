package com.example.pokeslots

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokeslots.databinding.FragmentPokeColBinding
import com.squareup.picasso.Picasso

/**
 * A simple [Fragment] subclass.
 * Use the [PokeColFrag.newInstance] factory method to
 * create an instance of this fragment.
 */
class PokeColFrag : Fragment() {
    private var _binding: FragmentPokeColBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPokeColBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpDefaultFocus()
        val textViewName = binding.textViewPokeCollectName2

        val pokemonViewModel: PokemonViewModel by activityViewModels()
        val pokemonList = pokemonViewModel.pokemons

//        Set up Recycler View
        val recyclerView = binding.recyclerViewPokeCol

        recyclerView.adapter = PokemonCollectionAdapter(pokemonList)
        recyclerView.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.scrollToPosition(0)

        val button = view.findViewById<Button>(R.id.button_pokeCol_setMain2)
        button.setOnClickListener {
            pokemonViewModel.setMainFocus(textViewName.text.toString())
            pokemonViewModel.updateFireBase()
            Toast.makeText(context, "Progress Saved!", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_pokeColFrag_to_pokemonMainFragment)
        }
    }

    /**
     * For pokeCol Fragment
     */
    private fun setUpDefaultFocus() {

        val textViewName = binding.textViewPokeCollectName2
        val textViewLevel = binding.textViewPokeCollectLevel2
        val textViewSpecies = binding.textViewPokeColSpecies
        val imageViewFocus = binding.imageViewPokeFocus2

        val pokemonViewModel: PokemonViewModel by activityViewModels()
        val pokemon = pokemonViewModel.getPokemonByMainFocusBoolean()!!

        textViewName.text = pokemon.name
        textViewSpecies.text = pokemon.species
        textViewLevel.text = pokemon.affectionLevel.toString()

        Picasso.get().
        load(pokemon.image)
            .into(imageViewFocus)
    }
}
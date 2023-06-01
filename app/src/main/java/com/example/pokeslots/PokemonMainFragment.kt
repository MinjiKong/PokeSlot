package com.example.pokeslots

import android.content.ContentValues
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.pokeslots.databinding.FragmentPokemonMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "MainPokemonKey"

/**
 * A simple [Fragment] subclass.
 * Use the [PokemonMainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PokemonMainFragment : Fragment() {
 private val pokemonViewModel: PokemonViewModel by activityViewModels()
    private var _binding: FragmentPokemonMainBinding? = null
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
        _binding = FragmentPokemonMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUIElements()
        increaseAffectionButtons()
    }

    /**
     * Creates an observer for the pokemon list
     */
    private fun createPokemonsObserver(): Observer<ArrayList<Pokemon>> {
        return Observer<ArrayList<Pokemon>> {
            val pokemon: Pokemon? = pokemonViewModel.getPokemonByMainFocusBoolean()
            binding.textViewPokeName.text = """Name: ${pokemon?.name}"""
            binding.textViewAffectionLevel.text = """Level: ${pokemon?.affectionLevel.toString()}"""
            Picasso.get().load(pokemon!!.image).into(binding.imageViewPokemonMain)
        }
    }

    /**
     * Initializes the UI elements
     */
    private fun initializeUIElements() {
        val name = binding.textViewPokeName
        val image = binding.imageViewPokemonMain
        val affectionLevel = binding.textViewAffectionLevel

        val pokemon: Pokemon? = pokemonViewModel.getPokemonByMainFocusBoolean()

        if (pokemon != null) {
            name.text = "Username: ${pokemon.name}"
            affectionLevel.text = "Level: ${pokemon.affectionLevel.toString()}"
            Picasso.get().load(pokemon.image).into(image)
        } else {
            image.setImageResource(R.drawable.monsterball)
            name.text = "Loading..."
            affectionLevel.text = ""

            val scope = CoroutineScope(Main)
            scope.launch {
                delay(1000)
                initializeUIElements()
            }
        }
    }

    /**
     * Increases the affection level of the pokemon
     */
    private fun increaseAffectionButtons() {
        val buttonFeed = binding.buttonFeed
        val buttonBath = binding.buttonBath
        val buttonPet = binding.buttonPet
        setupButton(buttonFeed, 10, 5000)
        setupButton(buttonBath, 50, 10000)
        setupButton(buttonPet, 5, 0)
    }

    /**
     * Sets up a button to increase affection level
     */
    private fun setupButton(button: Button, affectionIncrease: Int, cooldownDuration: Long) {
        val progressBar = binding.progressBarMainFragAffectionProgress
        val affectionLevel = binding.textViewAffectionLevel

        button.setOnClickListener {
            pokemonViewModel.increasePokemonAffection(affectionIncrease)
            affectionLevel.text = "Level: ${pokemonViewModel.getMainPokemon()?.affectionLevel}"
            progressBar.progress = pokemonViewModel.getMainPokemon()?.progressAffection!!
            button.startCoolDown(cooldownDuration)
        }

        binding.buttonMaincolSave.setOnClickListener {
            pokemonViewModel.updateFireBase()
            Toast.makeText(context, "Progress Saved!", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Lamdba function to start a cool down on a button
     */
    private fun Button.startCoolDown(durationInMillis: Long) {
        if (durationInMillis == 0L) {
            return
        }
        isEnabled = false
        Toast.makeText(context, " ${durationInMillis/1000} seconds left!", Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({
            isEnabled = true
        }, durationInMillis)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PokemonMainFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: Bundle) =
            PokemonMainFragment().apply {
                arguments = Bundle().apply {
                    putBundle(ARG_PARAM1, param1)
                }
            }
    }
}
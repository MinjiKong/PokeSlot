package com.example.pokeslots

import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.pokeslots.databinding.FragmentPokeSlotsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.CountDownLatch

class PokeSlotsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var _binding: FragmentPokeSlotsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPokeSlotsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val drawButton = binding.buttonPokeslotsDraw
        val species = binding.textViewPokeColSpecies
        val image = binding.imageViewPokeslotsMonsterball

        val name = binding.textViewPokeslotsName
        val setName = binding.editTextPokeslotsName
        val saveButton = binding.buttonPokeslotsSave

        val pokemonViewModel: PokemonViewModel by activityViewModels()

        drawButton.setOnClickListener {
            val (pokemonSpecies, pokemonImage, pokemonID) = pokemonViewModel.fetchPokemonDetailsByRandom()
            val animationSet = AnimationSet(true)
            animationSet.addAnimation(AnimationUtils.loadAnimation(view.context, R.anim.zoom_in))
            animationSet.addAnimation(AnimationUtils.loadAnimation(view.context, R.anim.shake))

            image.startAnimation(animationSet)

            Handler().postDelayed({
                Picasso.get().load(pokemonImage).into(image)
                species.text = "Species: ${pokemonSpecies}"
                name.visibility = View.VISIBLE
                setName.visibility = View.VISIBLE
                saveButton.visibility = View.VISIBLE
                setName.setText("")
                image.clearAnimation()
            }, 2000)

            var isSaveButtonPressed = false

            saveButton.setOnClickListener {
                val pokemonName = setName.text.toString()

                if (!isSaveButtonPressed && pokemonName.isNotEmpty()) {
                    isSaveButtonPressed = true
                    name.text = "Your new Pokemon has been saved"
                    pokemonViewModel.addNewPokemon(
                        Pokemon(
                            pokemonName,
                            pokemonSpecies,
                            pokemonID,
                            pokemonImage,
                            pokemonViewModel.getMainPokemon()!!.belongsTo,
                            false,
                            0,
                            1
                        )
                    )
                    pokemonViewModel.updateFireBase()
                    Toast.makeText(context, "Progress Saved!", Toast.LENGTH_SHORT).show()
                } else if (pokemonName.isEmpty()) {
                    name.text = "Kindly enter a name for your new Pokemon"
                }
            }
        }
    }

}
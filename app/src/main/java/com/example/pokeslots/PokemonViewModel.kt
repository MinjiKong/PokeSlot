package com.example.pokeslots

import android.content.ContentValues
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.CountDownLatch

class PokemonViewModel : ViewModel() {
    val pokemonsLiveData = MutableLiveData<ArrayList<Pokemon>>()
    var pokemons = pokemonsLiveData.value ?: ArrayList()
    var pokemonTotalLevel = MutableLiveData<Int>()


    fun addNewPokemon(pokemon: Pokemon) {
        pokemons.add(pokemon)
    }

    fun initializeTotalLevelValue() {
        pokemonTotalLevel.value = updateTotalLevel()
    }

    fun updateTotalLevel(): Int {
        /**
         * Returns the total level of all the pokemon
         */
        var totalLevel = 0
        for (pokemon in pokemons) {
            totalLevel += pokemon.affectionLevel ?: 0
        }
        pokemonTotalLevel.value = totalLevel
        return totalLevel
    }

    fun getMainPokemon(): Pokemon? {
        /**
         * Returns the pokemon that is the main focus
         */
        for (pokemon in pokemons) {
            if (pokemon.mainFocus == true) {
                return pokemon
            }
        }
        return null
    }
    fun increasePokemonAffection(points: Int) {
        /**
         * Increases the affection of the main pokemon
         */
        val pokemon = getPokemonByMainFocusBoolean()
        pokemon?.increaseAffectionProgress(points)
        updateTotalLevel()
    }

    private fun getPokemonByName(name: String): Pokemon? {
        /**
         * Returns the pokemon that is the main focus
         */
        for (pokemon in pokemons) {
            if (pokemon.name == name) {
                return pokemon
            }
        }
        return null
    }

    fun setMainFocus(pokemonName: String) {
        /**
         * Sets the main focus to the pokemon with the pokemon name given
         */
        // Find the pokemon with the pokemon name
        val pokemon = getPokemonByName(pokemonName)

//        To turn off current main focus
        for (poke in pokemons) {
            if (poke.mainFocus == true) {
                poke.mainFocus = false
            }
        }
        // To set new main focus
        for (poke in pokemons) {
            poke.mainFocus = poke.pokeId == pokemon?.pokeId
        }
    }

    fun getPokemonByMainFocusBoolean(): Pokemon? {
        /**
         * Returns the pokemon that is the main focus
         */
        for (pokemon in pokemons) {
            if (pokemon.mainFocus == true) {
                return pokemon
            }
        }
        return null
    }

    fun listOfPokemonFromFireBase(): List<Pokemon> {
        val db = Firebase.firestore
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val userRef = db.collection("users").document(userId!!)
        println("line 169" + userRef)

        userRef.get()
            .addOnSuccessListener { documentSnapshot ->
                val pokemonList = documentSnapshot.get("pokemon") as? List<Map<String, Any>>
                println("line 173" + pokemonList)
                if (pokemonList != null){
                    for (pokemonMap in pokemonList){
                        val pokemon = Pokemon(
                            name = pokemonMap["name"] as String,
                            species = pokemonMap["species"] as String,
                            pokeId = (pokemonMap["pokeId"] as Long).toInt(),
                            image = pokemonMap["image"] as String,
                            belongsTo = pokemonMap["belongsTo"] as String,
                            mainFocus = pokemonMap["mainFocus"] as Boolean,
                            progressAffection = (pokemonMap["progressAffection"] as Long).toInt(),
                            affectionLevel = (pokemonMap["affectionLevel"] as Long).toInt()
                        )
                        if (pokemons.contains(pokemon)){
                            Log.d(ContentValues.TAG, "Pokemon already exists in the collection")
                        }else{
                            addNewPokemon(pokemon)
                            println("From line 190" + pokemons)
                        }
                    }
                }
            }.addOnFailureListener() { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }
        return pokemons
    }

    fun fetchPokemonDetailsByName(pokemonName: String): Triple<String, String, Int> {
        val client = OkHttpClient()

        var name = ""
        var imageUrl = ""
        var id = 0

        val latch = CountDownLatch(1)

        val request = Request.Builder()
            .url("https://pokeapi.co/api/v2/pokemon/$pokemonName")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val json = response.body()?.string()
                val jsonObject = JSONObject(json!!)
                name = jsonObject.getString("name")
                imageUrl = jsonObject.getJSONObject("sprites").getString("other")
                    .let { JSONObject(it).getJSONObject("official-artwork").getString("front_default") }
                id = jsonObject.getInt("id")
                latch.countDown()
            }
        })
        latch.await()
        return Triple(name, imageUrl, id)
    }

    fun updateFireBase(){
        val db = Firebase.firestore
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid
        val userDocRef = db.collection("users").document(userId!!)

        // Loop through the pokemons ArrayList and update each Pokemon in the Firebase map array
        val pokemonList = pokemons.map { pokemon ->
            mapOf(
                "name" to pokemon.name,
                "species" to pokemon.species,
                "pokeId" to pokemon.pokeId,
                "image" to pokemon.image,
                "belongsTo" to pokemon.belongsTo,
                "mainFocus" to pokemon.mainFocus,
                "progressAffection" to pokemon.progressAffection,
                "affectionLevel" to pokemon.affectionLevel
            )
        }
        // Update the Firebase document with the list of Pokemon maps
        userDocRef.update("pokemon", pokemonList)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "Saved the progress")
            }
            .addOnFailureListener {
                Log.d(ContentValues.TAG, "Failed")
            }
    }

    fun fetchPokemonDetailsByRandom(): Triple<String, String, Int> {
        val client = OkHttpClient()

        var name = ""
        var imageUrl = ""
        var id = 0

        val latch = CountDownLatch(1)

        val request = Request.Builder()
            .url("https://pokeapi.co/api/v2/pokemon/${(1..500).random()}")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val json = response.body()?.string()
                val jsonObject = JSONObject(json!!)
                name = jsonObject.getString("name")
                imageUrl = jsonObject.getJSONObject("sprites").getString("other")
                    .let { JSONObject(it).getJSONObject("official-artwork").getString("front_default") }
                id = jsonObject.getInt("id")
                latch.countDown()
            }
        })
        latch.await()
        return Triple(name, imageUrl, id)
    }

}
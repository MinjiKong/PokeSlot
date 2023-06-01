package com.example.pokeslots

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.pokeslots.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main

//test
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.CountDownLatch


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var mainPokemon: Pokemon? = null
    private lateinit var navController: NavController
    private val pokemonViewModel: PokemonViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupLevelAndName()

        val scope = CoroutineScope(Main)
        scope.launch {
            pokemonViewModel.listOfPokemonFromFireBase()
            delay(1000)
            setAsMainPokemon()
            binding.textViewMainUsername.text = "Username: ${mainPokemon?.belongsTo}"
            pokemonViewModel.initializeTotalLevelValue()
            binding.textViewMainLevel.text = "Level: ${pokemonViewModel.pokemonTotalLevel.value}"
        }

        pokemonViewModel.pokemonTotalLevel.observe(this) {
            binding.textViewMainLevel.text = "Level: $it"
        }

        //Navbar
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView_PokemonMain) as NavHostFragment
        navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(navController.graph)

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.botNavViewMain.setupWithNavController(navController)

        // Hide the app bar
        supportActionBar?.hide()

    }

    /**
     * Ensures the UI is empty before the data is exists
     */
    private fun setupLevelAndName() {
        binding.textViewMainLevel.text = ""
        binding.textViewMainUsername.text = "Loading..."
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bot_menu, menu)
        menu?.clear()
        return super.onCreateOptionsMenu(menu)
    }
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    /**
     * Finds the main pokemon and stores it as a variable
     */
    private fun setAsMainPokemon() {
        mainPokemon = pokemonViewModel.getMainPokemon()
    }
}
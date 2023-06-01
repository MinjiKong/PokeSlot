package com.example.pokeslots

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.example.pokeslots.databinding.ActivitySignUpBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySignUpBinding
    private val pokemonViewModel: PokemonViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = Firebase.auth

        val signIn = binding.textViewSignupLogin
        signIn.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        val signupButton = binding.buttonSignup

        signupButton.setOnClickListener {
            performSignUp()
        }
    }

    private fun performSignUp() {
        val email = binding.editTextSignupEmail
        val password = binding.editTextSignupPassword

        if (email.text.isEmpty() || password.text.isEmpty()){
            Toast.makeText(this, "Please fill all the fields.", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val inputEmail = email.text.toString()
        val inputPassword = password.text.toString()

        auth.createUserWithEmailAndPassword(inputEmail, inputPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val db = Firebase.firestore
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    val userId = currentUser?.uid

                    val(name1, image1, id1) = pokemonViewModel.fetchPokemonDetailsByName("pikachu")

                    val defaultPokemons = listOf(currentUser?.email?.split("@")
                            ?.let { Pokemon("PikaPika", name1, id1, image1, it.first(), true, 0, 1) })


                    val userDocRef = db.collection("users").document(userId!!)
                    val user = hashMapOf(
                        "email" to inputEmail,
                        "pokemon" to defaultPokemons
                    )

                    userDocRef.set(user)
                        .addOnSuccessListener {
                            Log.d(TAG, "DocumentSnapshot added with ID: $userId")
                        }
                        .addOnFailureListener { e ->
                            // Error adding document
                            Log.w(TAG, "Error adding document", e)
                        }
                    // Sign in success, move to PokeSlot fragment
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)

                    Toast.makeText(baseContext, "Success.",
                        Toast.LENGTH_SHORT).show()

                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error occured ${it.localizedMessage}",
                Toast.LENGTH_SHORT).show()
            }
    }
}
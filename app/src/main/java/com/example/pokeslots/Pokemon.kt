package com.example.pokeslots

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Pokemon(
    var name: String, var species: String, var pokeId: Int, var image: String, var belongsTo: String,
    var mainFocus: Boolean?, var progressAffection: Int?, var affectionLevel: Int?): Parcelable {
        fun increaseAffectionProgress(value: Int) {
            /**
             * Increases the affection level of the pokemon
             */
            if (progressAffection != null) {
                if (progressAffection!! >= 100) {
                    progressAffection = 0
                    affectionLevel = affectionLevel?.plus(1)

                } else {
                    println("Pokemon: $progressAffection")
                    progressAffection = progressAffection?.plus(value)
                }
            }
        }
}
package com.trivago.challenge.characters.model

import com.google.gson.annotations.SerializedName
import com.karntrehan.starwars.dependencies.Exclude

data class CharacterDetailsModel(
    @SerializedName("url") val url: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("birth_year") val birthYear: String?,
    @SerializedName("height") val heightCentimeters: String?,
    @Exclude val heightFtInches: Pair<String, String>?,

    @SerializedName("species") val speciesUrl: List<String>?,
    @Exclude val specieDetails: List<com.trivago.challenge.characters.model.SpeciesDetailsModel>?,

    @SerializedName("films") val filmUrls: List<String>?,
    @Exclude val filmDetails: List<com.trivago.challenge.characters.model.FilmDetailsModel>?
) {



}

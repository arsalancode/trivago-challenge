package com.trivago.challenge.characters.networking

import com.karntrehan.starwars.architecture.RemoteResponse
import com.karntrehan.starwars.characters.model.*
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface CharacterService {

    @GET
    fun getCharacters(@Url url: String): Single<RemoteResponse<List<com.trivago.challenge.characters.model.CharacterSearchModel>>>

    @GET("people")
    fun searchCharacter(@Query("search") query: String): Single<RemoteResponse<List<com.trivago.challenge.characters.model.CharacterSearchModel>>>

    @GET
    fun getCharacterDetails(@Url url: String): Single<com.trivago.challenge.characters.model.CharacterDetailsModel>

    @GET
    fun getCharacterSpecies(@Url url: String?): Single<com.trivago.challenge.characters.model.SpeciesResponseModel>

    @GET
    fun getCharacterHomeworld(@Url url: String?): Single<com.trivago.challenge.characters.model.HomeworldResponseModel?>

    @GET
    fun getCharacterFilms(@Url url: String?): Single<com.trivago.challenge.characters.model.FilmResponseModel>

}

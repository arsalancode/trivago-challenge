package com.trivago.challenge.characters.networking

import com.trivago.challenge.characters.model.CharacterDetailsModel
import com.trivago.challenge.characters.model.FilmResponseModel
import com.trivago.challenge.characters.model.HomeworldResponseModel
import com.trivago.challenge.characters.model.SpeciesResponseModel
import io.reactivex.Single

class CharacterDetailsRepo(private val service: com.trivago.challenge.characters.networking.CharacterService) :
    com.trivago.challenge.characters.networking.CharacterDetailsContract.Repo {

    override fun getCharacterDetails(url: String): Single<com.trivago.challenge.characters.model.CharacterDetailsModel> = service.getCharacterDetails(url)

    override fun getSpecieDetails(url: String?): Single<com.trivago.challenge.characters.model.SpeciesResponseModel> = service.getCharacterSpecies(url)

    override fun getFilmDetails(url: String?): Single<com.trivago.challenge.characters.model.FilmResponseModel> = service.getCharacterFilms(url)

    override fun getHomeworldDetails(url: String?): Single<com.trivago.challenge.characters.model.HomeworldResponseModel?> = service.getCharacterHomeworld(url)

}
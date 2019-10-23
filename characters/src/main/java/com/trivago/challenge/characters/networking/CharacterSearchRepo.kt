package com.trivago.challenge.characters.networking

import com.karntrehan.starwars.architecture.RemoteResponse
import io.reactivex.Single

class CharacterSearchRepo(private val service: CharacterService) :
    CharacterSearchContract.Repo {

    override fun characters(url: String)
            : Single<RemoteResponse<List<com.trivago.challenge.characters.model.CharacterSearchModel>>> = service.getCharacters(url)

    override fun searchCharacter(query: String)
            : Single<RemoteResponse<List<com.trivago.challenge.characters.model.CharacterSearchModel>>> = service.searchCharacter(query)

}
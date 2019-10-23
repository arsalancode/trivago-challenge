package com.trivago.challenge.characters.networking

import com.karntrehan.starwars.architecture.RemoteResponse
import com.trivago.challenge.characters.model.CharacterSearchModel
import io.reactivex.Single

class CharacterSearchRepo(private val service: com.trivago.challenge.characters.networking.CharacterService) :
    com.trivago.challenge.characters.networking.CharacterSearchContract.Repo {

    override fun characters(url: String)
            : Single<RemoteResponse<List<com.trivago.challenge.characters.model.CharacterSearchModel>>> = service.getCharacters(url)

    override fun searchCharacter(query: String)
            : Single<RemoteResponse<List<com.trivago.challenge.characters.model.CharacterSearchModel>>> = service.searchCharacter(query)

}
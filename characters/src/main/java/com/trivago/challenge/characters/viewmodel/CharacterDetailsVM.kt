package com.trivago.challenge.characters.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.trivago.challenge.architecture.BaseVM
import com.trivago.challenge.extensions.divide
import com.trivago.challenge.extensions.hide
import com.trivago.challenge.extensions.show
import com.trivago.challenge.characters.model.CharacterDetailsModel
import com.trivago.challenge.characters.networking.CharacterDetailsContract
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class CharacterDetailsVM(private val repo: CharacterDetailsContract.Repo) : BaseVM() {

    private val characterDetails = MutableLiveData<CharacterDetailsModel>()

    private var specieName: String? = null
    private var specieLanguage: String? = null

    fun getCharacterDetails(url: String): LiveData<CharacterDetailsModel> {
        if (characterDetails.value == null) {

            _loading.show()

            repo.getCharacterDetails(url)
                //Subscribe on io thread
                .subscribeOn(Schedulers.io())
                //Move to computation thread
                .observeOn(Schedulers.computation())
                .map {
                    //convert character's height from cm to feet and inches
                    val details = it.copy(heightFtInches = cmToFeet(it.heightCentimeters))
                    //Pass the details to livedata
                    characterDetails.postValue(details)
                    //Pass details along the chain
                    return@map details
                }
                .flatMap { details ->
                    //Fetch the species and film details in parallel and zip their results together
                    Single.zip(speciesAndHomeWorld(details), films(details),
                        BiFunction { speciesDetails: List<com.trivago.challenge.characters.model.SpeciesDetailsModel>, filmDetails: List<com.trivago.challenge.characters.model.FilmDetailsModel> ->
                            //Wait for results and pass it to the details we already have
                            details.copy(specieDetails = speciesDetails, filmDetails = filmDetails)
                        })
                }.subscribe({
                    _loading.hide()
                    //Pass complete character details to UI
                    characterDetails.postValue(it)
                }, { handleError(it) })
                .addTo(disposable)
        }
        return characterDetails
    }

    fun cmToFeet(heightCentimeters: String?): Pair<String, String>? {
        return heightCentimeters
            ?.toIntOrNull()
            ?.let { Pair(it.divide(30.48), it.divide(2.54)) }
    }


    private fun speciesAndHomeWorld(details: CharacterDetailsModel): Single<List<com.trivago.challenge.characters.model.SpeciesDetailsModel>> {
        //Iterate through all the character species urls
        return Flowable.fromIterable(details.speciesUrl)
            //Get the details for each specie
            .flatMapSingle { speciesUrl -> repo.getSpecieDetails(speciesUrl) }
            //For each specie result, get the homeworld / planet details
            .flatMapSingle { specie ->
                specieName = specie.name
                specieLanguage = specie.language
                return@flatMapSingle repo
                    .getHomeworldDetails(specie.homeworldUrl)
                    //If homeworld api fails, return a default object
                    // to allow the stream to continue.
                    .onErrorReturn { com.trivago.challenge.characters.model.HomeworldResponseModel() }
            }
            .observeOn(Schedulers.computation())
            //Combine the specie details with homeworld details
            .map { homeworldResponse ->
                com.trivago.challenge.characters.model.SpeciesDetailsModel(
                    specieName, specieLanguage,
                    homeworldResponse.name, homeworldResponse.population
                )
            }
            //Wait for all species and homeworld results
            .toList()
    }

    private fun films(details: CharacterDetailsModel): Single<List<com.trivago.challenge.characters.model.FilmDetailsModel>>? {
        return Flowable.fromIterable(details.filmUrls)
            //Get details from each film
            .flatMapSingle { filmUrl -> repo.getFilmDetails(filmUrl) }
            //Convert response model to model required by UI
            .map { film ->
                com.trivago.challenge.characters.model.FilmDetailsModel(
                    film.title,
                    film.releaseDate,
                    film.openingCrawl
                )
            }
            //Wait for all film results
            .toList()
    }
}

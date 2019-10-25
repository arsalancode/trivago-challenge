package com.trivago.challenge.characters.viewmodel

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.trivago.challenge.characters.model.CharacterSearchModel
import com.trivago.challenge.characters.networking.CharacterSearchContract
import com.trivago.challenge.networking.RemoteResponse
import com.trivago.challenge.view.extensions.hide
import com.trivago.challenge.view.extensions.show
import com.trivago.challenge.viewmodel.BaseVM
import io.reactivex.Single
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class CharacterSearchVM(private val repo: CharacterSearchContract.Repo) : BaseVM() {

    private val _characters = MutableLiveData<List<CharacterSearchModel>>()
    val characters: LiveData<List<CharacterSearchModel>> by lazy { _characters }

    private var nextPageUrl: String? = ""
    private var processing: Boolean = false

    private val _paginationLoading = MutableLiveData<Boolean>()
    val paginationLoading: LiveData<Boolean> by lazy { _paginationLoading }

    private val initialAPI = "people"

    private var birthYear: String? = null

    fun initialLoad() {
        if (_characters.value != null && !_characters.value.isNullOrEmpty()) return

        _loading.show()
        getCharacters(url = initialAPI, resetItems = true)
    }

    private fun getCharacters(url: String, resetItems: Boolean) {
        if (processing) return

        processing = true
        handleCharactersObs(repo.characters(url), resetItems, null)
    }

    /**
     * Handle the characters returned from the API
     *
     * resetItems clears the current contents.
     * */
    private fun handleCharactersObs(
        charactersObs: Single<RemoteResponse<List<CharacterSearchModel>>>,
        resetItems: Boolean,
        birthYear: String?
    ) {
        charactersObs
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .map { response ->
                nextPageUrl = response.next
                return@map response.results
            }
            .map { searchModels ->
                appendOrSetResults(resetItems, _characters.value, searchModels)
            }
            .map { it ->

                println("Filter: $it")

                when (birthYear) {
                    null -> it
                    else -> it.filter { it.birthYear.equals(birthYear, true) }
                }
            }
            .subscribe({
                _loading.hide()
                _paginationLoading.hide()
                _characters.postValue(it)
                processing = false
            }, {
                handleError(it)
                processing = false
            })
            .addTo(disposable)
    }

    private fun appendOrSetResults(
        resetItems: Boolean,
        existingData: List<CharacterSearchModel>?,
        newData: List<CharacterSearchModel>
    ): List<CharacterSearchModel> {
        val finalData = mutableListOf<CharacterSearchModel>()
        if (resetItems || existingData.isNullOrEmpty())
            finalData.addAll(newData)
        else {
            finalData.addAll(existingData)
            finalData.addAll(newData)
        }
        return finalData
    }

    fun loadNextPage() {
        nextPageUrl?.run {
            _paginationLoading.show()

            when (birthYear) {
                null -> getCharacters(this, false)
                else -> handleCharactersObs(repo.characters(this), false, birthYear + "BBY")
            }
        }
    }

    fun searchCharacter(query: String?) {
        if (query.isNullOrEmpty()) return

        _loading.show()

        when (query.toIntOrNull()) {
            null -> {
                birthYear = null
                handleCharactersObs(repo.searchCharacter(query), true, null)
            }
            else -> {
                birthYear = query
                handleCharactersObs(repo.characters(initialAPI), true, birthYear + "BBY")

                // also find in next pages //
//                filterCharacterByBirthYear(birthYear)
            }
        }

//        handleCharactersObs(repo.searchCharacter(query), true)
    }

    fun refreshCharacters() {
        _loading.show()
        birthYear = null
        getCharacters(url = initialAPI, resetItems = true)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun setCharacter(characters: List<CharacterSearchModel>?) {
        _characters.postValue(characters)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun getInitialApi() = initialAPI

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun setNextPageUrl(url: String?) {
        nextPageUrl = url
    }
}


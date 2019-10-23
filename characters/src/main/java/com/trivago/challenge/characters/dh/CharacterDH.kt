package com.trivago.challenge.characters.dh

import com.trivago.challenge.characters.networking.CharacterDetailsContract
import com.trivago.challenge.characters.networking.CharacterDetailsRepo
import com.trivago.challenge.characters.networking.CharacterService
import com.trivago.challenge.characters.networking.CharacterSearchContract
import com.trivago.challenge.characters.networking.CharacterSearchRepo
import com.karntrehan.starwars.characters.viewmodel.CharacterDetailsVM
import com.karntrehan.starwars.characters.viewmodel.CharacterSearchVM
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.create

/*Dependency holder for Character module*/
object CharacterDH {

    fun init() {
        loadKoinModules(
            com.trivago.challenge.characters.dh.CharacterDH.characterDetailsModule(),
            com.trivago.challenge.characters.dh.CharacterDH.characterSearchModule(),
            com.trivago.challenge.characters.dh.CharacterDH.characterModule()
        )
    }

    //Details module
    private fun characterDetailsModule(): Module = module {
        viewModel { CharacterDetailsVM(get()) }
        single {
            com.trivago.challenge.characters.dh.CharacterDH.characterDetailsContract(
                get()
            )
        }
    }

    private fun characterDetailsContract(service: com.trivago.challenge.characters.networking.CharacterService)
            : com.trivago.challenge.characters.networking.CharacterDetailsContract.Repo =
        com.trivago.challenge.characters.networking.CharacterDetailsRepo(service)

    //Search module
    private fun characterSearchModule(): Module = module {
        viewModel { CharacterSearchVM(get()) }
        single {
            com.trivago.challenge.characters.dh.CharacterDH.characterSearchContract(
                get()
            )
        }
    }

    private fun characterSearchContract(service: com.trivago.challenge.characters.networking.CharacterService)
            : com.trivago.challenge.characters.networking.CharacterSearchContract.Repo =
        com.trivago.challenge.characters.networking.CharacterSearchRepo(service)


    //Character module
    private fun characterModule(): Module = module {
        single { com.trivago.challenge.characters.dh.CharacterDH.characterService(get()) }
    }

    private fun characterService(retrofit: Retrofit): com.trivago.challenge.characters.networking.CharacterService = retrofit.create()

}
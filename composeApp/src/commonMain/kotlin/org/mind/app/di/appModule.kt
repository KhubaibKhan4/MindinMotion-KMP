package org.mind.app.di

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.database.database
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module
import org.mind.app.createDriver
import org.mind.app.data.local.DatabaseHelper
import org.mind.app.domain.repository.Repository
import org.mind.app.presentation.viewmodel.MainViewModel

val appModule = module {
    single(qualifier = qualifier("firebase")) { Firebase.auth }
    single(qualifier = qualifier("firebase-database")) { Firebase.database }
    single {
        Repository(
            get(qualifier = qualifier("firebase")),
            get(qualifier("firebase-database"))
        )
    }
    single { DatabaseHelper(createDriver()) }
    single { MainViewModel(get(), get()) }
}
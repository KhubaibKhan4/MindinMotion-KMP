package org.mind.app.di

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module
import org.mind.app.createDriver
import org.mind.app.data.local.DatabaseHelper
import org.mind.app.domain.repository.Repository
import org.mind.app.presentation.viewmodel.MainViewModel

val appModule = module {
    single(qualifier = qualifier("firebase")) { Firebase.auth }
    single { Repository(get(qualifier = qualifier("firebase"))) }
    single { DatabaseHelper(createDriver()) }
    single { MainViewModel(get(),get()) }
}
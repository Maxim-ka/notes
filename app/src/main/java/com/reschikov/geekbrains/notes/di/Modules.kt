package com.reschikov.geekbrains.notes.di

import android.content.Context
import android.view.inputmethod.InputMethodManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.reschikov.geekbrains.notes.repository.Repository
import com.reschikov.geekbrains.notes.repository.provider.FireStoreProvider
import com.reschikov.geekbrains.notes.repository.provider.RemoteDataProvider
import com.reschikov.geekbrains.notes.view.navigation.RouterSupportMessage
import com.reschikov.geekbrains.notes.repository.preference.Storage
import com.reschikov.geekbrains.notes.view.fragments.EditorNoteFragment
import com.reschikov.geekbrains.notes.viewmodel.activity.SplashViewModel
import com.reschikov.geekbrains.notes.viewmodel.fragments.ListNotesViewModel
import com.reschikov.geekbrains.notes.viewmodel.fragments.NoteViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router

val appModule = module {
    single { Storage(get()) }
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single<RemoteDataProvider> { FireStoreProvider(get(), get(), get()) }
    single { Repository(get()) }
}

val navigatorModule = module {
    single<Cicerone<Router>>{ Cicerone.create() }
    single { getNavigatorHolder(get())}
    single { getRouter(get()) }
}

fun getNavigatorHolder(cicerone: Cicerone<Router>): NavigatorHolder = cicerone.navigatorHolder

fun getRouter(cicerone: Cicerone<Router>): RouterSupportMessage = RouterSupportMessage(cicerone.router)

val viewModelModule = module {
    viewModel { NoteViewModel(get(), get()) }
    viewModel { ListNotesViewModel(get()) }
    viewModel { SplashViewModel(get(), get()) }
}

val softKeyModule = module {
    scope(named<EditorNoteFragment>()) {
        scoped { getSoftKey(get()) }
    }
}

fun getSoftKey(context: Context): InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
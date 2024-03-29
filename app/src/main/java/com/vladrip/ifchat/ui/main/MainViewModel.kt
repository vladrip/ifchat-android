package com.vladrip.ifchat.ui.main

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.room.withTransaction
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.vladrip.ifchat.R
import com.vladrip.ifchat.data.DataHolder
import com.vladrip.ifchat.data.local.LocalDatabase
import com.vladrip.ifchat.data.network.RequestRestorer
import com.vladrip.ifchat.data.repository.MessagingRepository
import com.vladrip.ifchat.data.repository.PersonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@SuppressLint("StaticFieldLeak") //because lint doesn't know APP context is injected and thinks there is a leak
@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val personRepository: PersonRepository,
    private val messagingRepository: MessagingRepository,
    private val requestRestorer: RequestRestorer,
    private val localDb: LocalDatabase,
    private val gson: Gson,
) : ViewModel() {

    fun getPerson(uid: String): Flow<PersonUiState> {
        return personRepository.getPerson(uid).map {
            when (it.status) {
                DataHolder.Status.SUCCESS -> {
                    val person = it.data!!
                    PersonUiState(fullName = person.fullName())
                }

                DataHolder.Status.LOADING ->
                    PersonUiState(fullName = context.getString(R.string.loading))

                DataHolder.Status.NETWORK_ERROR ->
                    PersonUiState(fullName = context.getString(R.string.waiting_for_network))

                DataHolder.Status.ERROR ->
                    PersonUiState(fullName = context.getString(R.string.unknown_error))
            }
        }
    }

    suspend fun restoreRequests() {
        requestRestorer.restoreRequests()
    }

    suspend fun saveDeviceToken(token: String) {
        messagingRepository.saveDeviceToken(token)
    }

    suspend fun logout() {
        messagingRepository.deleteCurrentDeviceToken()
        localDb.withTransaction { localDb.clearAllTables() }
        Firebase.auth.signOut()
    }

    fun gson(): Gson {
        return gson
    }
}
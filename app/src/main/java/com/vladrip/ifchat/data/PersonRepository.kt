package com.vladrip.ifchat.data

import com.haroldadmin.cnradapter.NetworkResponse
import com.haroldadmin.cnradapter.executeWithRetry
import com.vladrip.ifchat.api.IFChatService
import com.vladrip.ifchat.db.LocalDatabase
import com.vladrip.ifchat.ui.state.PersonUiState
import com.vladrip.ifchat.ui.state.StateHolder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PersonRepository @Inject constructor(
    private val api: IFChatService,
    localDb: LocalDatabase,
) {
    private val personDao = localDb.personDao()

    fun getPerson(uid: String): Flow<StateHolder<PersonUiState>> = flow {
        val personLocal = personDao.get(uid)
        if (personLocal != null)
            emit(StateHolder(state = PersonUiState(fullName = personLocal.getFullName())))
        else emit(StateHolder(status = StateHolder.Status.LOADING))

        var response = api.getPerson(uid)
        if (response is NetworkResponse.NetworkError) {
            emit(StateHolder(status = StateHolder.Status.NETWORK_ERROR))
            response = executeWithRetry(times = Int.MAX_VALUE) {
                api.getPerson(uid)
            }
        }

        emit(when (response) {
            is NetworkResponse.Success -> {
                val person = response.body
                personDao.insert(person)
                StateHolder(state = PersonUiState(fullName = person.getFullName()))
            }
            else -> StateHolder(status = StateHolder.Status.ERROR)
        })
    }
}
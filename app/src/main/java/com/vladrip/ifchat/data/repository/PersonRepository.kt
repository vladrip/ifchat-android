package com.vladrip.ifchat.data.repository

import com.haroldadmin.cnradapter.NetworkResponse
import com.haroldadmin.cnradapter.executeWithRetry
import com.vladrip.ifchat.data.DataHolder
import com.vladrip.ifchat.data.entity.Person
import com.vladrip.ifchat.data.local.LocalDatabase
import com.vladrip.ifchat.data.network.IFChatService
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

    fun getPerson(uid: String): Flow<DataHolder<Person>> = flow {
        val localPerson = personDao.get(uid)
        if (localPerson != null)
            emit(DataHolder(localPerson))
        else emit(DataHolder(status = DataHolder.Status.LOADING))

        var response = api.getPerson(uid)
        if (response is NetworkResponse.NetworkError) {
            emit(DataHolder(status = DataHolder.Status.NETWORK_ERROR))
            response = executeWithRetry(times = Int.MAX_VALUE) {
                api.getPerson(uid)
            }
        }

        emit(
            when (response) {
                is NetworkResponse.Success -> {
                    val person = response.body
                    personDao.insert(person)
                    DataHolder(person)
                }

                else -> DataHolder(status = DataHolder.Status.ERROR)
            }
        )
    }
}
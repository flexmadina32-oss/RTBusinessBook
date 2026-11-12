package com.riotech.rtbusinessbook.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.riotech.rtbusinessbook.data.AppDatabase
import com.riotech.rtbusinessbook.data.TransactionEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.get(app).transactionDao()

    val active: StateFlow<List<TransactionEntity>> =
        dao.observeActive().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val deleted: StateFlow<List<TransactionEntity>> =
        dao.observeDeleted().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun save(
        existing: TransactionEntity?, type: String, amountMinor: Long, category: String,
        description: String, method: String, dateMillis: Long
    ) = viewModelScope.launch {
        val now = System.currentTimeMillis()
        if (existing == null) {
            dao.insert(
                TransactionEntity(
                    type = type, amountMinor = amountMinor, category = category,
                    description = description, paymentMethod = method, dateMillis = dateMillis,
                    createdAt = now, updatedAt = now
                )
            )
        } else {
            dao.update(
                existing.copy(
                    amountMinor = amountMinor, category = category, description = description,
                    paymentMethod = method, dateMillis = dateMillis, updatedAt = now
                )
            )
        }
    }

    fun delete(id: Long) = viewModelScope.launch { dao.softDelete(id, System.currentTimeMillis()) }
    fun restore(id: Long) = viewModelScope.launch { dao.restore(id, System.currentTimeMillis()) }
    fun purge(id: Long) = viewModelScope.launch { dao.purge(id) }
}

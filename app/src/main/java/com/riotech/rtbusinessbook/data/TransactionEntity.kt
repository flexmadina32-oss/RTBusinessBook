package com.riotech.rtbusinessbook.data

import androidx.room.Entity
import androidx.room.PrimaryKey

const val TYPE_INCOME = "INCOME"
const val TYPE_EXPENSE = "EXPENSE"

/** Amount paisa (minor units) mein store hota hai taake decimal ghalti na ho. 100 paisa = Rs 1 */
@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val amountMinor: Long,
    val category: String,
    val description: String,
    val paymentMethod: String,
    val dateMillis: Long,
    val createdAt: Long,
    val updatedAt: Long,
    val isDeleted: Boolean = false
)

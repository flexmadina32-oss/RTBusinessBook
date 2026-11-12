@file:OptIn(ExperimentalMaterial3Api::class)

package com.riotech.rtbusinessbook.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.riotech.rtbusinessbook.data.TYPE_EXPENSE
import com.riotech.rtbusinessbook.data.TYPE_INCOME
import com.riotech.rtbusinessbook.data.TransactionEntity
import com.riotech.rtbusinessbook.util.Fmt

data class FormRequest(val type: String, val existing: TransactionEntity?)

private val incomeCategories = listOf("Sales", "Service", "Commission", "Other Income")
private val expenseCategories = listOf("Rent", "Salary", "Electricity", "Transport", "Purchase", "Other Expense")
private val methods = listOf("Cash", "Bank", "JazzCash", "Easypaisa")

@Composable
fun RtApp(vm: MainViewModel) {
    val all by vm.active.collectAsState()
    val deleted by vm.deleted.collectAsState()
    var tab by remember { mutableIntStateOf(0) }
    var form by remember { mutableStateOf<FormRequest?>(null) }

    val req = form
    if (req != null) {
        BackHandler { form = null }
        FormScreen(
            req = req,
            onCancel = { form = null },
            onSave = { amt, cat, desc, method, date ->
                vm.save(req.existing, req.type, amt, cat, desc, method, date)
                form = null
            }
        )
        return
    }

    Scaffold(
        topBar = {
            Row(
                Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary)
                    .statusBarsPadding().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("RT Business Book", color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Rio Tech AI", color = RtYellow, fontSize = 12.sp)
                }
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(selected = tab == 0, onClick = { tab = 0 },
                    icon = { Icon(Icons.Default.Home, null) }, label = { Text("Dashboard") })
                NavigationBarItem(selected = tab == 1, onClick = { tab = 1 },
                    icon = { Icon(Icons.Default.Menu, null) }, label = { Text("History") })
                NavigationBarItem(selected = tab == 2, onClick = { tab = 2 },
                    icon = { Icon(Icons.Default.Delete, null) }, label = { Text("Recycle Bin") })
            }
        }
    ) { pad ->
        Box(Modifier.padding(pad).fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            when (tab) {
                0 -> Dashboard(all, onAdd = { form = FormRequest(it, null) })
                1 -> History(all, onEdit = { form = FormRequest(it.type, it) }, onDelete = { vm.delete(it.id) })
                else -> RecycleBin(deleted, onRestore = { vm.restore(it.id) }, onPurge = { vm.purge(it.id) })
            }
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(Modifier.padding(14.dp)) {
            Text(title, fontSize = 12.sp)
            Spacer(Modifier.height(4.dp))
            Text(value, color = color, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun Dashboard(all: List<TransactionEntity>, onAdd: (String) -> Unit) {
    fun sum(type: String, from: Long = Long.MIN_VALUE) =
        all.filter { it.type == type && it.dateMillis >= from }.sumOf { it.amountMinor }

    val totalIn = sum(TYPE_INCOME)
    val totalOut = sum(TYPE_EXPENSE)
    val today = Fmt.startOfToday()
    val month = Fmt.startOfMonth()
    val net = totalIn - totalOut

    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)) {
                Column(Modifier.fillMaxWidth().padding(18.dp)) {
                    Text("Net Profit / Loss", color = MaterialTheme.colorScheme.onPrimary)
                    Text(Fmt.money(net), color = if (net >= 0) RtYellow else Color(0xFFFFCDD2),
                        fontSize = 28.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("Total Income", Fmt.money(totalIn), IncomeGreen, Modifier.weight(1f))
                StatCard("Total Expenses", Fmt.money(totalOut), ExpenseRed, Modifier.weight(1f))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("This Month Income", Fmt.money(sum(TYPE_INCOME, month)), IncomeGreen, Modifier.weight(1f))
                StatCard("This Month Expenses", Fmt.money(sum(TYPE_EXPENSE, month)), ExpenseRed, Modifier.weight(1f))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("Today Income", Fmt.money(sum(TYPE_INCOME, today)), IncomeGreen, Modifier.weight(1f))
                StatCard("Today Expenses", Fmt.money(sum(TYPE_EXPENSE, today)), ExpenseRed, Modifier.weight(1f))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { onAdd(TYPE_INCOME) }, modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = IncomeGreen)) { Text("+ Add Income") }
                Button(onClick = { onAdd(TYPE_EXPENSE) }, modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)) { Text("+ Add Expense") }
            }
        }
        item { Text("Recent Transactions", fontWeight = FontWeight.Bold, fontSize = 16.sp) }
        if (all.isEmpty()) {
            item { Text("Abhi koi transaction nahi. Upar se Income ya Expense add karein.") }
        }
        items(all.take(5), key = { it.id }) { TxRow(it, null, null) }
    }
}

@Composable
private fun TxRow(t: TransactionEntity, onEdit: (() -> Unit)?, onDelete: (() -> Unit)?) {
    val isIn = t.type == TYPE_INCOME
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(t.category, fontWeight = FontWeight.Bold)
                if (t.description.isNotBlank()) Text(t.description, fontSize = 13.sp)
                Text("${Fmt.date(t.dateMillis)}  |  ${t.paymentMethod}", fontSize = 12.sp)
            }
            Text((if (isIn) "+" else "-") + Fmt.money(t.amountMinor),
                color = if (isIn) IncomeGreen else ExpenseRed, fontWeight = FontWeight.Bold)
            if (onEdit != null) IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, "Edit") }
            if (onDelete != null) IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Delete") }
        }
    }
}

@Composable
private fun History(all: List<TransactionEntity>, onEdit: (TransactionEntity) -> Unit,
                    onDelete: (TransactionEntity) -> Unit) {
    var filter by remember { mutableStateOf("ALL") }
    var toDelete by remember { mutableStateOf<TransactionEntity?>(null) }
    val shown = if (filter == "ALL") all else all.filter { it.type == filter }

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(filter == "ALL", { filter = "ALL" }, { Text("All") })
            FilterChip(filter == TYPE_INCOME, { filter = TYPE_INCOME }, { Text("Income") })
            FilterChip(filter == TYPE_EXPENSE, { filter = TYPE_EXPENSE }, { Text("Expense") })
        }
        if (shown.isEmpty()) Text("Koi record nahi mila.", Modifier.padding(16.dp))
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(shown, key = { it.id }) { t -> TxRow(t, { onEdit(t) }, { toDelete = t }) }
        }
    }
    toDelete?.let { t ->
        AlertDialog(
            onDismissRequest = { toDelete = null },
            title = { Text("Delete record?") },
            text = { Text("Ye record Recycle Bin mein chala jayega. Wahan se restore ho sakta hai.") },
            confirmButton = { TextButton(onClick = { onDelete(t); toDelete = null }) { Text("Delete") } },
            dismissButton = { TextButton(onClick = { toDelete = null }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun RecycleBin(deleted: List<TransactionEntity>, onRestore: (TransactionEntity) -> Unit,
                       onPurge: (TransactionEntity) -> Unit) {
    var toPurge by remember { mutableStateOf<TransactionEntity?>(null) }
    if (deleted.isEmpty()) {
        Text("Recycle Bin khali hai.", Modifier.padding(16.dp))
    }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(deleted, key = { it.id }) { t ->
            Column {
                TxRow(t, null, null)
                Row {
                    TextButton(onClick = { onRestore(t) }) { Text("Restore") }
                    TextButton(onClick = { toPurge = t }) { Text("Delete Forever", color = ExpenseRed) }
                }
            }
        }
    }
    toPurge?.let { t ->
        AlertDialog(
            onDismissRequest = { toPurge = null },
            title = { Text("Permanently delete?") },
            text = { Text("Ye record hamesha ke liye khatam ho jayega. Wapas nahi aayega.") },
            confirmButton = { TextButton(onClick = { onPurge(t); toPurge = null }) { Text("Delete Forever") } },
            dismissButton = { TextButton(onClick = { toPurge = null }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun FormScreen(
    req: FormRequest,
    onCancel: () -> Unit,
    onSave: (Long, String, String, String, Long) -> Unit
) {
    val ex = req.existing
    val isIncome = req.type == TYPE_INCOME
    val cats = if (isIncome) incomeCategories else expenseCategories

    var amount by remember { mutableStateOf(ex?.let { Fmt.minorToText(it.amountMinor) } ?: "") }
    var category by remember { mutableStateOf(ex?.category ?: cats.first()) }
    var desc by remember { mutableStateOf(ex?.description ?: "") }
    var method by remember { mutableStateOf(ex?.paymentMethod ?: methods.first()) }
    var date by remember { mutableStateOf(Fmt.date(ex?.dateMillis ?: System.currentTimeMillis())) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            .statusBarsPadding().imePadding().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            (if (ex == null) "Add " else "Edit ") + (if (isIncome) "Income" else "Expense"),
            fontSize = 22.sp, fontWeight = FontWeight.Bold,
            color = if (isIncome) IncomeGreen else ExpenseRed
        )
        OutlinedTextField(amount, { amount = it }, label = { Text("Amount (PKR)") },
            singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(category, { category = it }, label = { Text("Category") },
            singleLine = true, modifier = Modifier.fillMaxWidth())
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(cats) { c -> FilterChip(category == c, { category = c }, { Text(c) }) }
        }
        OutlinedTextField(desc, { desc = it }, label = { Text("Description (optional)") },
            modifier = Modifier.fillMaxWidth())
        Text("Payment Method")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(methods) { m -> FilterChip(method == m, { method = m }, { Text(m) }) }
        }
        OutlinedTextField(date, { date = it }, label = { Text("Date (dd/MM/yyyy)") },
            singleLine = true, modifier = Modifier.fillMaxWidth())
        error?.let { Text(it, color = ExpenseRed) }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) { Text("Cancel") }
            Button(
                onClick = {
                    val amt = Fmt.parseAmount(amount)
                    val dt = Fmt.parseDate(date)
                    when {
                        amt == null -> error = "Sahi amount likhein (0 se zyada)."
                        category.isBlank() -> error = "Category likhein."
                        dt == null -> error = "Date format dd/MM/yyyy mein likhein, e.g. 25/12/2025"
                        else -> onSave(amt, category.trim(), desc.trim(), method, dt)
                    }
                },
                modifier = Modifier.weight(1f)
            ) { Text("Save") }
        }
    }
}

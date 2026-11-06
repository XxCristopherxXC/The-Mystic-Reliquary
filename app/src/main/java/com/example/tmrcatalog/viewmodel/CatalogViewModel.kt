package com.example.tmrcatalog.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.tmrcatalog.R
import com.example.tmrcatalog.model.TmrProduct
import com.example.tmrcatalog.model.TmrRecord
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CatalogViewModel : ViewModel() {
    private val _productList = mutableStateListOf<TmrProduct>()
    val productList: List<TmrProduct> get() = _productList

    private var nextId = 1
    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    init {
        addProduct(
            code = "0668211",
            name = "Shampoo Limpieza Renovadora 375 ml",
            price = 4000.0,
            category = "Shampoo",
            imageSource = R.drawable.img2,
            initialQuantity = 5
        )
        addProduct(
            code = "0690897",
            name = "Shampoo Purificación Capilar Carbón 375 ml",
            price = 4690.0,
            category = "Shampoo",
            imageSource = R.drawable.img,
            initialQuantity = 3
        )
        addProduct(
            code = "5057960",
            name = "Shampoo Anti-Comezón Menta 375 ml",
            price = 4000.0,
            category = "Shampoo",
            imageSource = R.drawable.img3,
            initialQuantity = 10
        )
    }

    fun addProduct(
        code: String,
        name: String,
        price: Double,
        category: String,
        imageSource: Any?,
        initialQuantity: Int = 1
    ) {
        if (code.isNotBlank() && name.isNotBlank()) {
            _productList.add(
                TmrProduct(
                    id = nextId++,
                    code = code,
                    name = name,
                    price = price,
                    category = category,
                    imageSource = imageSource ?: R.drawable.img,
                    quantity = initialQuantity
                )
            )
        }
    }

    fun increaseQuantity(product: TmrProduct) {
        val index = _productList.indexOf(product)
        if (index != -1) {
            _productList[index] = _productList[index].copy(quantity = _productList[index].quantity + 1)
        }
    }

    fun decreaseQuantity(product: TmrProduct) {
        val index = _productList.indexOf(product)
        if (index != -1) {
            val currentQty = _productList[index].quantity
            if (currentQty > 0) {
                _productList[index] = _productList[index].copy(quantity = currentQty - 1)
            }
        }
    }

    fun removeProduct(product: TmrProduct) {
        _productList.remove(product)
    }

    // Guarda el registro en la hora especificada para el día de hoy y limpia registros viejos
    fun saveProductAtHour(product: TmrProduct, hourSlot: String) {
        val todayStr = LocalDate.now().format(dateFormatter)
        val index = _productList.indexOf(product)

        if (index != -1) {
            val currentProduct = _productList[index]
            val records = currentProduct.recordsByDate.toMutableMap()

            // Obtener o crear el registro para el día de hoy
            val todayRecord = records.getOrPut(todayStr) { TmrRecord(date = todayStr) }

            // Actualizar el horario específico
            when (hourSlot) {
                "12:00" -> todayRecord.qty12 = currentProduct.quantity
                "15:00" -> todayRecord.qty15 = currentProduct.quantity
                "18:00" -> todayRecord.qty18 = currentProduct.quantity
            }

            // Mantiene actualizado el mapa con la nueva información
            records[todayStr] = todayRecord

            // LIMPIEZA AUTOMÁTICA: Elimina registros con antigüedad superior a 12 días
            cleanOldRecords(records)

            // Reemplazar estado para refrescar UI
            _productList[index] = currentProduct.copy(recordsByDate = records)
        }
    }

    private fun cleanOldRecords(records: MutableMap<String, TmrRecord>) {
        val today = LocalDate.now()
        val keysToRemove = records.keys.filter { dateStr ->
            try {
                val recordDate = LocalDate.parse(dateStr, dateFormatter)
                // Se conservan los últimos 12 días
                recordDate.isBefore(today.minusDays(12))
            } catch (e: Exception) {
                false
            }
        }
        keysToRemove.forEach { records.remove(it) }
    }

    // Obtener la lista de fechas registradas (ordenadas de más reciente a más antigua)
    fun getAvailableDates(): List<String> {
        val allDates = mutableSetOf<String>()
        _productList.forEach { product ->
            allDates.addAll(product.recordsByDate.keys)
        }
        return allDates.sortedByDescending {
            try { LocalDate.parse(it, dateFormatter) } catch (e: Exception) { LocalDate.MIN }
        }
    }
}
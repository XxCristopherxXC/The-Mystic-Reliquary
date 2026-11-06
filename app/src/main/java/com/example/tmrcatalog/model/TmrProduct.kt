package com.example.tmrcatalog.model

data class TmrRecord(
    val date: String,
    var qty12: Int? = null,
    var qty15: Int? = null,
    var qty18: Int? = null
)

data class TmrProduct(
    val id: Int,
    val code: String,
    val name: String,
    val price: Double,
    val category: String,
    val imageSource: Any?,
    var quantity: Int = 1,
    val recordsByDate: MutableMap<String, TmrRecord> = mutableMapOf()
)
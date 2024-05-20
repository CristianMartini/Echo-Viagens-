package com.example.echoviagens

data class OrderRequest(
    val userId: Int,
    val total: Double,
    val products: List<Produto>
)

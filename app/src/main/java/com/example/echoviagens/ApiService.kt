package com.example.echoviagens

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("/")
    fun getProdutos(): Call<List<Produto>>

}


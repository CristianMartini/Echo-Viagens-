package com.example.echoviagens

import Produto
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Query

interface CartApiService {
    @GET("/getCartItems")
    fun getCartItems(@Query("userId") userId: Int): Call<List<Produto>>

    @DELETE("/deleteCartItem")
    fun deleteCartItem(@Query("produtoId") produtoId: Int, @Query("userId") userId: Int): Call<Void>
}
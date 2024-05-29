package com.example.echoviagens



import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query

interface CartApiService {
    @GET("/getCartItems")
    fun getCartItems(@Query("userId") userId: Int): Call<List<Produto>>

    // Substituindo o método DELETE por um PUT para atualizar a quantidade do item para zero

        @PUT("/updateCartItemQuantity")
        fun updateCartItemQuantity(@Body updateRequest: CartItemUpdateRequest): Call<Void>


    data class CartItemUpdateRequest(
        val userId: Int,
        val produtoId: Int,
        val quantidade: Int
    )
}

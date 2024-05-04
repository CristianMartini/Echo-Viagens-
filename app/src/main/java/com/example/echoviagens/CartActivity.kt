package com.example.echoviagens

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
class CartActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var totalTextView: TextView
    private lateinit var goToPaymentButton: Button
    private var total: Double = 0.0
    private var userId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        recyclerView = findViewById(R.id.cartRecyclerView)
        totalTextView = findViewById(R.id.totalTextView)
        goToPaymentButton = findViewById(R.id.goToPaymentButton)

        // Obtenha userId das preferências compartilhadas do login aqui
        val sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE)
        userId = sharedPreferences.getInt("userId", 0)

        recyclerView.layoutManager = LinearLayoutManager(this)
        fetchCartItems()

        goToPaymentButton.setOnClickListener {
            // Ir para tela de pagamento enviando os dados
        }
    }

    private fun fetchCartItems() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://c0c74366-09d0-4c14-a004-0805ee76eefe-00-3p5n3xcfdp0te.janeway.repl.co/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(CartApiService::class.java)

        api.getCartItems(userId = userId).enqueue(object : Callback<List<Produto>> {
            override fun onResponse(call: Call<List<Produto>>, response: Response<List<Produto>>) {
                if (response.isSuccessful) {
                    val cartItems = response.body()?.toMutableList() ?: mutableListOf()
                    recyclerView.adapter = CartAdapter(cartItems, this@CartActivity, userId) {
                        total = cartItems.sumOf { it.produtoPreco.toDouble() * it.quantidadeDisponivel }
                        totalTextView.text = "Total: R$${String.format("%.2f", total)}"
                    }
                }
            }

            override fun onFailure(call: Call<List<Produto>>, t: Throwable) {
                // Tratamento de falhas
            }
        })
    }
}

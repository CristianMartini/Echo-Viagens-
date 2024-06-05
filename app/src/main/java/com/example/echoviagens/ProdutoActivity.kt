package com.example.echoviagens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ProdutoActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CustomAdapter
    private lateinit var searchView: SearchView
    private lateinit var userNameTextView: TextView
    private var allProducts: List<Produto> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_produto_video)  // Certifique-se de que o nome do layout está correto

        recyclerView = findViewById(R.id.recyclerViewProdutos)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapter = CustomAdapter(allProducts)
        recyclerView.adapter = adapter

        val sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", "Usuário Desconhecido")

        userNameTextView = findViewById(R.id.textView2)
        userNameTextView.text = userName

        searchView = findViewById(R.id.searchView)

        val goToHome = findViewById<LinearLayout>(R.id.goToHome2)
        val goToCart = findViewById<LinearLayout>(R.id.goToCart2)

        goToHome.setOnClickListener {
            val homeIntent = Intent(this, ProdutoActivity::class.java)
            startActivity(homeIntent)
        }

        goToCart.setOnClickListener {
            val cartIntent = Intent(this, CartActivity::class.java)
            startActivity(cartIntent)
        }

        val logging = HttpLoggingInterceptor { message -> Log.d("OkHttp", message) }
            .apply { level = HttpLoggingInterceptor.Level.BODY }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://2c7bfeed-a130-43f9-ad11-0507bb90574b-00-1eihxskz2uzjp.kirk.replit.dev/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        fetchAllProducts(apiService)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { searchTerm ->
                    val filteredProducts = allProducts.filter { produto ->
                        produto.produtoNome.contains(searchTerm, ignoreCase = true)
                    }
                    adapter.updateData(filteredProducts)
                }
                return true
            }
        })
    }

    private fun fetchAllProducts(apiService: ApiService) {
        apiService.getProdutos().enqueue(object : Callback<List<Produto>> {
            override fun onResponse(call: Call<List<Produto>>, response: Response<List<Produto>>) {
                if (response.isSuccessful) {
                    allProducts = response.body() ?: emptyList()
                    adapter.updateData(allProducts)
                } else {
                    Log.e("API Error", "Response not successful. Code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Produto>>, t: Throwable) {
                Log.e("API Failure", "Error fetching products", t)
            }
        })
    }
}

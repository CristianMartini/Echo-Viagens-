package com.example.echoviagens

import android.os.Bundle
import android.util.Log
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
    private lateinit var searchView: androidx.appcompat.widget.SearchView
    private lateinit var allProducts: List<Produto>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_produtos)

        recyclerView = findViewById(R.id.recyclerViewProdutos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        searchView = findViewById(R.id.searchView)

        // Configuração do Logging Interceptor
        val logging = HttpLoggingInterceptor { message ->
            Log.d("OkHttp", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // Configuração do OkHttpClient com o interceptor
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        // Configuração do Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://2c7bfeed-a130-43f9-ad11-0507bb90574b-00-1eihxskz2uzjp.kirk.repl.co/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        // Método para buscar todos os produtos
        fun fetchAllProducts() {
            apiService.getProdutos().enqueue(object : Callback<List<Produto>> {
                override fun onResponse(call: Call<List<Produto>>, response: Response<List<Produto>>) {
                    if (response.isSuccessful) {
                        allProducts = response.body() ?: emptyList()
                        adapter = CustomAdapter(allProducts)
                        recyclerView.adapter = adapter
                    } else {
                        Log.e("API Error", "Response not successful. Code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<List<Produto>>, t: Throwable) {
                    Log.e("API Failure", "Error fetching products", t)
                }
            })
        }

        // Buscar todos os produtos inicialmente
        fetchAllProducts()

        // Configuração da SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Não é necessário implementar nada aqui
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Filtrar produtos com base no termo de busca
                newText?.let { searchTerm ->
                    if (searchTerm.isNotEmpty()) {
                        val filteredProducts = allProducts.filter { produto ->
                            produto.produtoNome.contains(searchTerm, ignoreCase = true)
                        }
                        adapter = CustomAdapter(filteredProducts)
                        recyclerView.adapter = adapter
                    } else {
                        // Se o termo de busca estiver vazio, mostrar todos os produtos novamente
                        adapter = CustomAdapter(allProducts)
                        recyclerView.adapter = adapter
                    }
                }
                return true
            }
        })
    }
}

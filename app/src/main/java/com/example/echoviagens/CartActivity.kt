package com.example.echoviagens
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
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
    private lateinit var goToHomeLayout: LinearLayout  // Alterado para LinearLayout
    private var total: Double = 0.0
    private lateinit var cartAdapter: CartAdapter
    private var items: MutableList<Produto> = mutableListOf()
    private var userId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.carrinho)

        recyclerView = findViewById(R.id.recyclerviewCarrinho)
        totalTextView = findViewById(R.id.totalCarrinho)
        goToPaymentButton = findViewById(R.id.btnPayment)
        goToHomeLayout = findViewById(R.id.goToHome2) // Alterado para referenciar LinearLayout

        recyclerView.layoutManager = LinearLayoutManager(this)
        userId = getSharedPreferences("Login", Context.MODE_PRIVATE).getInt("userId", 0)
        fetchCartItems()

        goToPaymentButton.setOnClickListener {
            val intent = Intent(this, PaymentActivity::class.java).apply {
                putExtra("TOTAL", total.toString())
                putExtra("USER", userId)
                putParcelableArrayListExtra("PRODUCT_LIST", ArrayList(items))
            }
            startActivity(intent)
        }

        goToHomeLayout.setOnClickListener {
            val intent = Intent(this, ProdutoActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchCartItems() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://16b33afa-15e5-4117-8d4a-f602c22a619b-00-2sdbx4eltghf5.worf.replit.dev/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(CartApiService::class.java)

        api.getCartItems(userId = userId).enqueue(object : Callback<List<Produto>> {
            override fun onResponse(call: Call<List<Produto>>, response: Response<List<Produto>>) {
                if (response.isSuccessful && response.body() != null) {
                    items = response.body()!!.toMutableList()
                    setupAdapter()
                    updateTotal()
                } else {
                    Log.e("API Error", "Response not successful. Code: ${response.code()}, Message: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Produto>>, t: Throwable) {
                Log.e("API Failure", "Error fetching products: ${t.message}")
            }
        })
    }

    private fun setupAdapter() {
        cartAdapter = CartAdapter(items, this) { updateTotal() }
        recyclerView.adapter = cartAdapter
    }

    fun updateTotal() {
        total = items.sumOf { it.produtoPreco.toDouble() * it.quantidadeDisponivel.toDouble() }
        runOnUiThread {
            totalTextView.text = "Total: R$${String.format("%.2f", total)}"
        }
    }
}

package com.example.echoviagens

   import android.os.Bundle
    import android.widget.Button
    import android.widget.EditText
    import android.widget.Toast
    import androidx.appcompat.app.AppCompatActivity
    import retrofit2.Retrofit
    import retrofit2.converter.gson.GsonConverterFactory
    import retrofit2.Call
    import retrofit2.Callback
    import retrofit2.Response
   import retrofit2.http.Body
   import retrofit2.http.Field
    import retrofit2.http.POST

    class PaymentActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_Payment)

            val totalValue = intent.getDoubleExtra("TOTAL", 0.0)
            val userId = intent.getIntExtra("USER", 0)
            val productList = intent.getParcelableArrayListExtra<Produto>("PRODUCT_LIST")

            val cardNumberInput: EditText = findViewById(R.id.cardNumberInput)
            val cardExpirationInput: EditText = findViewById(R.id.cardExpirationInput)
            val cardCVCInput: EditText = findViewById(R.id.cardCVCInput)
            val finishPaymentButton: Button = findViewById(R.id.finishPaymentButton)

            finishPaymentButton.setOnClickListener {
                if (validateCardDetails(cardNumberInput.text.toString(), cardExpirationInput.text.toString(), cardCVCInput.text.toString())) {
                    enviaOrdem(userId, totalValue, productList)
                } else {
                    Toast.makeText(this, "Detalhes do cartÃ£o invÃ¡lidos", Toast.LENGTH_LONG).show()
                }
            }
        }

        private fun enviaOrdem(userId: Int, total: Double, products: ArrayList<Produto>?) {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://ecd95a86-6946-4009-9df5-47e6d47e547a-00-31rxlog0zs3rm.riker.replit.dev/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val api = retrofit.create(OrderApiService::class.java)
            val call = api.createOrder(userId, total, products!!)
            call.enqueue(object : Callback<ResponseCompra> {
                override fun onResponse(call: Call<ResponseCompra>, response: Response<ResponseCompra>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@PaymentActivity, "Pedido realizado com sucesso", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@PaymentActivity, "Erro ao realizar pedido", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ResponseCompra>, t: Throwable) {
                    Toast.makeText(this@PaymentActivity, "Falha na conexÃ£o", Toast.LENGTH_LONG).show()
                }
            })
        }

        interface OrderApiService {
            @POST("/")
            fun createOrder(@Field("userId") userId: Int, @Field("total") total: Double, @Body products: List<Produto>): Call<ResponseCompra>
        }
    }


}
package com.example.echoviagens
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.echoviagens.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

class ProdutoDetalhes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_produto_detalhes)
        val nomeProduto = intent.getStringExtra("NOME_PRODUTO")
        val descricaoProduto = intent.getStringExtra("DESCRICAO_PRODUTO")
        val imagemProduto = intent.getStringExtra("IMAGEM_PRODUTO")

        val produtoId = intent.getIntExtra("ID_PRODUTO", 0)
        val quantidadeDisponivel = intent.getIntExtra("QUANTIDADE_DISPONIVEL", 0)


        // Carregar imagem usando Glide
        Glide.with(this)
            .load(imagemProduto)
            .placeholder(R.drawable.ic_launcher_background) // Imagem de placeholder enquanto carrega
            .error(com.google.android.material.R.drawable.mtrl_ic_error) // Imagem de erro, caso ocorra
            .into(findViewById<ImageView>(R.id.imagem_produto))

        findViewById<TextView>(R.id.txtNomeProduto).text = nomeProduto
        findViewById<TextView>(R.id.txtDescricaoProduto).text = descricaoProduto
        findViewById<TextView>(R.id.txtQuantidadeDisponivel).text = quantidadeDisponivel.toString()


        val editTextQuantidade = findViewById<EditText>(R.id.editQuantidadeDesejada)
        val btnAdicionarCarrinho = findViewById<Button>(R.id.btnAdicionarAoCarrinho)

        val sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("userId", 0)

        btnAdicionarCarrinho.setOnClickListener {
            val quantidadeDesejada = editTextQuantidade.text.toString().toIntOrNull() ?: 0
            adicionarAoCarrinho(userId, produtoId, quantidadeDesejada)
        }
        // Botão Carrinho
        val btnCarrinho = findViewById<Button>(R.id.btnCarrinho)
        btnCarrinho.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun adicionarAoCarrinho(userId: Int, produtoId: Int, quantidade: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://e12fee2b-97e1-4da1-8e95-f7aca08c0ffe-00-tfei85kuwe7y.picard.repl.co/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService::class.java)
        api.adicionarAoCarrinho(userId, produtoId, quantidade).enqueue(object :
            Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProdutoDetalhes, response.body() ?: "Sucesso!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ProdutoDetalhes, "Resposta não bem-sucedida", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(this@ProdutoDetalhes, "Erro na API: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    interface ApiService {
        @FormUrlEncoded
        @POST("/")
        fun adicionarAoCarrinho(
            @Field("userId") userId: Int,
            @Field("produtoId") produtoId: Int,
            @Field("quantidade") quantidade: Int
        ): Call<String>
    }
}

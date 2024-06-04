package com.example.echoviagens




import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
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
        setContentView(R.layout.detalhe_produto)

        // Recebendo dados da Intent
        val nomeProduto = intent.getStringExtra("NOME_PRODUTO") ?: ""
        val precoProduto = intent.getStringExtra("PRODUTO_PRECO") ?: "R$ 0,00"
        val descricaoProduto = intent.getStringExtra("DESCRICAO_PRODUTO") ?: ""
        val imagemProduto = intent.getStringExtra("IMAGEM_PRODUTO") ?: ""
        val produtoId = intent.getIntExtra("ID_PRODUTO", 0)
        val quantidadeDisponivel = intent.getIntExtra("QUANTIDADE_DISPONIVEL", 0)

        // Carregando imagem do produto com Glide
        Glide.with(this)
            .load(imagemProduto)
            .placeholder(R.drawable.ic_launcher_background)
            .error(com.google.android.material.R.drawable.mtrl_ic_error)
            .into(findViewById<ImageView>(R.id.imagem_produto))

        // Definindo os textos nas Views
        findViewById<TextView>(R.id.txtNomeProduto).text = nomeProduto
        findViewById<TextView>(R.id.produto_preco).text = "R$ $precoProduto" // Exibindo o preço
        findViewById<TextView>(R.id.txtDescricaoProduto).text = descricaoProduto
        findViewById<TextView>(R.id.txtQuantidadeDisponivel).text = quantidadeDisponivel.toString()

        val editTextQuantidade = findViewById<TextView>(R.id.editQuantidadeDesejada)
        editTextQuantidade.text = "0"  // Set initial quantity to 0

        val btnIncrease = findViewById<TextView>(R.id.btnIncrease)
        val btnDecrease = findViewById<TextView>(R.id.btnDecrease)

        // Incrementa a quantidade
        btnIncrease.setOnClickListener {
            var quantidade = editTextQuantidade.text.toString().toInt()
            if (quantidade < quantidadeDisponivel) {
                quantidade++
                editTextQuantidade.text = quantidade.toString()
            }
        }

        // Decrementa a quantidade
        btnDecrease.setOnClickListener {
            var quantidade = editTextQuantidade.text.toString().toInt()
            if (quantidade > 0) {
                quantidade--
                editTextQuantidade.text = quantidade.toString()
            }
        }
        val btnAdicionarCarrinho = findViewById<Button>(R.id.btnAdicionarAoCarrinho)
        btnAdicionarCarrinho.setOnClickListener {
            val quantidadeDesejada = findViewById<TextView>(R.id.editQuantidadeDesejada).text.toString().toIntOrNull() ?: 0
            if (quantidadeDesejada > 0) {
                adicionarAoCarrinho(quantidadeDesejada)
            } else {
                Toast.makeText(this, "Quantidade inválida.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun adicionarAoCarrinho(quantidade: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://e12fee2b-97e1-4da1-8e95-f7aca08c0ffe-00-tfei85kuwe7y.picard.replit.dev/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService::class.java)
        val userId = getSharedPreferences("Login", Context.MODE_PRIVATE).getInt("userId", 0)
        val produtoId = intent.getIntExtra("ID_PRODUTO", 0)

        api.adicionarAoCarrinho(userId, produtoId, quantidade).enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful && response.body()?.contains("Sucesso!") == true) {
                    Toast.makeText(this@ProdutoDetalhes, "Adicionado com sucesso", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@ProdutoDetalhes, CartActivity::class.java))
                } else {
                    Toast.makeText(this@ProdutoDetalhes, "Falha ao adicionar: ${response.body()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(this@ProdutoDetalhes, "Erro na API: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }

    interface ApiService {
        @FormUrlEncoded
        @POST("caminho/para/sua/api")
        fun adicionarAoCarrinho(
            @Field("userId") userId: Int,
            @Field("produtoId") produtoId: Int,
            @Field("quantidade") quantidade: Int
        ): Call<String>
    }
}
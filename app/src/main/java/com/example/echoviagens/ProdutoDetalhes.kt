package com.example.echoviagens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class ProdutoDetalhes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_produto_detalhes)
        val nomeProduto = intent.getStringExtra("NOME_PRODUTO")
        val descricaoProduto = intent.getStringExtra("DESCRICAO_PRODUTO")

        findViewById<TextView>(R.id.txtNomeProduto).text = nomeProduto
        findViewById<TextView>(R.id.txtDescricaoProduto).text = descricaoProduto

        findViewById<Button>(R.id.btnAdicionarAoCarrinho).setOnClickListener {
            // TODO ADICIONAR AO CARRINHO
        }
    }
}
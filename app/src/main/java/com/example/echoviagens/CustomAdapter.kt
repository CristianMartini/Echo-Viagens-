package com.example.echoviagens


import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CustomAdapter(private var dataSet: List<Produto>) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    fun updateData(newData: List<Produto>) {
        dataSet = newData
        notifyDataSetChanged()  // Notifica o RecyclerView que os dados foram atualizados
    }
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nome: TextView = view.findViewById(R.id.nomeProduto)

        val valor: TextView = view.findViewById(R.id.valorProduto)
        val imagem: ImageView = view.findViewById(R.id.imagem_produto)

        val btnComprar: Button = view.findViewById(R.id.btn_Comprar)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.produto_recyclerview, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val produto = dataSet[position]

        viewHolder.nome.text = produto.produtoNome

        viewHolder.valor.text = ("R$" + produto.produtoPreco.toString());

        Glide.with(viewHolder.itemView.context)
            .load(produto.imagemUrl)
            .placeholder(R.drawable.ic_launcher_background) // placeholder
            .into(viewHolder.imagem)

        viewHolder.btnComprar.setOnClickListener {
            val intent = Intent(viewHolder.itemView.context, ProdutoDetalhes::class.java)
            intent.putExtra("ID_PRODUTO", produto.produtoId)
            intent.putExtra("NOME_PRODUTO", produto.produtoNome)
            intent.putExtra("IMAGEM_PRODUTO", produto.imagemUrl)
            intent.putExtra("DESCRICAO_PRODUTO", produto.produtoDesc)
            intent.putExtra("PRODUTO_PRECO", String.format("%.2f", produto.produtoPreco))  // Garantindo que o preço é passado como String formatada
            intent.putExtra("QUANTIDADE_DISPONIVEL", produto.quantidadeDisponivel)

            viewHolder.itemView.context.startActivity(intent)
        }



    }

    override fun getItemCount() = dataSet.size
}
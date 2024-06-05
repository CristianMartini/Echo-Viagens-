package com.example.echoviagens



import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
class CartAdapter(private val items: MutableList<Produto>,
                  private val context: Context,
                  private val updateTotal: () -> Unit) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productName: TextView = view.findViewById(R.id.produtoNome)
        val productPrice: TextView = view.findViewById(R.id.preçoTotal)
        val productQuantity: TextView = view.findViewById(R.id.qtdProduto)
        val productImage: ImageView = view.findViewById(R.id.produtoImagem)
        val deleteButton: LinearLayout= view.findViewById(R.id.botao_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_carrinho, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.productName.text = item.produtoNome
        holder.productPrice.text = item.produtoPreco?.let { String.format("R$%.2f", it.toDouble()) }
        holder.productQuantity.text = "${item.quantidadeDisponivel}"
        Glide.with(context).load(item.imagemUrl).into(holder.productImage)

        holder.deleteButton.setOnClickListener {
            removeItemFromCart(item, position)
        }
    }

    private fun removeItemFromCart(item: Produto, position: Int) {
        val retrofit = getRetrofit()
        val api = retrofit.create(CartApiService::class.java)

        val sharedPreferences = context.getSharedPreferences("Login", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("userId", 0)

        val updateRequest = CartApiService.CartItemUpdateRequest(userId, item.produtoId, 0)

        api.updateCartItemQuantity(updateRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    items.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, items.size)
                    updateTotal()
                    Toast.makeText(context, "Item removido com sucesso", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Falha ao atualizar o carrinho: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Erro de conexão: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }



    private fun getRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("https://16b33afa-15e5-4117-8d4a-f602c22a619b-00-2sdbx4eltghf5.worf.replit.dev/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    override fun getItemCount() = items.size
}

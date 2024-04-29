package com.example.echoviagens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.util.Log
import retrofit2.http.GET
import retrofit2.http.Query

class LoginActivity<SharedPreferences> : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailEditText = findViewById(R.id.txtEmail)
        passwordEditText = findViewById(R.id.editTextTextPassword5)
        val loginButton: Button = findViewById(R.id.btnLogin)

        val sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE)

        loginButton.setOnClickListener {
            blockLogin(sharedPreferences)
        }
    }

    private fun blockLogin(sharedPreferences: android.content.SharedPreferences) {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://71470dd5-14c8-4ec4-960e-a81bd29d91ad-00-sk7g2syvbseo.janeway.replit.dev/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        val call = apiService.login(email, password)
        call.enqueue(object : Callback<List<LoginResponse>> {
            override fun onResponse(
                call: Call<List<LoginResponse>>,
                response: Response<List<LoginResponse>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val loginResponses = response.body()!!
                    if (loginResponses.isNotEmpty()) {
                        val userId = loginResponses[0].USUARIO_ID
                        // Salvar o ID do usuário no SharedPreferences
                        val editor = sharedPreferences.edit()
                        editor.putInt("userId", userId)
                        editor.apply()

                        val intent = Intent(this@LoginActivity, ProdutoActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Usuario ou senha invalidos",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Log.e(
                        "LoginActivity",
                        "Login failed: HTTP error code: " + response.code() + " msg: " + response.message()
                    )
                    Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<LoginResponse>>, t: Throwable) {
                Log.e("LoginActivity", "onFailure: " + t.message)
                Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    interface ApiService {
        @GET("login")
        fun login(
            @Query("usuario") usuario: String,
            @Query("senha") senha: String
        ): Call<List<LoginResponse>>
    }
}

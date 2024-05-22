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

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailEditText = findViewById(R.id.txtEmail)
        passwordEditText = findViewById(R.id.editTextTextPassword5)
        loginButton = findViewById(R.id.btnLogin)

        val sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                performLogin(email, password, sharedPreferences)
            } else {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performLogin(email: String, password: String, sharedPreferences: android.content.SharedPreferences) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://fc785e2e-499f-48b1-aea1-dd10e069c099-00-14j9788ktdjj6.spock.repl.co/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.login(email, password)

        call.enqueue(object : Callback<List<LoginResponse>> {
            override fun onResponse(call: Call<List<LoginResponse>>, response: Response<List<LoginResponse>>) {
                if (response.isSuccessful && response.body() != null) {
                    val loginResponses = response.body()!!
                    if (loginResponses.isNotEmpty()) {
                        val userId = loginResponses[0].USUARIO_ID
                        sharedPreferences.edit().putInt("userId", userId).apply()

                        val intent = Intent(this@LoginActivity, ProdutoActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Invalid username or password", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Log.e("LoginActivity", "Login failed: HTTP error code: " + response.code())
                    Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<LoginResponse>>, t: Throwable) {
                Log.e("LoginActivity", "onFailure: " + t.message)
                Toast.makeText(this@LoginActivity, "Login error: ${t.message}", Toast.LENGTH_LONG).show()
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


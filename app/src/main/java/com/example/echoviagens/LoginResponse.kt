package com.example.echoviagens

data class LoginResponse(
    val USUARIO_ID: Int,
    val USUARIO_NOME: String,
    val USUARIO_EMAIL: String,
    val USUARIO_SENHA: String,
    val USUARIO_CPF: String)
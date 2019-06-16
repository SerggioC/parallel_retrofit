package com.sergiocruz.parallelretrofit.model


import com.google.gson.annotations.SerializedName

data class AppleAuthKeys(
    @SerializedName("keys")
    val keys: List<Key>
)

data class Key(
    @SerializedName("alg")
    val alg: String,
    @SerializedName("e")
    val e: String,
    @SerializedName("kid")
    val kid: String,
    @SerializedName("kty")
    val kty: String,
    @SerializedName("n")
    val n: String,
    @SerializedName("use")
    val use: String
)

//alg
//string
//The encryption algorithm used to encrypt the token.
//
//e
//string
//The exponent value for the RSA public key.
//
//kid
//string
//A 10-character identifier key, obtained from your developer account.
//
//kty
//string
//The key type parameter setting. This must be set to "RSA".
//
//n
//string
//The modulus value for the RSA public key.
//
//use
//string
//The
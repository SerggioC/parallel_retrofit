package com.sergiocruz.parallelretrofit.api

import com.sergiocruz.parallelretrofit.model.Post
import com.sergiocruz.parallelretrofit.model.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AppAPI {

    @GET("/users")
    fun getUsers(): Call<List<User>>

    @GET("/users/{id}")
    fun getSingleUserById(@Path("id") id: Int): Call<List<User>>

    @GET("/posts/{id}")
    fun getPostByUserId(@Query("userId") id: Int): Call<List<Post>>

}
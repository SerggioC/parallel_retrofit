package com.sergiocruz.parallelretrofit.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sergiocruz.parallelretrofit.api.ApiController
import com.sergiocruz.parallelretrofit.model.Post
import com.sergiocruz.parallelretrofit.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PageViewModel : ViewModel() {

    private val _index = MutableLiveData<Int>()
    val text: LiveData<String> = Transformations.map(_index) {
        "Hello world from section: $it"
    }

    fun setIndex(index: Int) {
        _index.value = index
    }

    @Synchronized
    fun updateCounter() = ++counter

    @Volatile
    var counter = 0

    fun getUserFromAPI(callback: ((Int) -> Unit)?) {
        ApiController
            .controller
            .getUsers()
            .enqueue(object : Callback<List<User>?> {
                override fun onResponse(call: Call<List<User>?>, response: Response<List<User>?>) {
                    userList.value = response.body()
                    callback?.invoke(updateCounter())
                }

                override fun onFailure(call: Call<List<User>?>, t: Throwable) {
                    userList.value = null
                    callback?.invoke(updateCounter())
                }
            })
    }

    fun getUserFromAPISequentially(
        lista: List<Int>,
        index: Int,
        callback: ((Boolean, Int) -> Unit)?
    ) {
        ApiController
            .controller
            .getUsers()
            .enqueue(object : Callback<List<User>?> {
                override fun onResponse(call: Call<List<User>?>, response: Response<List<User>?>) {
                    if (index == lista.size) {
                        callback?.invoke(true, index)
                    } else {
                        callback?.invoke(false, index)
                        getUserFromAPISequentially(lista, index + 1, callback)
                    }
                }

                override fun onFailure(call: Call<List<User>?>, t: Throwable) {
                    Error("FUCK!")
                }
            })
    }


    private val userList = MutableLiveData<List<User>>()
    fun getUsers(): LiveData<List<User>> {
        getUserFromAPI(null)
        return userList
    }

    /*** Parallel execution ***/
    fun getPostsFromUserListParallel(
        userIdList: List<Int>,
        callback: ((Boolean, Int, List<Post>?) -> Unit)?
    ) {
        var counter = 0
        userIdList.forEachIndexed { userId, index ->
            getPostsFromUser(userId) {
                if (++counter == userIdList.size) {
                    callback?.invoke(true, index, it)
                } else {
                    callback?.invoke(false, index, it)
                }
            }
        }
    }

    fun getPostsFromUser(userId: Int, update: (List<Post>?) -> Unit) {

        ApiController.controller.getPostByUserId(userId).enqueue(object : Callback<List<Post>?> {
            override fun onResponse(call: Call<List<Post>?>, response: Response<List<Post>?>) {
                update(response.body())
            }

            override fun onFailure(call: Call<List<Post>?>, t: Throwable) {
                update(null)
            }
        })

    }

    /*** Sequential execution ***/
    fun getPostsFromUserListSequential(
        userIdList: List<Int>,
        index: Int,
        callback: ((Boolean, Int, List<Post>?) -> Unit)?
    ) {
        getPostsFromUser(userIdList.get(index)) {
            if (index + 1 == userIdList.size) {
                callback?.invoke(true, index + 1, it)
            } else {
                getPostsFromUserListSequential(userIdList, index + 1, callback)
                callback?.invoke(false, index + 1, it)
            }
        }


    }


}








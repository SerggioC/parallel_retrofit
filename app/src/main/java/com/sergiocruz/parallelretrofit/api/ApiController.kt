package com.sergiocruz.parallelretrofit.api

import android.annotation.SuppressLint
import android.content.Context
import androidx.room.*
import com.sergiocruz.parallelretrofit.model.AppleAuthKeys
import com.sergiocruz.parallelretrofit.model.Post
import com.sergiocruz.parallelretrofit.model.User
import okhttp3.ConnectionPool
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import retrofit2.http.Query


object ApiController {

    val dispatcher: Dispatcher by lazy { Dispatcher() }

    val controller: AppAPI by lazy {

        dispatcher.maxRequests = 64

        val okHttpClient = OkHttpClient
            .Builder()
            .dispatcher(dispatcher)
            .connectionPool(ConnectionPool())
            .build()

        Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(AppAPI::class.java)
    }

}


open class SingletonHolder<out T : Any, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator
    @Volatile
    private var instance: T? = null

    operator fun invoke(arg: A): T {
        val i = instance
        if (i != null) {
            return i
        }

        return synchronized(this) {
            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}

class ApiController2 private constructor(private val context: Context) {

    companion object : SingletonHolder<ApiController2, Context>(::ApiController2)

    fun getInstance(): AppAPI {
        return controller
    }

    private val baseUrl: String
        get() {

            // use the context in some way...
            val valor = context.getString(R.string.status_bar_notification_info_overflow)

            return if (valor == "999+") {
                "https://jsonplaceholder.typicode.com/"
            } else {
                "https://typicode.com/"
            }
        }

    private val dispatcher: Dispatcher by lazy { Dispatcher() }

    private val baseRetrofitBuilder: Retrofit.Builder by lazy {
        dispatcher.maxRequests = 64

        val okHttpClient = OkHttpClient
            .Builder()
            .dispatcher(dispatcher)
            .connectionPool(ConnectionPool())
            .build()

        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
    }


    private val controller: AppAPI
        get() {
            return baseRetrofitBuilder
                .baseUrl(baseUrl)
                .build()
                .create(AppAPI::class.java)
        }

}


interface AnotherAPIInterface {

    @GET("/users")
    fun getUsers(): Call<List<User>>

    @GET("/users/{id}")
    fun getSingleUserById(@Path("id") id: Int): Call<List<User>>

    @GET("/posts/{id}")
    fun getPostByUserId(@Query("userId") id: Int): Call<List<Post>>

}


object MultiApiMultiBaseUrlController {

    private lateinit var retrofit: Retrofit

    private val dispatcher: Dispatcher by lazy { Dispatcher() }

    private val client: OkHttpClient by lazy {
        dispatcher.maxRequests = 64
        OkHttpClient
            .Builder()
            .dispatcher(dispatcher)
            .connectionPool(ConnectionPool())
            .build()
    }

    fun withBaseUrl(baseUrl: String): MultiApiMultiBaseUrlController {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client).build()
        this.retrofit = retrofit
        return this
    }

    fun withSomeApiInterface(): AnotherAPIInterface {

        return retrofit.create(AnotherAPIInterface::class.java)
    }

    fun withAnotherApiInterface(): AppAPI {
        return retrofit.create(AppAPI::class.java)
    }

}

object AppleApiController {

    val dispatcher: Dispatcher by lazy { Dispatcher() }

    val controller: AppleAuthAPI by lazy {

        dispatcher.maxRequests = 4

        val okHttpClient = OkHttpClient
            .Builder()
            .dispatcher(dispatcher)
            .connectionPool(ConnectionPool())
            .build()

        Retrofit.Builder()
            .baseUrl("https://appleid.apple.com/auth/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(AppleAuthAPI::class.java)


    }

}

interface AppleAuthAPI {

    @GET("keys")
    fun getKeys(): Call<AppleAuthKeys>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("token")
    fun getToken(@Body id: Int): Call<List<User>>

}


class Manager private constructor(context: Context) {
    private lateinit var s: String

    init {
        // Init using context argument
        s = context.getString(R.string.status_bar_notification_info_overflow)
    }

    fun doStuff(): String {
        return s
    }

    companion object : SingletonHolder<Manager, Context>(::Manager)
}


@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class UsersDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object : SingletonHolder<UsersDatabase, Context>({
        Room.databaseBuilder(
            it.applicationContext,
            UsersDatabase::class.java, "Sample.db"
        ).build()
    })
}

@Dao
interface UserDao {

    @androidx.room.Query("SELECT * FROM user_table")
    fun getMeCenas(): List<User>
}


interface GitHubService {
    companion object {
        val instance: GitHubService by lazy {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .build()
            retrofit.create(GitHubService::class.java)
        }
    }

}














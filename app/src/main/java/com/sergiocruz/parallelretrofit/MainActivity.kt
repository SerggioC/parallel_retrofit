package com.sergiocruz.parallelretrofit

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.sergiocruz.parallelretrofit.api.ApiController
import com.sergiocruz.parallelretrofit.api.UserDao
import com.sergiocruz.parallelretrofit.api.UsersDatabase
import com.sergiocruz.parallelretrofit.ui.main.SectionsPagerAdapter
import com.venmo.android.pin.PinFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.lang.ref.WeakReference


class MainActivity : AppCompatActivity() {

    private lateinit var pinFragment: PinFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)

        view_pager.adapter = sectionsPagerAdapter

        tabs.setupWithViewPager(view_pager)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

//        pinFragment = PinFragment.checkPin(null, null)
//
//        pinFragment.observable.observeForever({ pin: String ->
//            Log.i("Sergio> ", "I observed this pin: $pin")
//        })


        Log.w("Sergio> ", "creating Activity")

        val fragment = supportFragmentManager.findFragmentByTag(PinFragment::class.java.simpleName)

        if (fragment is PinFragment) {
            //handleAttach(fragment)
        } else {
            // something else
        }

//        val dao: UserDao = UsersDatabase.getInstance(this).userDao()
//
//        GlobalScope.launch {
//            val cenas = dao.getMeCenas()
//        }



    }

    /*** on child fragment attached ***/
    override fun onAttachFragment(fragment: Fragment?) {
        super.onAttachFragment(fragment)

        if (fragment is PinFragment)
            handleAttach(fragment)
    }

    val config = true

    private fun handleAttach(fragment: Fragment?) {
        val dismissCallback: WeakReference<(() -> Unit)?>? = WeakReference {
            Log.i("Sergio> ", "dismissedpinfragment")
            Unit
        }

        if (fragment is PinFragment) {
            if (config) {
                fragment.creatorCallback = WeakReference { newPin ->
                    if (newPin.isNullOrEmpty().not()) {
                        /// save the $newPin in DB

                        runBlocking {
                            delay(2000)
                        }

                        //error("bug!")

                        false
                    } else {
                        // do something else

                        false
                    }

                }
            } else {
                val function = WeakReference { inputPin: String ->
                    val timer = System.currentTimeMillis()
                    val tn = Thread.currentThread().name


                    runBlocking {
                        // check the DB
                        delay(2000)
                    }

                    val savedPin = "1111"
                    val elapsed = System.currentTimeMillis() - timer
                    Log.i("Sergio> ", "delay done time: $elapsed threadName: $tn")
                    inputPin == savedPin
                }
                fragment.validatorCallback = function
            }
            fragment.dismissCallback = dismissCallback
        }

    }

//    override fun onRetainCustomNonConfigurationInstance(): Any {
//        return super.onRetainCustomNonConfigurationInstance()
//
//    }

    data class User(
        val userId: Int,
        val nif: String,
        val userName: String,
        val password: String,
        val pin: String
    )

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentByTag(PinFragment::class.java.simpleName)

        if (fragment is PinFragment) {
            fragment.dismiss()
            return
        }

        super.onBackPressed()
        ApiController.dispatcher.cancelAll()
        Toast.makeText(this, "canceled", Toast.LENGTH_SHORT).show()

    }


}
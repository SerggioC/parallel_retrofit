package com.sergiocruz.parallelretrofit

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.sergiocruz.parallelretrofit.api.ApiController
import com.sergiocruz.parallelretrofit.ui.main.SectionsPagerAdapter
import com.venmo.android.pin.*


class MainActivity : AppCompatActivity(), PinListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)

        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter

        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

        val fab: FloatingActionButton = findViewById(R.id.fab)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        pinIt()

    }

    data class User(
        val userId: Int,
        val nif: String,
        val userName: String,
        val password: String,
        val pin: String
    )


    private fun getAsyncConfig(): PinFragmentConfiguration? {
        return PinFragmentConfiguration(this)
            .pinSaver(object : AsyncSaver {
                override fun save(pin: String?) {
                    val threadName = Thread.currentThread().name
                    Log.i("Sergio> ", "validated $pin $threadName")
                }

            }).validator(object : AsyncValidator {
                override fun isValid(input: String): Boolean {
                    val threadName = Thread.currentThread().name
                    Log.i("Sergio> ", "entered $input $threadName")
                    return true
                }
            })
    }

    private fun getSyncConfig(): PinFragmentConfiguration? {
        return PinFragmentConfiguration(this)
            .pinSaver { pin ->
                val threadName = Thread.currentThread().name
                Log.i("Sergio> ", "validated $pin $threadName")

            }.validator { input ->
                val threadName = Thread.currentThread().name
                Log.i("Sergio> ", "entered $input $threadName")
                true

            }
    }


    fun pinIt() {


        val asyncConfig = getAsyncConfig()

        val toShow = if (false)
            PinFragment.newInstanceForVerification(asyncConfig)
        else
            PinFragment.newInstanceForCreation(asyncConfig)

        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, toShow)
            .commit()
    }

    override fun onValidated() {
        Log.i("Sergio> ", "validated")
    }

    override fun onPinCreated() {
        Log.i("Sergio> ", "created")
    }


    override fun onBackPressed() {
        //super.onBackPressed()
        ApiController.dispatcher.cancelAll()
        Toast.makeText(this, "canceled", Toast.LENGTH_SHORT).show()

    }


}
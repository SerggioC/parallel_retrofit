package com.sergiocruz.parallelretrofit.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.sergiocruz.parallelretrofit.R
import com.sergiocruz.parallelretrofit.model.Post
import com.sergiocruz.parallelretrofit.model.User
import kotlinx.android.synthetic.main.fragment_main.*

/**
 * A placeholder fragment containing a simple view.
 */
class PlaceholderFragment : Fragment() {

    private lateinit var pageViewModel: PageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_main, container, false)
        val textView: TextView = root.findViewById(R.id.section_label)
        pageViewModel.text.observe(this, Observer<String> {
            textView.text = it
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button.setOnClickListener {
            pageViewModel.getUsers().observe(this, Observer<List<User>?> {
                toastThis("DONE getting users: ${it?.size}")
            })
        }

        buttonMultiRequest.setOnClickListener {
            val initial = System.currentTimeMillis()
            pageViewModel.counter = 0
            val times = 100
            repeat(times) {
                pageViewModel.getUserFromAPI {
                    counterTV.text = it.toString()
                    if (it == times) {
                        val time = System.currentTimeMillis() - initial
                        toastThis("DONE! Parallel time: $time")
                        counterTV.append(" time: $time")
                    }
                }
            }
        }

        buttonSequential.setOnClickListener {
            val initial = System.currentTimeMillis()

            val lista = (1..100).toList()

            pageViewModel.getUserFromAPISequentially(lista, 1) { done, indexDone: Int ->
                sequentialTV.text = indexDone.toString()

                if (done) {
                    val time = System.currentTimeMillis() - initial
                    toastThis("DONE! Parallel time: $time")
                    sequentialTV.append(" time: $time")
                }

            }


        }


        buttonGetPostParallel.setOnClickListener {
            val list = (0..10).toList()
            val start = System.currentTimeMillis()
            pageViewModel
                .getPostsFromUserListParallel(list) { done: Boolean, index: Int, postList: List<Post>? ->
                    parallelPostsTV.text = index.toString()
                    if (done) {
                        val time = System.currentTimeMillis() - start
                        parallelPostsTV.append(" Time: $time")
                    }
                }
        }


        buttonSequentialGetPosts.setOnClickListener {
            val list = (0..10).toList()
            val start = System.currentTimeMillis()
            pageViewModel.getPostsFromUserListSequential(list, 0) { done: Boolean, index: Int, postList: List<Post>? ->
                textViewSeuentialGetPosts.text = index.toString()
                if (done) {
                    val time = System.currentTimeMillis() - start
                    textViewSeuentialGetPosts.append(" Time: $time")
                }
            }
        }

    }


    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): PlaceholderFragment {
            return PlaceholderFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}

private fun Fragment?.toastThis(message: String) {
    Toast.makeText(this?.context, message, Toast.LENGTH_SHORT).show()
}

package com.drofficial.googlegeminiapi

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginTop
import androidx.recyclerview.widget.LinearLayoutManager
import com.drofficial.googlegeminiapi.databinding.ActivityMainBinding
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var chatList: MutableList<Chat>
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chatList = mutableListOf()
        prepareRecyclerView()

        val dotSize = resources.getDimensionPixelSize(R.dimen.dot_size)
        val dotMargin = resources.getDimensionPixelSize(R.dimen.dot_margin)

        repeat(4) {
            val dot = ImageView(this)
            dot.setImageResource(R.drawable.dot_shape)

            val layoutParams = LinearLayout.LayoutParams(dotSize, dotSize)
            if (it > 0) {
                layoutParams.marginStart = dotMargin
            }

            dot.layoutParams = layoutParams
            binding.dotContainer.addView(dot)
        }

        // Apply the waving animation to each dot
        repeat(binding.dotContainer.childCount) { index ->
            val dot = binding.dotContainer.getChildAt(index)
            val waveAnimator = createWaveAnimator(dot, index)
            waveAnimator.start()
        }

        binding.sendButton.setOnClickListener {
            if (binding.inputMessage.text!!.isEmpty()){
                Toast.makeText(this, "Please enter some query", Toast.LENGTH_SHORT).show()
            } else {
                val question = binding.inputMessage.text.toString().trim()
                addToChat(question, Chat.SEND_BY_ME)
                binding.inputMessage.setText("")
                requestQuery(question)
                binding.dotContainer.visibility = View.VISIBLE
            }
        }
    }

    private fun prepareRecyclerView() {
        chatAdapter = ChatAdapter(chatList)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = chatAdapter
        }
    }

    private fun requestQuery(text: String) {
        val generativeModel = GenerativeModel(
            modelName = "gemini-pro",
            apiKey = BuildConfig.API_KEY
        )
        CoroutineScope(Dispatchers.IO).launch {
            val response = generativeModel.generateContent(text)
            withContext(Dispatchers.Main) {
                addResponse(response.text.toString().trim())
                binding.dotContainer.visibility = View.GONE
                Log.d("Response", "requestQuery: ${response.text}")
            }


        }
    }

    private fun addResponse(response: String) {
        addToChat(response, Chat.SEND_BY_GEMINI)
    }


    private fun addToChat(message: String, sendBy: String) {
        chatList.add(Chat(message, sendBy))
        chatAdapter.notifyDataSetChanged()
        binding.recyclerView.smoothScrollToPosition(chatAdapter.itemCount)
    }

    private fun createWaveAnimator(target: View, index: Int): ObjectAnimator {
        val waveAnimator = ObjectAnimator.ofFloat(target, "translationY", 0f, -20f, 0f)
        waveAnimator.repeatCount = ObjectAnimator.INFINITE
        waveAnimator.duration = 1000L
        waveAnimator.startDelay = index * 100L // Adjust the delay for each dot
        return waveAnimator
    }


    private fun isConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            return networkCapabilities != null &&
                    (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))

    }

    private fun buildDialog(context: Context): AlertDialog.Builder {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("No Internet Connection")
        builder.setMessage("Please check your internet connection.")
        builder.setPositiveButton("OK") { _, _ -> finishAffinity() }
        return builder
    }


    override fun onResume() {
        super.onResume()
        if (!isConnected(this)) {
            buildDialog(this).show()
        }
    }

}
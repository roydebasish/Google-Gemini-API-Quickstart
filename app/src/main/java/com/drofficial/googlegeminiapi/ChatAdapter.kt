package com.drofficial.googlegeminiapi
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class ChatAdapter(private val messageList: List<Chat>) : RecyclerView.Adapter<ChatAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val chatView = LayoutInflater.from(parent.context).inflate(R.layout.chat_item, parent, false)
        return MyViewHolder(chatView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val message = messageList[position]
        if (message.sentBy == Chat.SEND_BY_ME) {
            holder.leftChatView.visibility = View.GONE
            holder.rightChatView.visibility = View.VISIBLE
            holder.rightChatTextView.text = message.message
        } else {
            holder.rightChatView.visibility = View.GONE
            holder.leftChatView.visibility = View.VISIBLE
            holder.leftChatTextView.text = message.message
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var leftChatView: MaterialCardView = itemView.findViewById(R.id.left_chat_view)
        var rightChatView: MaterialCardView = itemView.findViewById(R.id.right_chat_view)
        var leftChatTextView: TextView = itemView.findViewById(R.id.left_chat_text_view)
        var rightChatTextView: TextView = itemView.findViewById(R.id.right_chat_text_view)
    }
}
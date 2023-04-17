package com.vladrip.ifchat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.vladrip.ifchat.R
import com.vladrip.ifchat.model.entity.Chat.ChatType
import com.vladrip.ifchat.model.entity.Message
import com.vladrip.ifchat.utils.FormatHelper

class MessagesAdapter(
    private val chatType: ChatType,
    private val userId: Long
) :
    PagingDataAdapter<Message, ViewHolder>(MESSAGE_COMPARATOR) {

    companion object {
        val MESSAGE_COMPARATOR = object : DiffUtil.ItemCallback<Message>() {
            override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
                return compareBy<Message>({ it.sentAt }, { it.content })
                    .compare(oldItem, newItem) == 0
            }

            override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean =
                oldItem.id == newItem.id
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = getItem(position)
        if (message != null) when (holder) {
            is MessageViewHolder -> holder.bind(message)
            is UserMessageViewHolder -> holder.bind(message)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val holder: ViewHolder = when (viewType) {
            R.layout.message_user ->
                UserMessageViewHolder(inflater.inflate(R.layout.message_user, parent, false))

            else -> {
                val view = inflater.inflate(R.layout.message, parent, false)
                if (chatType == ChatType.PRIVATE)
                    view.findViewById<TextView>(R.id.message_username).visibility = View.GONE
                MessageViewHolder(view)
            }
        }
        return holder
    }

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        return if (message != null && message.sender.id == userId)
            R.layout.message_user
        else R.layout.message
    }

    inner class MessageViewHolder(item: View) : ViewHolder(item) {
        fun bind(message: Message) {
            if (chatType != ChatType.PRIVATE)
                itemView.findViewById<TextView>(R.id.message_username).text =
                    message.sender.getFullName()
            itemView.findViewById<TextView>(R.id.message_content).text = message.content
            itemView.findViewById<TextView>(R.id.message_sent_at).text =
                FormatHelper.formatMessageSentAt(message.sentAt)
        }
    }

    inner class UserMessageViewHolder(item: View) : ViewHolder(item) {
        fun bind(message: Message) {
            itemView.findViewById<TextView>(R.id.message_content).text = message.content
            itemView.findViewById<TextView>(R.id.message_sent_at).text =
                FormatHelper.formatMessageSentAt(message.sentAt)
        }
    }
}
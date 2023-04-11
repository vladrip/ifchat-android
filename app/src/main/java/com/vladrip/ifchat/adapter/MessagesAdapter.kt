package com.vladrip.ifchat.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.vladrip.ifchat.R
import com.vladrip.ifchat.model.entity.Chat.ChatType
import com.vladrip.ifchat.model.entity.Message
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class MessagesAdapter(private val chatType: ChatType, private val userNumber: String) :
    PagingDataAdapter<Message, MessagesAdapter.MessageViewHolder>(MESSAGE_COMPARATOR) {

    companion object {
        val MESSAGE_COMPARATOR = object : DiffUtil.ItemCallback<Message>() {
            override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
                return compareBy<Message>({ it.sentAt }, { it.fromNumber }, { it.content })
                    .compare(oldItem, newItem) == 0
            }

            override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean =
                oldItem.id == newItem.id
        }
    }

    inner class MessageViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        fun bind(message: Message) {
            itemView.findViewById<TextView>(R.id.message_content).text = message.content
            itemView.findViewById<TextView>(R.id.message_sent_at).text = message.sentAt
                .format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))

            if (message.fromNumber != userNumber) {
                if (chatType == ChatType.GROUP)
                    itemView.findViewById<TextView>(R.id.message_username).text = message.fromNumber
            } else {
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { gravity = Gravity.END }
                itemView.layoutParams = params
            }
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val holder = MessageViewHolder(inflater.inflate(R.layout.recycler_message, parent, false))
        if (chatType == ChatType.PRIVATE)
            holder.itemView.findViewById<TextView>(R.id.message_username).height = 0
        return holder
    }
}
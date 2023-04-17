package com.vladrip.ifchat.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.marginEnd
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.vladrip.ifchat.R
import com.vladrip.ifchat.model.entity.Chat.ChatType
import com.vladrip.ifchat.model.entity.Message
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class MessagesAdapter(
    private val chatType: ChatType,
    private val userId: Long
) :
    PagingDataAdapter<Message, MessagesAdapter.MessageViewHolder>(MESSAGE_COMPARATOR) {

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

    inner class MessageViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val messageView: ViewGroup
        private val initialMargin: Int
        init {
            messageView = (item as ViewGroup).getChildAt(0) as ViewGroup
            initialMargin = messageView.marginEnd
        }

        fun bind(message: Message) {
            messageView.findViewById<TextView>(R.id.message_content).text = message.content
            messageView.findViewById<TextView>(R.id.message_sent_at).text = message.sentAt
                .format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))

            val sender = message.sender
            if (sender.id == userId) {
                messageView.apply {
                    findViewById<TextView>(R.id.message_username).visibility = View.GONE
                    background = AppCompatResources.getDrawable(context, R.drawable.shape_bg_outgoing_bubble)
                    //swap end and start paddings to suit shape change
                    setPadding(paddingEnd, paddingTop, paddingStart, paddingBottom)
                }
                messageView.layoutParams = FrameLayout.LayoutParams(
                    messageView.layoutParams as FrameLayout.LayoutParams
                ).apply {
                    marginStart = initialMargin
                    marginEnd = 0
                    gravity = Gravity.END
                }
            } else if (chatType == ChatType.GROUP) {
                val fullName = "${sender.firstName} ${sender.lastName}"
                messageView.findViewById<TextView>(R.id.message_username).text = fullName
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
            holder.messageView.findViewById<TextView>(R.id.message_username).visibility = View.GONE
        return holder
    }
}
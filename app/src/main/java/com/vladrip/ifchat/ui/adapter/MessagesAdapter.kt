package com.vladrip.ifchat.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.vladrip.ifchat.R
import com.vladrip.ifchat.model.Chat.ChatType
import com.vladrip.ifchat.model.Message
import com.vladrip.ifchat.ui.viewmodel.UiModel
import com.vladrip.ifchat.utils.FormatHelper

class MessagesAdapter(
    private val chatType: ChatType,
    private val onMessageClick: View.OnClickListener,
) : PagingDataAdapter<UiModel, ViewHolder>(UI_MODEL_COMPARATOR) {
    private val userUid = Firebase.auth.uid

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uiModel = getItem(position)
        if (uiModel is UiModel.MessageItem) when (holder) {
            is MessageViewHolder -> holder.bind(uiModel.message)
            is UserMessageViewHolder -> holder.bind(uiModel.message)
        }
        else if (uiModel is UiModel.DateSeparator)
            (holder as DateSeparatorViewHolder).bind(uiModel.formattedDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val holder: ViewHolder = when (viewType) {
            R.layout.message_user ->
                UserMessageViewHolder(inflater.inflate(R.layout.message_user, parent, false))

            R.layout.message -> {
                val view = inflater.inflate(R.layout.message, parent, false)
                if (chatType == ChatType.PRIVATE)
                    view.findViewById<TextView>(R.id.message_username).visibility = View.GONE
                MessageViewHolder(view)
            }

            R.layout.date_separator ->
                DateSeparatorViewHolder(inflater.inflate(R.layout.date_separator, parent, false))

            else -> throw IllegalArgumentException("Invalid itemView type")
        }
        return holder
    }

    override fun getItemViewType(position: Int): Int {
        val uiModel = getItem(position)
        return if (uiModel is UiModel.MessageItem) {
            if (uiModel.message.sender.uid == userUid)
                R.layout.message_user
            else R.layout.message
        } else R.layout.date_separator
    }

    inner class MessageViewHolder(view: View) : ViewHolder(view) {
        private val username = view.findViewById<TextView>(R.id.message_username)
        private val content = view.findViewById<TextView>(R.id.message_content)
        private val sentAt = view.findViewById<TextView>(R.id.message_sent_at)

        fun bind(message: Message) {
            if (chatType != ChatType.PRIVATE) username.text = message.sender.getFullName()
            content.text = message.content
            sentAt.text = FormatHelper.formatMessageSentAt(message.sentAt)
        }
    }

    inner class UserMessageViewHolder(view: View) : ViewHolder(view) {
        private val body = (itemView as ViewGroup).getChildAt(0)
        private val content = view.findViewById<TextView>(R.id.message_content)
        private val sentAt = view.findViewById<TextView>(R.id.message_sent_at)
        private val status = view.findViewById<ImageView>(R.id.message_status)

        init {
            body.setOnClickListener(onMessageClick)
        }

        fun bind(message: Message) {
            body.tag = message.id
            content.text = message.content
            sentAt.text = FormatHelper.formatMessageSentAt(message.sentAt)
            status.setImageResource(
                when (message.status) {
                    Message.Status.SENDING -> R.drawable.message_sending
                    Message.Status.SENT -> R.drawable.message_sent
                    Message.Status.DELETING -> R.drawable.delete
                    else -> R.drawable.message_read
                }
            )
        }
    }

    inner class DateSeparatorViewHolder(view: View) : ViewHolder(view) {
        private val date = view.findViewById<TextView>(R.id.date_separator_text)

        fun bind(formattedDate: String) {
            date.text = formattedDate
        }
    }

    companion object {
        val UI_MODEL_COMPARATOR = object : DiffUtil.ItemCallback<UiModel>() {
            override fun areItemsTheSame(oldItem: UiModel, newItem: UiModel): Boolean =
                if (oldItem is UiModel.MessageItem && newItem is UiModel.MessageItem)
                    oldItem.message.id == newItem.message.id
                else oldItem == newItem

            override fun areContentsTheSame(oldItem: UiModel, newItem: UiModel): Boolean =
                if (oldItem is UiModel.MessageItem && newItem is UiModel.MessageItem)
                    compareBy<Message>({ it.status }, { it.sentAt }, { it.content })
                        .compare(oldItem.message, newItem.message) == 0
                else if (oldItem is UiModel.DateSeparator && newItem is UiModel.DateSeparator)
                    oldItem.formattedDate == newItem.formattedDate
                else false
        }
    }
}
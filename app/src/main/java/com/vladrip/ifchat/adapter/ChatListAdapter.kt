package com.vladrip.ifchat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.vladrip.ifchat.R
import com.vladrip.ifchat.model.entity.ChatListEl
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.WeekFields
import java.util.Locale

class ChatListAdapter :
    PagingDataAdapter<ChatListEl, ChatListAdapter.ChatViewHolder>(CHAT_LIST_ELEMENT_COMPARATOR) {
    companion object {
        val CHAT_LIST_ELEMENT_COMPARATOR = object : DiffUtil.ItemCallback<ChatListEl>() {
            override fun areContentsTheSame(oldItem: ChatListEl, newItem: ChatListEl): Boolean {
                return compareBy<ChatListEl>(
                    { it.chatName },
                    { it.lastMsgSentAt },
                    { it.lastMsgContent })
                    .compare(oldItem, newItem) == 0
            }

            override fun areItemsTheSame(oldItem: ChatListEl, newItem: ChatListEl): Boolean =
                oldItem.chatId == newItem.chatId
        }
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ChatViewHolder(inflater.inflate(R.layout.recyler_chat, parent, false))
    }

    inner class ChatViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        fun bind(chatListEl: ChatListEl) {
            itemView.findViewById<TextView>(R.id.chat_name).text = chatListEl.chatName
            itemView.findViewById<TextView>(R.id.chat_short_info).text = chatListEl.lastMsgContent
            itemView.findViewById<TextView>(R.id.chat_last_msg_time).text =
                format(chatListEl.lastMsgSentAt)

            itemView.setOnClickListener {
                val bundle = bundleOf(
                    "chatId" to chatListEl.chatId,
                    "chatType" to chatListEl.chatType.toString()
                )
                itemView.findNavController().navigate(R.id.action_chat_list_to_chat, bundle)
            }
        }

        private fun format(dateTime: LocalDateTime): String {
            val now = LocalDateTime.now()
            val weekOfMonth = WeekFields.of(Locale.getDefault()).weekOfMonth()
            val formatter = when {
                dateTime.year != now.year ->
                    DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)

                dateTime.monthValue != now.monthValue ||
                        dateTime.get(weekOfMonth) != now.get(weekOfMonth) ->
                    DateTimeFormatter.ofPattern("MMM dd")

                dateTime.dayOfYear != now.dayOfYear ->
                    DateTimeFormatter.ofPattern("EEE")

                else -> DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(Locale.UK)
            }
            return dateTime.format(formatter)
        }
    }
}
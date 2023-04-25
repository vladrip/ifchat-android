package com.vladrip.ifchat.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.vladrip.ifchat.R
import com.vladrip.ifchat.model.ChatListEl
import com.vladrip.ifchat.utils.FormatHelper

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
        return ChatViewHolder(inflater.inflate(R.layout.chat_list_element, parent, false))
    }

    inner class ChatViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        fun bind(chatListEl: ChatListEl) {
            itemView.findViewById<TextView>(R.id.chat_name).text = chatListEl.chatName
            itemView.findViewById<TextView>(R.id.chat_short_info).text = chatListEl.lastMsgContent.trim()
            itemView.findViewById<TextView>(R.id.chat_last_msg_time).text =
                FormatHelper.formatLastSent(chatListEl.lastMsgSentAt)

            itemView.setOnClickListener {
                val bundle = bundleOf(
                    "chatId" to chatListEl.chatId,
                    "chatType" to chatListEl.chatType
                )
                itemView.findNavController().navigate(R.id.action_chat_list_to_chat, bundle)
            }
        }
    }
}
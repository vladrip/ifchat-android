package com.vladrip.ifchat.ui.chatlist

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
import com.vladrip.ifchat.data.entity.ChatListEl
import com.vladrip.ifchat.utils.FormatHelper

class ChatListAdapter :
    PagingDataAdapter<ChatListEl, ChatListAdapter.ChatViewHolder>(DEFAULT_COMPARATOR) {

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ChatViewHolder(inflater.inflate(R.layout.chat_list_element, parent, false))
    }

    class ChatViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        fun bind(chatListEl: ChatListEl) {
            itemView.findViewById<TextView>(R.id.member_name).text = chatListEl.chatName
            itemView.findViewById<TextView>(R.id.member_online).text =
                chatListEl.lastMsgContent?.replace("\n", " ")?.trim() ?: ""
            itemView.findViewById<TextView>(R.id.chat_last_msg_time).text =
                chatListEl.lastMsgSentAt?.let { FormatHelper.formatLastSent(it) } ?: ""

            itemView.setOnClickListener {
                val bundle = bundleOf(
                    "chatId" to chatListEl.chatId,
                    "chatType" to chatListEl.chatType
                )
                itemView.findNavController().navigate(R.id.action_chat_list_to_chat, bundle)
            }
        }
    }

    companion object {
        val DEFAULT_COMPARATOR = object : DiffUtil.ItemCallback<ChatListEl>() {
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
}
package com.vladrip.ifchat.ui.chat.info

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.vladrip.ifchat.R
import com.vladrip.ifchat.data.entity.ChatMemberShort
import com.vladrip.ifchat.utils.FormatHelper

class ChatMembersAdapter
    :
    PagingDataAdapter<ChatMemberShort, ChatMembersAdapter.ChatMemberViewHolder>(DEFAULT_COMPARATOR) {

    override fun onBindViewHolder(holder: ChatMemberViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMemberViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ChatMemberViewHolder(
            inflater.inflate(R.layout.chat_member_list_element, parent, false)
        )
    }

    class ChatMemberViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        fun bind(chatMemberShort: ChatMemberShort) {
            itemView.findViewById<TextView>(R.id.member_name).text = chatMemberShort.fullName()
            itemView.findViewById<TextView>(R.id.member_online).text =
                FormatHelper.formatLastOnline(chatMemberShort.onlineAt, itemView.context)
        }
    }

    companion object {
        val DEFAULT_COMPARATOR = object : DiffUtil.ItemCallback<ChatMemberShort>() {
            override fun areContentsTheSame(
                oldItem: ChatMemberShort,
                newItem: ChatMemberShort,
            ): Boolean {
                return compareBy<ChatMemberShort>(
                    { it.chatId },
                    { it.onlineAt },
                    { it.firstName },
                    { it.lastName })
                    .compare(oldItem, newItem) == 0
            }

            override fun areItemsTheSame(
                oldItem: ChatMemberShort,
                newItem: ChatMemberShort,
            ): Boolean =
                oldItem.id == newItem.id
        }
    }
}
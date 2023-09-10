package com.vladrip.ifchat.ui.chat.info

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.vladrip.ifchat.R
import com.vladrip.ifchat.ui.chat.info.tabs.ChatInfoTabFragment
import com.vladrip.ifchat.ui.chat.info.tabs.ChatMembersTabFragment

class ChatInfoTabsAdapter(fragment: Fragment, chatId: Long) : FragmentStateAdapter(fragment) {
    private val args = bundleOf("chatId" to chatId)

    override fun getItemCount(): Int {
        return posToTabNamesRes.size
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = when (position) {
            0 -> ChatInfoTabFragment()
            1 -> ChatMembersTabFragment()
            else -> Fragment()
        }
        fragment.arguments = args
        return fragment
    }

    companion object {
        val posToTabNamesRes = mapOf(
            0 to R.string.tab_info,
            1 to R.string.tab_members
        )
    }
}
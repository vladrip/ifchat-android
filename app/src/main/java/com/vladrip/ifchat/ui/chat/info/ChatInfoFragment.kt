package com.vladrip.ifchat.ui.chat.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.vladrip.ifchat.R
import com.vladrip.ifchat.databinding.FragmentChatInfoBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatInfoFragment : Fragment() {
    private var _binding: FragmentChatInfoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChatInfoViewModel by viewModels()
    private lateinit var chatInfoTabsAdapter: ChatInfoTabsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChatInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatInfoTabsAdapter = ChatInfoTabsAdapter(this, viewModel.chatId)
        binding.tabPager.adapter = chatInfoTabsAdapter

        TabLayoutMediator(binding.tabLayout, binding.tabPager) { tab, position ->
            tab.text = getString(
                ChatInfoTabsAdapter.posToTabNamesRes
                    .getOrDefault(position, R.string.unknown_error)
            )
        }.attach()
    }
}
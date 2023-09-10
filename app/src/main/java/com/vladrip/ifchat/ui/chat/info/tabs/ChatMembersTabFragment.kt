package com.vladrip.ifchat.ui.chat.info.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.vladrip.ifchat.databinding.FragmentChatMembersTabBinding
import com.vladrip.ifchat.ui.chat.info.ChatInfoViewModel
import com.vladrip.ifchat.ui.chat.info.ChatMembersAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatMembersTabFragment : Fragment() {
    private var _binding: FragmentChatMembersTabBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChatInfoViewModel by viewModels({ requireParentFragment() })
    private lateinit var adapter: ChatMembersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChatMembersTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ChatMembersAdapter()
        binding.chatMembersList.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.members.collectLatest { adapter.submitData(it) }
            }
        }
    }
}
package com.vladrip.ifchat.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import com.vladrip.ifchat.ui.adapter.ChatListAdapter
import com.vladrip.ifchat.databinding.FragmentChatListBinding
import com.vladrip.ifchat.ui.viewmodel.ChatListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatListFragment : Fragment() {
    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!
    private val chatListViewModel: ChatListViewModel by viewModels()
    private lateinit var adapter: ChatListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ChatListAdapter()
        binding.chatList.adapter = adapter
        binding.chatList.scrollToPosition(0)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                chatListViewModel.getChatList().collectLatest {
                    adapter.submitData(it)
                }
            }
        }

        styleRecyclerView()
    }

    private fun styleRecyclerView() {
        val dividerItemDecoration = DividerItemDecoration(
            binding.chatList.context, LinearLayout.VERTICAL
        )
        val divider =
            ContextCompat.getDrawable(requireContext(), android.R.drawable.divider_horizontal_dark)
        if (divider != null) {
            divider.setTint(Color.DKGRAY)
            dividerItemDecoration.setDrawable(divider)
        }
        binding.chatList.addItemDecoration(dividerItemDecoration)
    }
}
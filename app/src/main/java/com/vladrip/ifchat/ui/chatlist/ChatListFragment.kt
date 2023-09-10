package com.vladrip.ifchat.ui.chatlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import com.vladrip.ifchat.R
import com.vladrip.ifchat.databinding.FragmentChatListBinding
import com.vladrip.ifchat.exception.WaitingForNetworkException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatListFragment : Fragment() {
    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!
    private val chatListViewModel: ChatListViewModel by viewModels()
    private lateinit var adapter: ChatListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChatListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ChatListAdapter()
        binding.chatList.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                chatListViewModel.chatList.collectLatest {
                    adapter.submitData(it)
                    adapter.onPagesUpdatedFlow.take(1).collect {
                        binding.chatList.scrollToPosition(0)
                    }
                }
            }
        }

        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (actionBar != null)
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.CREATED) {
                    val waitingForNetwork =
                        getString(R.string.waiting_for_network).replaceFirstChar { it.uppercase() }
                    val loading = getString(R.string.loading).replaceFirstChar { it.uppercase() }

                    adapter.loadStateFlow.collect { loadState ->
                        val mediatorState = loadState.mediator?.refresh
                        if (mediatorState is LoadState.Error
                            && mediatorState.error is WaitingForNetworkException
                        ) {
                            actionBar.title = waitingForNetwork
                            delay(1000)
                            adapter.refresh()
                        } else if (mediatorState is LoadState.Loading
                            && actionBar.title != waitingForNetwork
                        ) {
                            actionBar.title = loading
                        } else if (mediatorState is LoadState.NotLoading) {
                            actionBar.title = getString(R.string.app_name)
                        }
                    }
                }
            }
        addDividersToRecyclerView()
    }

    override fun onResume() {
        adapter.refresh()
        super.onResume()
    }

    private fun addDividersToRecyclerView() {
        val dividerItemDecoration = DividerItemDecoration(
            binding.chatList.context, DividerItemDecoration.VERTICAL
        )
        dividerItemDecoration.drawable?.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                resources.getColor(R.color.palette1_divider, this.context?.theme),
                BlendModeCompat.SRC_OVER
            )
        binding.chatList.addItemDecoration(dividerItemDecoration)
    }
}
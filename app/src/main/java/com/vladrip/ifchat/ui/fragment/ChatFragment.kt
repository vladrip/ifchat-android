package com.vladrip.ifchat.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.navigateUp
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.vladrip.ifchat.R
import com.vladrip.ifchat.ui.adapter.MessagesAdapter
import com.vladrip.ifchat.databinding.FragmentChatBinding
import com.vladrip.ifchat.ui.viewmodel.ChatViewModel
import com.vladrip.ifchat.ui.state.ChatUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class ChatFragment : Fragment(), MenuProvider {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChatViewModel by viewModels()
    private lateinit var appbar: View
    private lateinit var adapter: MessagesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().addMenuProvider(this, viewLifecycleOwner)
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.chatId == 0L) {
            if (!findNavController().navigateUp())
                requireActivity().supportFragmentManager.popBackStackImmediate()
            return
        }

        initAppBar()
        //TODO: get user id (store in app context if authenticated?)
        adapter = MessagesAdapter(viewModel.chatType, 1)
        binding.messages.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.getChatById(viewModel.chatId, viewModel.chatType, requireContext())
                    .collectLatest { fillAppBar(it) }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.getMessages(viewModel.chatId).collectLatest {
                    adapter.submitData(it)
                }
            }
        }

        binding.messageInput.addTextChangedListener {
            val isSendEnabled = binding.sendMessage.isEnabled
            if (it.isNullOrBlank() && isSendEnabled) {
                binding.sendMessage.isEnabled = false
                binding.sendMessage.setColorFilter(Color.GRAY)
            } else if (!isSendEnabled) {
                binding.sendMessage.isEnabled = true
                binding.sendMessage.colorFilter = null
            }
        }

        binding.sendMessage.setOnClickListener {
            val text = binding.messageInput.text
            if (!text.isNullOrBlank() && text.length <= 4096) { //double-check to be safe
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.sendMessage(text.toString())
                }
            }
            text.clear()
        }
    }

    private fun initAppBar() {
        appbar = layoutInflater.inflate(R.layout.appbar_chat, binding.root, false)
        appbar.findViewById<TextView>(R.id.chat_short_info).text = getString(R.string.loading)

        (requireActivity() as AppCompatActivity).supportActionBar?.run {
            setDisplayShowCustomEnabled(true)
            customView = appbar
        }
    }

    private fun fillAppBar(chat: ChatUiState) {
        if (chat.name != null) appbar.findViewById<TextView>(R.id.chat_name).text = chat.name
        if (chat.shortInfo != null) appbar.findViewById<TextView>(R.id.chat_short_info).text =
            chat.shortInfo
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.chat_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            android.R.id.home -> findNavController().navigateUp()
        }
        return true
    }

    override fun onDestroy() {
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowCustomEnabled(false)
        super.onDestroy()
    }
}
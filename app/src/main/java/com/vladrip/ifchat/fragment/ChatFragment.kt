package com.vladrip.ifchat.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.vladrip.ifchat.R
import com.vladrip.ifchat.adapter.MessagesAdapter
import com.vladrip.ifchat.databinding.FragmentChatBinding
import com.vladrip.ifchat.model.entity.Chat.ChatType
import com.vladrip.ifchat.ui.ChatViewModel
import com.vladrip.ifchat.ui.state.ChatUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class ChatFragment : Fragment(), MenuProvider {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private val chatViewModel: ChatViewModel by viewModels()
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

        val chatId = arguments?.getLong("chatId")
        if (chatId == null) {
            requireActivity().supportFragmentManager.popBackStackImmediate()
            return
        }
        val chatType = arguments?.getString("chatType")
            ?.let { ChatType.valueOf(it) }
            ?: ChatType.GROUP

        initAppBar() //TODO: real user phone number
        adapter = MessagesAdapter(chatType, "+380 99 301 1337")
        binding.messages.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                val chat = withContext(Dispatchers.Default) {
                    chatViewModel.getChatById(chatId, chatType, requireContext())
                }
                fillAppBar(chat)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                chatViewModel.getMessages(chatId).collectLatest {
                    adapter.submitData(it)
                }
            }
        }
    }

    private fun initAppBar() {
        appbar = layoutInflater.inflate(R.layout.appbar_chat, binding.root, false)
        appbar.findViewById<TextView>(R.id.chat_name).text = getString(R.string.loading)

        (requireActivity() as AppCompatActivity).supportActionBar?.run {
            setDisplayShowCustomEnabled(true)
            customView = appbar
        }
    }

    private fun fillAppBar(chat: ChatUiState) {
        appbar.findViewById<TextView>(R.id.chat_name).text = chat.name
        appbar.findViewById<TextView>(R.id.chat_short_info).text = chat.shortInfo
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
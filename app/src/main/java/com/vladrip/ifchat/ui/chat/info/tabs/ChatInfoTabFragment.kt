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
import com.vladrip.ifchat.databinding.FragmentChatInfoTabBinding
import com.vladrip.ifchat.ui.chat.info.ChatInfoViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ChatInfoTabFragment : Fragment() {
    private var _binding: FragmentChatInfoTabBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChatInfoViewModel by viewModels({ requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChatInfoTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.getChatInfo().collectLatest {
                    binding.chatDescription.text = it.description
                }
            }
        }
    }
}
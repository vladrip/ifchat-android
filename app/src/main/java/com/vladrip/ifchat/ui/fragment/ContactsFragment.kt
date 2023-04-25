package com.vladrip.ifchat.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.vladrip.ifchat.databinding.FragmentContactsBinding
import com.vladrip.ifchat.mock.Constants
import com.vladrip.ifchat.mock.adapter.ContactsAdapter
import com.vladrip.ifchat.mock.dto.Contact

class ContactsFragment : Fragment() {
    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val c = requireContext()

        binding.contactRecycler.adapter = ContactsAdapter(
            listOf(
                Contact(1, "+12058058360", "Colin Becci"),
                Contact(2, "+380394716941", "Hollis Wil"),
                Contact(3, "+380683422638", "Shannen Danna"),
                Contact(4, "+380392855075", "Daphne Maris")
            )
        )
        binding.contactRecycler.setItemViewCacheSize(Constants.CONTACTS_RECYCLER_CACHE_SIZE)

        val itemDecorator = DividerItemDecoration(c, DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(
            ContextCompat.getDrawable(c, android.R.drawable.divider_horizontal_dark)!!
        )
        binding.contactRecycler.addItemDecoration(itemDecorator)
    }
}
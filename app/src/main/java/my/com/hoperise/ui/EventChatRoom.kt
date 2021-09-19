package my.com.hoperise.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import my.com.hoperise.R
import my.com.hoperise.data.EventChatRoomViewModel
import my.com.hoperise.databinding.FragmentEventChatRoomBinding
import my.com.hoperise.util.EventChatRoomAdapter
import my.com.hoperise.util.hideKeyboard

class EventChatRoom : Fragment() {

    private lateinit var binding: FragmentEventChatRoomBinding
    private val vmChatRoom: EventChatRoomViewModel by activityViewModels()
    private val id by lazy { requireArguments().getString("id") ?: "" }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        requireActivity().title = getString(R.string.chat_room)

        binding = FragmentEventChatRoomBinding.inflate(inflater, container, false)

        vmChatRoom.setEventID(id)

        val adapter = EventChatRoomAdapter()

        binding.rvChatRoom.adapter = adapter

        vmChatRoom.getMessages().observe(viewLifecycleOwner) { messages ->
            adapter.submitList(messages)
            if (messages.isNotEmpty())
                binding.rvChatRoom.smoothScrollToPosition(messages.size - 1)
        }

        binding.edtMessage.setText(vmChatRoom.userContent.value.toString())

        binding.edtMessage.doOnTextChanged { text, _, _, _ ->
            vmChatRoom.userContent.value = text.toString()

            if (text.toString().isEmpty()) {
                hideKeyboard()
                binding.btnSend.visibility = View.INVISIBLE
            }
            else
                binding.btnSend.visibility = View.VISIBLE
        }

        binding.btnSend.setOnClickListener {
            vmChatRoom.sendMessage(binding.edtMessage.text.toString())
            binding.edtMessage.text.clear()
            hideKeyboard()
            binding.btnSend.visibility = View.INVISIBLE
        }

        return binding.root
    }
}

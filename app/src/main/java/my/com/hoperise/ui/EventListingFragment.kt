package my.com.hoperise.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import my.com.hoperise.R
import my.com.hoperise.databinding.FragmentEventListingBinding

class EventListingFragment : Fragment() {

    private lateinit var binding: FragmentEventListingBinding
    private val nav by lazy { findNavController() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        binding = FragmentEventListingBinding.inflate(inflater, container, false)
        val role = "user"
        if(role == "staff"){
            setHasOptionsMenu(true)
        }


        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //menu.clear()
        inflater.inflate(R.menu.menu_add, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.add)
            nav.navigate(R.id.addEventFragment)
        else
            return false

        return super.onOptionsItemSelected(item)
    }
}
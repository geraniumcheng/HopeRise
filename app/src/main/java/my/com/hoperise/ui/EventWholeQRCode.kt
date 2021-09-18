package my.com.hoperise.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import my.com.hoperise.R
import my.com.hoperise.databinding.FragmentEventWholeQRCodeBinding

class EventWholeQRCode : Fragment() {

    private lateinit var binding: FragmentEventWholeQRCodeBinding
    private val nav by lazy { findNavController() }
    private val content by lazy { requireArguments().getString("content") ?: "" }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        requireActivity().title = getString(R.string.QRCode)

        binding = FragmentEventWholeQRCodeBinding.inflate(inflater, container, false)

        val bitmap = BarcodeEncoder().encodeBitmap(content, BarcodeFormat.QR_CODE, 450, 450)
        binding.imgWholeQR.setImageBitmap(bitmap)

        binding.imgWholeQR.setOnClickListener { nav.navigateUp() }

        return binding.root
    }

}
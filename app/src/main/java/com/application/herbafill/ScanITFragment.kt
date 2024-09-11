package com.application.herbafill

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.application.herbafill.databinding.FragmentScanItBinding
class ScanITFragment : Fragment() {

    private lateinit var binding: FragmentScanItBinding
    private val pickImageRequest = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScanItBinding.inflate(inflater, container, false)
        binding.selectImageButton.setOnClickListener{
            openImagePicker()
        }
        return binding.root
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        startActivityForResult(intent, pickImageRequest)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickImageRequest && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                binding.imageView.setImageURI(uri)
            }
        }
    }

}
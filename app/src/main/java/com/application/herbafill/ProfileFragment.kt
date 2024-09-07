package com.application.herbafill

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import com.application.herbafill.Model.Account
import com.application.herbafill.databinding.FragmentProfileBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var currentPhotoPath: String

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_PICK_IMAGE = 2
        private const val REQUEST_IMAGE_CROP = 3
    }

    private var details = Account(
        0,
        "Keilizon Matthew T. Centino",
        R.drawable.meditation,
        "keilizon",
        "00"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.name.text = details.name
        binding.imageButton.setImageResource(details.profileImage)
        binding.imageButton.setOnClickListener {
            showImageOptions()
        }
        binding.back.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
        }
        return binding.root
    }

    private fun showImageOptions() {
        val dialogView = layoutInflater.inflate(R.layout.profiledialog, null)

        // Create the AlertDialog
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)

        // Create and show the dialog
        val dialog = dialogBuilder.create()
        dialog.show()

        // Set up button click listeners
        dialogView.findViewById<TextView>(R.id.btn_take_photo).setOnClickListener {
            dispatchTakePictureIntent()
            dialog.dismiss()
        }

        dialogView.findViewById<TextView>(R.id.btn_choose_gallery).setOnClickListener {
            dispatchPickPictureIntent()
            dialog.dismiss()
        }

        dialogView.findViewById<TextView>(R.id.btn_cancel).setOnClickListener {
            dialog.dismiss()
        }

    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "${requireContext().packageName}.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    private fun dispatchPickPictureIntent() {
        val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPhoto, REQUEST_PICK_IMAGE)
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val file = File(currentPhotoPath)
                    binding.imageButton.setImageURI(Uri.fromFile(file))
                }
                REQUEST_PICK_IMAGE -> {
                    data?.data?.let { uri ->
                        binding.imageButton.setImageURI(uri)
                    }
                }
            }
        }
    }

    private fun startCropImage(uri: Uri) {
        val cropIntent = Intent("com.android.camera.action.CROP").apply {
            setDataAndType(uri, "image/*")
            putExtra("crop", "true")
            putExtra("aspectX", 1)
            putExtra("aspectY", 1)
            putExtra("outputX", 512)
            putExtra("outputY", 512)
            putExtra("return-data", false)  // Set to false to use EXTRA_OUTPUT

            // Create a file to store the cropped image
            val croppedImageFile = createImageFile()
            val croppedImageUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                croppedImageFile
            )
            putExtra(MediaStore.EXTRA_OUTPUT, croppedImageUri)
        }
        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP)
    }

}
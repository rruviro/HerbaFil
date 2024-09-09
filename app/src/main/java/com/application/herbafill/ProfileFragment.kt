package com.application.herbafill

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import com.application.herbafill.Api.RetrofitClient
import com.application.herbafill.Model.Account
import com.application.herbafill.Model.HerbalDetailResponse
import com.application.herbafill.Model.UpdateResponse
import com.application.herbafill.databinding.FragmentProfileBinding
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentProfileBinding.inflate(inflater, container, false)

        val userID = arguments?.getInt("userID") ?: return binding.root
        fetchData(userID)

        binding.imageButton.setOnClickListener {
            showImageOptions()
        }

        binding.changeInfo.setOnClickListener {
            showChangeInfo()
        }

        //freeze page after navigating from other page as for now user's can only use back gesture
        binding.back.setOnClickListener {
//            findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
        }

        return binding.root
    }

    private var userDetails: Account? = null
    private fun fetchData(userID: Int) {
        RetrofitClient.instance.getUserDetails(userID)
            .enqueue(object : Callback<Account> {
                override fun onResponse(call: Call<Account>, response: Response<Account>) {
                    if (response.isSuccessful) {
                        userDetails = response.body()
                        if (userDetails != null) {
                            // Extract the username, password, and image
                            val name = userDetails!!.name

                            binding.name.text = name

                            Glide.with(requireContext())
                                .load(userDetails!!.userProfile)
                                .into(binding.imageButton)

                        } else {
                            Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Response not successful", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Account>, t: Throwable) {
                    Toast.makeText(context, "Connection Failed: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun showChangeInfo() {
        if (userDetails != null) {
            val dialogView = layoutInflater.inflate(R.layout.infomation_dialog, null)

            val dialogBuilder = AlertDialog.Builder(requireContext())
                .setView(dialogView)

            // Set initial values from userDetails
            val nameEditText = dialogView.findViewById<EditText>(R.id.name)
            val usernameEditText = dialogView.findViewById<EditText>(R.id.username)
            val passwordEditText = dialogView.findViewById<EditText>(R.id.password)
            val dialog = dialogBuilder.create()

            nameEditText.setText(userDetails!!.name)
            usernameEditText.setText(userDetails!!.username)
            passwordEditText.setText(userDetails!!.password)

            // Toggle password visibility
            dialogView.findViewById<TextView>(R.id.eyeToggle).setOnClickListener {
                if (passwordEditText.inputType == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                    passwordEditText.inputType == (InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT)) {
                    passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                } else {
                    passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                }
                passwordEditText.setSelection(passwordEditText.text.length)
            }

            dialogView.findViewById<TextView>(R.id.deploy).setOnClickListener {
                val updatedName = nameEditText.text.toString()
                val updatedUsername = usernameEditText.text.toString()
                val updatedPassword = passwordEditText.text.toString()
                updateUserData(userDetails!!.userID, updatedName, updatedUsername, updatedPassword)
                dialog.dismiss()
            }

            dialog.show()

        } else {
            Toast.makeText(context, "User details not available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUserData(userID: Int, name: String, username: String, password: String) {
        RetrofitClient.instance.updateUserDetails(userID, name, username, password)
            .enqueue(object : Callback<UpdateResponse> {
                override fun onResponse(call: Call<UpdateResponse>, response: Response<UpdateResponse>) {
                    if (response.isSuccessful) {
                        val updateResponse = response.body()
                        if (updateResponse != null) {
                            if (updateResponse.status == "success") {
                                Toast.makeText(context, updateResponse.message, Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Error: ${updateResponse.message}", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Response body is null", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Failed to update data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UpdateResponse>, t: Throwable) {
                    Toast.makeText(context, "Update failed: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
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
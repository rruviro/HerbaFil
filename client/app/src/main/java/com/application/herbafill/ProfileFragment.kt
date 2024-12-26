package com.application.herbafill

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputType
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.herbafill.Adapter.UserHistoryAdapter
import com.application.herbafill.Api.RetrofitClient
import com.application.herbafill.Model.Account
import com.application.herbafill.Model.Herbals
import com.application.herbafill.Model.UpdateResponse
import com.application.herbafill.Model.UserHistoryDetail
import com.application.herbafill.Model.UserHistoryResponse
import com.application.herbafill.Model.UserProfile
import com.application.herbafill.databinding.FragmentProfileBinding
import com.bumptech.glide.Glide
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileFragment : Fragment(), UserHistoryAdapter.OnItemClickListener {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var historyAdapter: UserHistoryAdapter
    private lateinit var historyList: MutableList<UserHistoryDetail>
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
        val bundle = Bundle().apply {
            putInt("userID", userID)
        }

        fetchData(userID)

        binding.imageButton.setOnClickListener {
            showImageOptions()
        }

        binding.changeInfo.setOnClickListener {
            showChangeInfo()
        }

        binding.back.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_homeFragment, bundle)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        val userID = arguments?.getInt("userID")
        if (userID != null) {
            fetchUserHistory(userID)
        }
    }

    private fun setupRecyclerView() {
        binding.historyRecycleView.layoutManager = LinearLayoutManager(context)
        historyAdapter = UserHistoryAdapter(emptyList(), this)
        binding.historyRecycleView.adapter = historyAdapter
    }

    private fun fetchUserHistory(userID: Int) {
        RetrofitClient.instance.getUserHistory(userID).enqueue(object : Callback<UserHistoryResponse> {
            override fun onResponse(
                call: Call<UserHistoryResponse>,
                response: Response<UserHistoryResponse>
            ) {
                if (response.isSuccessful) {
                    val historyList = response.body()?.data ?: emptyList()
                    historyAdapter = UserHistoryAdapter(historyList, this@ProfileFragment)
                    binding.historyRecycleView.adapter = historyAdapter
                } else {
                    Toast.makeText(context, "Failed to fetch user history", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserHistoryResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onItemClick(history: UserHistoryDetail) {
        val userID = arguments?.getInt("userID")
        val bundle = Bundle().apply {
            putString("mlHerbName", history.mlHerbName)
            userID?.let { putInt("userID", it) }
        }
        findNavController().navigate(R.id.action_profileFragment_to_historyDetailFragment, bundle)
    }

    private var userDetails: Account? = null
    private fun fetchData(userID: Int) {
        RetrofitClient.instance.getUserDetails(userID)
            .enqueue(object : Callback<Account> {
                override fun onResponse(call: Call<Account>, response: Response<Account>) {
                    if (response.isSuccessful) {
                        userDetails = response.body()
                        if (userDetails != null) {
                            val name = userDetails!!.name

                            binding.name.text = name

                            Glide.with(requireContext())
                                .load(userDetails?.userProfile)
                                .placeholder(R.drawable.meditation)
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

            val nameEditText = dialogView.findViewById<EditText>(R.id.name)
            val usernameEditText = dialogView.findViewById<EditText>(R.id.username)
            val passwordEditText = dialogView.findViewById<EditText>(R.id.password)
            val dialog = dialogBuilder.create()

            nameEditText.setText(userDetails!!.name)
            usernameEditText.setText(userDetails!!.username)
            passwordEditText.setText(userDetails!!.password)

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
                                fetchData(userID)
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

        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)

        val dialog = dialogBuilder.create()
        dialog.show()

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
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
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

    private fun handleCropImage(resultUri: Uri) {
        val cropIntent = Intent("com.android.camera.action.CROP")
        cropIntent.setDataAndType(resultUri, "image/*")
        cropIntent.putExtra("crop", "true")
        cropIntent.putExtra("aspectX", 1)
        cropIntent.putExtra("aspectY", 1)
        cropIntent.putExtra("outputX", 500)
        cropIntent.putExtra("outputY", 500)
        cropIntent.putExtra("return-data", true)
        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val file = File(currentPhotoPath)
                    val croppedBitmap = BitmapFactory.decodeFile(file.absolutePath)
                    binding.imageButton.setImageBitmap(croppedBitmap)

                    val croppedFile = createTempFileFromBitmap(croppedBitmap)
                    val userID = arguments?.getInt("userID")
                    if (userID != null && croppedFile != null) {
                        uploadImageToServer(userID, croppedFile)
                    } else {
                        Toast.makeText(requireContext(), "Failed to save cropped image", Toast.LENGTH_SHORT).show()
                    }
                }
                REQUEST_PICK_IMAGE -> {
                    val imageUri = data?.data
                    if (imageUri != null) {
                        handleCropImage(imageUri)
                    }
                }
                REQUEST_IMAGE_CROP -> {
                    val extras = data?.extras
                    val croppedBitmap = extras?.getParcelable<Bitmap>("data")
                    if (croppedBitmap != null) {
                        Glide.with(requireContext())
                            .load(croppedBitmap)
                            .into(binding.imageButton)

                        val croppedImageFile = createTempFileFromBitmap(croppedBitmap)
                        if (croppedImageFile != null) {
                            val userID = arguments?.getInt("userID")
                            if (userID != null) {
                                uploadImageToServer(userID, croppedImageFile)
                            }
                        } else {
                            Toast.makeText(context, "Failed to create file from bitmap", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Failed to get cropped image", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun createTempFileFromBitmap(bitmap: Bitmap): File? {
        return try {
            val tempFile = File.createTempFile("cropped_image", ".jpg", requireContext().cacheDir)
            FileOutputStream(tempFile).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
            tempFile
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    // Upload image file to server
    private fun uploadImageToServer(userID: Int, imageFile: File) {
        val requestBody = RequestBody.create(MediaType.parse("image/*"), imageFile)
        val body = MultipartBody.Part.createFormData("image", imageFile.name, requestBody)
        val userIdRequestBody = RequestBody.create(MediaType.parse("text/plain"), userID.toString())

        RetrofitClient.instance.uploadImage(body,userIdRequestBody)
            .enqueue(object : Callback<UpdateResponse> {
                override fun onResponse(call: Call<UpdateResponse>, response: Response<UpdateResponse>) {
                    if (response.isSuccessful && response.body()?.status == "success") {
                        Toast.makeText(context, "Image uploaded and saved to database successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UpdateResponse>, t: Throwable) {
                    Toast.makeText(context, "Upload failed: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

}
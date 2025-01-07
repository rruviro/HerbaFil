package com.application.herbafill

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
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
import com.application.herbafill.Api.ApiService
import com.application.herbafill.Api.RetrofitClient
import com.application.herbafill.Api.RetrofitClient.instance
import com.application.herbafill.Model.Account
import com.application.herbafill.Model.Herbals
import com.application.herbafill.Model.UpdateResponse
import com.application.herbafill.Model.UserHistoryDetail
import com.application.herbafill.Model.UserHistoryResponse
import com.application.herbafill.Model.UserImage
import com.application.herbafill.Model.UserProfileUpdateRequest
import com.application.herbafill.databinding.FragmentProfileBinding
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
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
        val email = arguments?.getString("email") ?: return binding.root
        val bundle = Bundle().apply {
            putInt("userID", userID)
            putString("email", email)
        }

        fetchData(userID)
        fetchPersonalData(email)

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
        val email = arguments?.getString("email")
        val bundle = Bundle().apply {
            putString("mlHerbName", history.mlherbname)
            userID?.let { putInt("userID", it) }
            putString("email", email)
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
                    val imageUri = Uri.fromFile(file)  // Convert file to URI
                    binding.imageButton.setImageURI(imageUri)

                    // Convert URI to Base64
                    val base64Image = convertUriToBase64(imageUri)
                    val userID = arguments?.getInt("userID")
                    val email = arguments?.getString("email")
                    if (userID != null) {
                        saveProfileImageToFirebase(email.toString(), base64Image)
                    } else {
                        Toast.makeText(requireContext(), "Failed to save image", Toast.LENGTH_SHORT).show()
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

                        // Convert cropped image to URI and then to Base64
                        val tempFile = File(requireContext().cacheDir, "cropped_image.png")
                        val outputStream = FileOutputStream(tempFile)
                        croppedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                        outputStream.close()
                        val croppedUri = Uri.fromFile(tempFile)

                        // Convert URI to Base64
                        val base64Image = convertUriToBase64(croppedUri)

                        val userID = arguments?.getInt("userID")
                        val email = arguments?.getString("email")
                        if (userID != null) {
                            saveProfileImageToFirebase(email.toString(), base64Image)
                        }
                    } else {
                        Toast.makeText(context, "Failed to get cropped image", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun convertUriToBase64(uri: Uri): String {
        val bitmap = BitmapFactory.decodeStream(requireContext().contentResolver.openInputStream(uri))
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun fetchPersonalData(email: String) {
        val sanitizedEmail = email.replace(".", "_") // Sanitize email to use as a key
        val databaseRef = FirebaseDatabase.getInstance().reference
            .child("users")
            .child(sanitizedEmail)

        // Fetch user data from Firebase
        databaseRef.get().addOnSuccessListener { snapshot ->
            val userImageBase64 = snapshot.child("userProfile").value as? String
            if (userImageBase64 != null) {
                // Decode Base64 string to byte array
                val imageByteArray = Base64.decode(userImageBase64, Base64.DEFAULT)

                // Load the image using Glide
                Glide.with(this)
                    .load(imageByteArray)
                    .placeholder(R.drawable.meditation)
                    .into(binding.imageButton) // Replace with your ImageView
            } else {
                // Handle case where userProfile is null
                Log.e("ProfileFragment", "User profile image URL is null.")
            }
        }.addOnFailureListener { exception ->
            // Handle any errors
            Log.e("ProfileFragment", "Error fetching user data: ${exception.message}")
        }
    }

    private fun saveProfileImageToFirebase(email: String, base64Image: String) {
        val sanitizedEmail = email.replace(".", "_") // Sanitizing email
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users").child(sanitizedEmail)
        // Fetch existing user data and update profile image
        databaseRef.get().addOnSuccessListener { snapshot ->
            val personalInfo = snapshot.getValue(UserImage::class.java)
            personalInfo?.let {
                val updatedInfo = it.copy(userProfile = base64Image)
                // Update the user profile data in Firebase
                databaseRef.setValue(updatedInfo).addOnSuccessListener {
                    Log.d("ProfileFragment", "Profile image updated successfully.")
                }.addOnFailureListener {
                    Log.e("ProfileFragment", "Failed to update profile image: ${it.message}")
                }
            }
        }
    }

}
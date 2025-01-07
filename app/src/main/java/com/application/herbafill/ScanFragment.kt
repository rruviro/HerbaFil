package com.application.herbafill

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import com.application.herbafill.databinding.FragmentScanBinding
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScanFragment : Fragment() {

    private lateinit var binding: FragmentScanBinding
    private lateinit var currentPhotoPath: String

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_PICK_IMAGE = 2
    }
    private val classNames = arrayOf(
        "Akapulko", "Ampalaya", "Bayabas", "Bawang", "Lagundi",
        "Niyog-niyogan", "Sambong", "Tsaang Gubat", "Ulasimang Bato",
        "Yerba Buena", "Unknown"
    )

    private var interpreter: Interpreter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScanBinding.inflate(inflater, container, false)

        val userID = arguments?.getInt("userID") ?: return binding.root
        val email = arguments?.getString("email") ?: return binding.root
        val childBundle = Bundle()
        childBundle.putInt("userID", userID)
        childBundle.putString("email", email)

        try {
            interpreter = Interpreter(loadModelFile("classifier.tflite"))
        } catch (e: IOException) {
            e.printStackTrace()
        }

        binding.selectImageButton.setOnClickListener {
            openImagePicker()
        }

        binding.captureBtn.setOnClickListener {
            captureImage()
        }

        binding.back.setOnClickListener {
            findNavController().navigate(R.id.action_scanFragment_to_homeFragment, childBundle)
        }

        return binding.root
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        startActivityForResult(intent, REQUEST_PICK_IMAGE)
    }

    private fun captureImage() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
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
                    val uri = Uri.fromFile(file)
                    binding.imageView.setImageURI(uri)
                    classifyImage(uri)
                }
                REQUEST_PICK_IMAGE -> {
                    data?.data?.let { uri ->
                        binding.imageView.setImageURI(uri)
                        classifyImage(uri)
                    }
                }
            }
        }
    }


    private fun classifyImage(uri: Uri) {
        if (interpreter == null) {
            Toast.makeText(context, "Model not initialized", Toast.LENGTH_SHORT).show()
            return
        }

        binding.classificationResult.text = "Classifying..."

        try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            val inputSize = 224
            val inputBuffer = ByteBuffer.allocateDirect(4 * 1 * inputSize * inputSize * 3).apply {
                order(ByteOrder.nativeOrder())
            }
            inputBuffer.rewind()

            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
            val intValues = IntArray(inputSize * inputSize)
            resizedBitmap.getPixels(intValues, 0, inputSize, 0, 0, inputSize, inputSize)

            for (pixel in intValues) {
                val r = ((pixel shr 16) and 0xFF) / 255.0f
                val g = ((pixel shr 8) and 0xFF) / 255.0f
                val b = (pixel and 0xFF) / 255.0f
                inputBuffer.putFloat(r)
                inputBuffer.putFloat(g)
                inputBuffer.putFloat(b)
            }

            val outputBuffer = ByteBuffer.allocateDirect(4 * classNames.size).apply {
                order(ByteOrder.nativeOrder())
            }
            val outputArray = FloatArray(classNames.size)

            interpreter?.run(inputBuffer, outputBuffer)

            outputBuffer.rewind()
            outputBuffer.asFloatBuffer().get(outputArray)

            outputArray.forEachIndexed { index, value ->
                Log.d("ClassifyImage", "Class $index (${classNames.getOrElse(index) { "Unknown" }}) : $value")
            }

            val resultString = buildString {
                outputArray.forEachIndexed { index, value ->
                    append("Class $index (${classNames.getOrElse(index) { "Unknown" }}) : $value\n")
                }
            }

            val maxIndex = outputArray.indices.maxByOrNull { outputArray[it] } ?: -1

            if (maxIndex != -1) {
                val resultText = classNames[maxIndex]
                binding.classificationResult.text = "Prediction: $resultText"

                val userID = arguments?.getInt("userID") ?: run {
                    Toast.makeText(requireContext(), "User ID is missing", Toast.LENGTH_SHORT).show()
                    return
                }
                val email = arguments?.getString("email") ?: run {
                    Toast.makeText(requireContext(), "User email is missing", Toast.LENGTH_SHORT).show()
                    return
                }
                val childBundle = Bundle()
                childBundle.putString("mlHerbName", resultText)
                childBundle.putInt("userID", userID)
                childBundle.putString("email", email)

                if (childBundle.equals("Unknown")) {
                    Toast.makeText(requireContext(), "This image is not valid, Plase try again with other Image", Toast.LENGTH_SHORT).show()
                } else {
                    findNavController().navigate(R.id.action_scanFragment_to_scanedDetailsFragment, childBundle)
                }

            } else {
                binding.classificationResult.text = "Error: No valid prediction"
            }



        } catch (e: Exception) {
            e.printStackTrace()
            binding.classificationResult.text = "Error during classification: ${e.message}"
        }
    }

    @Throws(IOException::class)
    private fun loadModelFile(filename: String): ByteBuffer {
        val fileDescriptor = requireContext().assets.openFd(filename)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        interpreter?.close()
    }
}
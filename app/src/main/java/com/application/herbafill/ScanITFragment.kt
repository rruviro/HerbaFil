package com.application.herbafill

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.application.herbafill.databinding.FragmentScanItBinding
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.metadata.MetadataExtractor
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.ByteOrder

class ScanITFragment : Fragment() {

    private lateinit var binding: FragmentScanItBinding
    private val pickImageRequest = 1

    // Class names from the TFLite model metadata
    private val classNames = arrayOf(
        "Akapulko", "Ampalaya", "Bayabas", "Bawang", "Lagundi",
        "Niyog-niyogan", "Sambong", "Tsaang Gubat", "Ulasimang Bato",
        "Yerba Buena"
    )

    private var interpreter: Interpreter? = null // Use nullable type for interpreter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScanItBinding.inflate(inflater, container, false)

        // Initialize the TensorFlow Lite model
        try {
            interpreter = Interpreter(loadModelFile("classifier.tflite"))
            Toast.makeText(context, "Model loaded successfully!", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Model loading failed!", Toast.LENGTH_SHORT).show()
        }

        binding.selectImageButton.setOnClickListener {
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
                classifyImage(uri)
            }
        }
    }

    private fun classifyImage(uri: Uri) {
        if (interpreter == null) {
            Toast.makeText(context, "Model not initialized", Toast.LENGTH_SHORT).show()
            return
        }

        // Clear previous classification result
        binding.classificationResult.text = "Classifying..."

        try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            // Assuming model input is (1, 224, 224, 3) for this example
            val inputSize = 224
            val inputBuffer = ByteBuffer.allocateDirect(4 * 1 * inputSize * inputSize * 3).apply {
                order(ByteOrder.nativeOrder())
            }
            inputBuffer.rewind()

            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
            val intValues = IntArray(inputSize * inputSize)
            resizedBitmap.getPixels(intValues, 0, inputSize, 0, 0, inputSize, inputSize)

            // Normalize and add pixel data to the ByteBuffer
            for (pixel in intValues) {
                val r = ((pixel shr 16) and 0xFF) / 255.0f
                val g = ((pixel shr 8) and 0xFF) / 255.0f
                val b = (pixel and 0xFF) / 255.0f
                inputBuffer.putFloat(r)
                inputBuffer.putFloat(g)
                inputBuffer.putFloat(b)
            }

            // Create output buffer with correct shape
            val outputBuffer = ByteBuffer.allocateDirect(4 * classNames.size).apply {
                order(ByteOrder.nativeOrder())
            }
            val outputArray = FloatArray(classNames.size)

            // Run inference
            interpreter?.run(inputBuffer, outputBuffer)

            // Convert output buffer to FloatArray
            outputBuffer.rewind()
            outputBuffer.asFloatBuffer().get(outputArray)

            // Log output values
            outputArray.forEachIndexed { index, value ->
                Log.d("ClassifyImage", "Class $index (${classNames.getOrElse(index) { "Unknown" }}) : $value")
            }

            // Create a string to hold class probabilities
            val resultString = buildString {
                outputArray.forEachIndexed { index, value ->
                    append("Class $index (${classNames.getOrElse(index) { "Unknown" }}) : $value\n")
                }
            }

            // Show Toast with class probabilities
            //Toast.makeText(context, resultString, Toast.LENGTH_LONG).show()

            // Process results
            val maxIndex = outputArray.indices.maxByOrNull { outputArray[it] } ?: -1

            if (maxIndex != -1) {
                val resultText = classNames[maxIndex]
                binding.classificationResult.text = "Prediction: $resultText"

                val bundle = Bundle().apply {
                    putString("mlHerbName", resultText)
                }
                findNavController().navigate(R.id.action_scanITFragment_to_scanedDetailsFragment, bundle)

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
        interpreter?.close() // Close only if interpreter is initialized
    }

}

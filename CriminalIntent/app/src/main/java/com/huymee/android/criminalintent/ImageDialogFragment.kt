package com.huymee.android.criminalintent

import android.app.Dialog
import android.os.Bundle

import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.huymee.android.criminalintent.databinding.FullImageDialogLayoutBinding

private const val TAG = "ImageDialogFragment"

class ImageDialogFragment : DialogFragment() {

    private val args: ImageDialogFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val photoFilePath = args.filePath
        val binding = FullImageDialogLayoutBinding.inflate(layoutInflater)

        val dialog = AlertDialog.Builder(requireContext()).create().apply {
            setView(binding.root)
            requestWindowFeature(Window.FEATURE_NO_TITLE)

            setOnShowListener {
                if (binding.fullImageView.tag != photoFilePath) {
                    val scaledBitmap = getScaledBitmap(
                        photoFilePath,
                        binding.fullImageView.width,
                        binding.fullImageView.height
                    )
                    binding.fullImageView.setImageBitmap(scaledBitmap)
                    binding.fullImageView.tag = photoFilePath
                }
                binding.closeButton.setOnClickListener {
                    findNavController().popBackStack()
                }
            }
            show()
        }
        return dialog
    }
}
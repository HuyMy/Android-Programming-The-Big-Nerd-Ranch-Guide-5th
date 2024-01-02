package com.huymee.android.criminalintent

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.doOnLayout
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.huymee.android.criminalintent.databinding.FragmentCrimeDetailBinding
import kotlinx.coroutines.launch
import java.io.File
import java.io.Serializable
import java.util.Date

private const val TAG = "CrimeDetailFragment"
private const val READ_CONTACT_PERMISSION = android.Manifest.permission.READ_CONTACTS


class CrimeDetailFragment : Fragment() {

    private var _binding: FragmentCrimeDetailBinding? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {isGranted ->
            if (isGranted) {
                crimeDetailViewModel.isPermissionDenied = false
                callsSuspect()
            } else {
                Toast.makeText(
                    requireContext(), getString(R.string.permission_denied), Toast.LENGTH_SHORT
                ).show()
                crimeDetailViewModel.isPermissionDenied = true
            }
        }

    private val binding: FragmentCrimeDetailBinding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }


    private val args: CrimeDetailFragmentArgs by navArgs()

    private val crimeDetailViewModel: CrimeDetailViewModel by viewModels {
        CrimeDetailViewModelFactory(args.crimeId, requireActivity().application)
    }

    private val selectSuspect = registerForActivityResult(
        ActivityResultContracts.PickContact()
    ) {uri ->
        uri?.let { parseContactSelection(it) }
    }

    private val takePhoto = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { didTakePhoto: Boolean ->
        if (didTakePhoto && photoName != null) {
            crimeDetailViewModel.updateCrime { oldCrime ->
                oldCrime.copy(photoFileName = photoName)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (binding.crimeTitle.text.isBlank()) {
                Toast.makeText(
                    requireContext(),
                    "Please enter the crime's title",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                findNavController().popBackStack()
            }
        }
    }

    private var photoName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCrimeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            crimeTitle.doOnTextChanged { text, _, _, _ ->
                crimeDetailViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(title = text.toString())
                }
            }

            crimeSolved.setOnCheckedChangeListener { _, isChecked ->
                crimeDetailViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(isSolved = isChecked)
                }
            }

            crimeSuspect.setOnClickListener {
                selectSuspect.launch(null)
            }

            val selectSuspectIntent = selectSuspect.contract.createIntent(
                requireContext(),
                null
            )
            crimeSuspect.isEnabled = canResolveIntent(selectSuspectIntent)

            callSuspect.setOnClickListener {
                checkContactPermissionAndCall()
            }

            crimeCamera.setOnClickListener {
                photoName = "IMG_${Date().time}.JPG"
                val photoFile = File(requireContext().applicationContext.filesDir, photoName!!)
                val photoUri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.huymee.android.criminalintent.fileprovider",
                    photoFile
                )
                takePhoto.launch(photoUri)
            }

            val captureImageIntent = takePhoto.contract.createIntent(
                requireContext(),
                Uri.parse("")
            )

            crimeCamera.isEnabled = canResolveIntent(captureImageIntent)

            topAppBar.apply {
                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.delete_crime -> {
                            crimeDetailViewModel.deleteCrime()
                            findNavController().popBackStack()
                            true
                        }
                        else -> false
                    }
                }
                setNavigationOnClickListener {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                crimeDetailViewModel.crime.collect {crime ->
                    crime?.let { updateUi(it) }
                }
            }
        }

        setFragmentResultListener(
            REQUEST_KEY_DATE
        ) { _, bundle ->
            bundle.customGetSerializable<Date>(BUNDLE_KEY_DATE)?.let {newDate ->
                crimeDetailViewModel.updateCrime { it.copy(date = newDate) }
            }
        }
    }

    private fun updateUi(crime: Crime) {
        binding.apply {
            if (crimeTitle.text.toString() != crime.title) {
                crimeTitle.setText(crime.title)
            }
            crimeSolved.isChecked = crime.isSolved
            crimeDate.apply {
                text = Utils.getFullFormattedDate(crime.date)
                setOnClickListener {
                    findNavController().navigate(
                        CrimeDetailFragmentDirections.selectDate(crime.date)
                    )
                }
            }
            crimeTime.apply {
                text = Utils.getFormattedTime(crime.date)
                setOnClickListener {
                    findNavController().navigate(
                        CrimeDetailFragmentDirections.selectTime(crime.date)
                    )
                }
            }

            crimeReport.setOnClickListener {
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, getCrimeReport(crime))
                    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
                }.also {
                    startActivity(it)
                }
            }

            crimeSuspect.text = crime.suspect.ifEmpty {
                callSuspect.isEnabled = false
                getString(R.string.crime_suspect_text)
            }
        }
        updatePhoto(crime.photoFileName)
    }

    private fun getCrimeReport(crime: Crime): String {
        val solvedString = if (crime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }

        val dateString = Utils.getShortFormattedDate(crime.date)
        val suspectText = if (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }

        return getString(
            R.string.crime_report,
            crime.title, dateString, solvedString, suspectText
        )
    }

    private fun parseContactSelection(contactUri: Uri) {
        val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts._ID)

        val queryCursor = requireActivity().contentResolver
            .query(contactUri, queryFields, null, null, null)

        queryCursor?.use {cursor ->
            if (cursor.moveToFirst()) {
                val suspect = cursor.getString(0)
                val contactId = cursor.getString(1)
                crimeDetailViewModel.updateCrime {oldCrime ->
                    oldCrime.copy(suspect = suspect, contactId = contactId)
                }
                binding.callSuspect.isEnabled = true
            }
        }
    }

    private fun canResolveIntent(intent: Intent): Boolean {
        val packageManager: PackageManager = requireActivity().packageManager
        val resolvedActivity: ResolveInfo? =
            packageManager.resolveActivity(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
        return resolvedActivity != null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun callsSuspect() {
        crimeDetailViewModel.getPhoneNumber()?.let { phoneNumber ->
            Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }.also { startActivity(it) }
        } ?: Toast.makeText(requireContext(),
            "Cannot retrieve phone number",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun checkContactPermissionAndCall() = when {
        ContextCompat.checkSelfPermission(
            requireContext(), READ_CONTACT_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED -> {
            crimeDetailViewModel.isPermissionDenied = true
            callsSuspect()
        }
        ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(), READ_CONTACT_PERMISSION) -> {
            Snackbar.make(
                binding.root, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE
            ).setAction(android.R.string.ok) {
                requestPermissionLauncher.launch(READ_CONTACT_PERMISSION)
            }.show()
        }
        crimeDetailViewModel.isPermissionDenied -> openPermissionSettingDialog()
        else -> requestPermissionLauncher.launch(READ_CONTACT_PERMISSION)
    }

    private fun openPermissionSettingDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.message_permission_disabled)
            .setPositiveButton(getString(android.R.string.ok)) { dialog, _ ->
                Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", requireActivity().packageName, null)
                }.also {
                    startActivity(it)
                }
                dialog.cancel()
            }
            .setNegativeButton(getString(android.R.string.cancel)) { dialog, _ ->
                dialog.cancel()
            }
            .show().apply {
                setCanceledOnTouchOutside(true)
            }
    }

    private fun updatePhoto(photoFileName: String?) {
        if (binding.crimePhoto.tag != photoFileName) {
            val photoFile = photoFileName?.let {
                File(requireContext().applicationContext.filesDir, it)
            }

            if (photoFile?.exists() == true) {
                binding.crimePhoto.doOnLayout { measuredView ->
                    val scaledBitmap = getScaledBitmap(
                        photoFile.path,
                        measuredView.width,
                        measuredView.height
                    )
                    binding.crimePhoto.apply {
                        setImageBitmap(scaledBitmap)
                        tag = photoFileName
                        setOnClickListener {
                            findNavController().navigate(
                                CrimeDetailFragmentDirections.showImage(photoFile.path)
                            )
                        }
                        contentDescription = getString(R.string.crime_photo_image_description)
                    }
                }
            } else {
                binding.crimePhoto.apply {
                    tag = null
                    setImageBitmap(null)
                    contentDescription = getString(R.string.crime_photo_no_image_description)
                }
            }
        }
    }

    private inline fun <reified T : Serializable> Bundle.customGetSerializable(key: String): T? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getSerializable(key, T::class.java)
        } else {
            @Suppress("DEPRECATION") getSerializable(key) as? T
        }
}
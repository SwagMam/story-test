package com.example.submission1aplikasistory.ui.addstories

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.submission1aplikasistory.databinding.ActivityAddStoriesBinding
import java.io.File
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.submission1aplikasistory.R
import com.example.submission1aplikasistory.data.Resource
import com.example.submission1aplikasistory.helper.*
import com.example.submission1aplikasistory.ui.camera.CameraActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddStoriesActivity : AppCompatActivity(), View.OnClickListener {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_key")
    private var _binding: ActivityAddStoriesBinding? = null
    private val binding get() = _binding!!
    private var imgScaleZoom = true
    private var getFile: File? = null
    private lateinit var addStoriesViewModel: AddStoriesViewModel
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var location: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddStoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = resources.getString(R.string.addStory)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        binding.tvCurrentLocation.setOnClickListener{getMyLocation()}

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSION
            )
        }

        setupViewModel()
        setupView()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnCameraAdd -> startCameraX()
            binding.btnGaleryAdd -> startGallery()
            binding.btnUpload -> uploadImage(asGuest = false)
            binding.btnUploadAsGuest -> uploadImage(asGuest = true)
            binding.imgStoryAdd -> {
                imgScaleZoom = !imgScaleZoom
                binding.imgStoryAdd.scaleType = if (imgScaleZoom) ImageView.ScaleType.CENTER_CROP else ImageView.ScaleType.FIT_CENTER
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    resources.getString(R.string.not_geting_permission),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient?.lastLocation?.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    this.location = location
                    binding.tvCurrentLocation.text = parseAddressLocation(
                        this,
                        location.latitude,
                        location.longitude
                    )
                } else {
                    Toast.makeText(this, getString(R.string.toast_activate_location), Toast.LENGTH_LONG).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun uploadImage(asGuest: Boolean) {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)

            val description =
                binding.edtDesciptionAdd.text.toString()
                    .toRequestBody(resources.getString(R.string.text_plain).toMediaType())
            val requestImageFile = file.asRequestBody(resources.getString(R.string.image_jpeg).toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                resources.getString(R.string.photo),
                file.name,
                requestImageFile
            )

            if (location != null) {
                showLoading(true)
                val lat = location?.latitude?.toString()?.toRequestBody(resources.getString(R.string.text_plain).toMediaType())!!
                val lon = location?.longitude?.toString()?.toRequestBody(resources.getString(R.string.text_plain).toMediaType())!!
                CoroutineScope(Dispatchers.IO).launch {
                    addStoriesViewModel.upload(imageMultipart, description, asGuest, lat, lon)
                }
            } else {
                Toast.makeText(
                    this,
                    resources.getString(R.string.input_location),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                this,
                resources.getString(R.string.input_picture),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupView() {
        with(binding) {
            btnCameraAdd.setOnClickListener(this@AddStoriesActivity)
            btnGaleryAdd.setOnClickListener(this@AddStoriesActivity)
            btnUpload.setOnClickListener(this@AddStoriesActivity)
            btnUploadAsGuest.setOnClickListener(this@AddStoriesActivity)
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = resources.getString(R.string.image_all)
        val chooser = Intent.createChooser(intent, resources.getString(R.string.choose_a_picture))
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra(resources.getString(R.string.picture)) as File
            val isBackCamera = it.data?.getBooleanExtra(resources.getString(R.string.is_back_camera), true) as Boolean
            val result = rotateBitmap(
                BitmapFactory.decodeFile(myFile.path),
                isBackCamera
            )
            binding.imgStoryAdd.setImageBitmap(result)
            getFile = myFile
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@AddStoriesActivity)
            binding.imgStoryAdd.setImageURI(selectedImg)
            getFile = myFile
        }
    }

    private fun setupViewModel() {
        val pref = UserPreferences.getInstance(dataStore)
        addStoriesViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[AddStoriesViewModel::class.java]

        addStoriesViewModel.uploadInfo.observe(this) {
            when (it) {
                is Resource.Success -> {
                    Toast.makeText(this, it.data, Toast.LENGTH_SHORT).show()
                    finish()
                    showLoading(false)
                }
                is Resource.Loading -> showLoading(true)
                is Resource.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    showLoading(false)
                }
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun showLoading(isLoading: Boolean) {
        binding.isLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSION = 10
    }
}
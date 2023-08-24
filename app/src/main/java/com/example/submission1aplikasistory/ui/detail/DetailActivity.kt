package com.example.submission1aplikasistory.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.submission1aplikasistory.R
import com.example.submission1aplikasistory.data.model.Stories
import com.example.submission1aplikasistory.databinding.ActivityDetailBinding
import com.example.submission1aplikasistory.helper.parseAddressLocation

class DetailActivity : AppCompatActivity(), View.OnClickListener {

    private var _binding: ActivityDetailBinding? = null
    private val binding get() = _binding!!
    private var imageScaleZoom = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = resources.getString(R.string.detail_story)

        val story = intent.getParcelableExtra<Stories>(EXTRA_DATA)
        setupView(story)
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.imgStoryDetail -> {
                imageScaleZoom = !imageScaleZoom
                binding.imgStoryDetail.scaleType =
                    if (imageScaleZoom) ImageView.ScaleType.CENTER_CROP else ImageView.ScaleType.FIT_CENTER
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
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

    private fun setupView(story: Stories?) {
        with(binding) {
            Glide.with(imgStoryDetail.context)
                .load(story?.photoUrl)
                .centerCrop()
                .apply(RequestOptions.placeholderOf(R.drawable.placeholder))
                .into(imgStoryDetail)
            tvNameDetail.text = story?.name
            tvDescriptionDetail.text = story?.description
            if (story?.lon != null) {
                tvLocationDetail.visibility = View.VISIBLE
                tvLocationDetail.text =
                    story.lat?.let { parseAddressLocation(this@DetailActivity, it, story.lon) }
            } else {
                tvLocationDetail.visibility = View.GONE
            }
            imgStoryDetail.setOnClickListener(this@DetailActivity)
        }
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
    }
}
package com.example.submission1aplikasistory.ui.main

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.submission1aplikasistory.R
import com.example.submission1aplikasistory.data.model.Stories
import com.example.submission1aplikasistory.databinding.ItemStoryBinding
import com.example.submission1aplikasistory.ui.detail.DetailActivity

class MainAdapter:
    PagingDataAdapter<Stories, MainAdapter.MainViewHolder>(DIFF_CALLBACK) {

    inner class MainViewHolder(private val binding: ItemStoryBinding):
        RecyclerView.ViewHolder(binding.root){
            fun bind(story: Stories) {
                with(binding) {
                    Glide.with(imgStory)
                        .load(story.photoUrl)
                        .centerCrop()
                        .apply(RequestOptions.placeholderOf(R.drawable.placeholder))
                        .into(imgStory)
                    tvName.text = story.name
                    root.setOnClickListener{
                        val optionsCompat: ActivityOptionsCompat =
                            ActivityOptionsCompat.makeSceneTransitionAnimation(
                                root.context as Activity,
                                Pair(imgStory, "imageDetail"),
                                Pair(tvName, "nameDetail")
                            )
                        val detailIntent = Intent(root.context, DetailActivity::class.java)
                        detailIntent.putExtra(DetailActivity.EXTRA_DATA, story)
                        root.context.startActivity(detailIntent, optionsCompat.toBundle())
                    }
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder = MainViewHolder(
        ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Stories>() {
            override fun areItemsTheSame(oldItem: Stories, newItem: Stories): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Stories, newItem: Stories): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}
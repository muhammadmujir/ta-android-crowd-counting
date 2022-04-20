package com.final_project.crowd_counting.base.view.rv.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.final_project.crowd_counting.databinding.ItemNetworkStateBinding

class PagingLoadStateAdapter<T : Any, VH : RecyclerView.ViewHolder>(
  private val adapter: PagingDataAdapter<T,VH>
) : LoadStateAdapter<PagingLoadStateAdapter.NetworkStateItemViewHolder>(){

  override fun onBindViewHolder(holder: NetworkStateItemViewHolder, loadState: LoadState) {
    holder.bind(loadState)
  }

  override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): NetworkStateItemViewHolder =
    NetworkStateItemViewHolder(
      ItemNetworkStateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    ) { adapter.retry() }

  class NetworkStateItemViewHolder(
    private val binding: ItemNetworkStateBinding,
    private val retryCallback: () -> Unit
  ) : RecyclerView.ViewHolder(binding.root) {

    init {
      binding.retryButton.setOnClickListener { retryCallback() }
    }

    fun bind(loadState: LoadState) {
      with(binding) {
        progressBar.isVisible = loadState is LoadState.Loading
        retryButton.isVisible = loadState is LoadState.Error
        errorMsg.isVisible =
          !(loadState as? LoadState.Error)?.error?.message.isNullOrBlank()
        errorMsg.text = (loadState as? LoadState.Error)?.error?.message
      }
    }
  }
}
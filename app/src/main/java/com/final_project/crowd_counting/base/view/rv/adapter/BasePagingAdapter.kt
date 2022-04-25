package com.final_project.crowd_counting.base.view.rv.adapter

import android.view.View
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class BasePagingAdapter<T:Any>(
  private val objectComparator: DiffUtil.ItemCallback<T>
): PagingDataAdapter<T, BasePagingAdapter.BasePagingViewHolder<T>>(objectComparator) {

  override fun onBindViewHolder(holder: BasePagingViewHolder<T>, position: Int) {
    getItem(position)?.let { holder.bind(it, position) }
  }

  abstract class BasePagingViewHolder<T>(itemView: View): RecyclerView.ViewHolder(itemView){
    abstract fun bind(data: T, position: Int? = null)
  }

  fun withLoadState(
    header: LoadStateAdapter<*>,
    footer: LoadStateAdapter<*>
  ): ConcatAdapter {
    addLoadStateListener { loadStates ->
      if (!(loadStates.refresh is LoadState.NotLoading))
        header.loadState = loadStates.refresh
      else
        header.loadState = loadStates.prepend
      footer.loadState = loadStates.append
    }
    return ConcatAdapter(header, this, footer)
  }
}
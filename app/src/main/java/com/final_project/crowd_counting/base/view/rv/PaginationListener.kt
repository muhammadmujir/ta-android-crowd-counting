package com.final_project.crowd_counting.base.view.rv

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class PaginationListener(
  private val layoutManager: RecyclerView.LayoutManager,
  private val itemPerPage: Int = 10
): RecyclerView.OnScrollListener() {

  override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
    super.onScrolled(recyclerView, dx, dy)
    val visibleItemCount = layoutManager.childCount
    val totalItemCount = layoutManager.itemCount
    var firstVisibleItemPosition = 0
    if (layoutManager is LinearLayoutManager) {
      firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
    } else if (layoutManager is GridLayoutManager){
      firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
    }
    if (
      !isLoading() && !isLastPage()
      && visibleItemCount + firstVisibleItemPosition >= totalItemCount
      && firstVisibleItemPosition >= 0
    ){
      loadMoreItems()
    }
  }

  protected abstract fun loadMoreItems()
  abstract fun isLastPage(): Boolean
  abstract fun isLoading(): Boolean
}
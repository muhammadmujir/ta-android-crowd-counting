package com.final_project.crowd_counting.base.view.rv

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpacingItemDecoration(
  private val leftMargin: Int = 0,
  private val topMargin: Int = 0,
  private val rightMargin: Int = 0,
  private val bottomMargin: Int = 0
): RecyclerView.ItemDecoration() {
  override fun getItemOffsets(
    outRect: Rect,
    view: View,
    parent: RecyclerView,
    state: RecyclerView.State
  ) {
    super.getItemOffsets(outRect, view, parent, state)
    outRect.left = leftMargin
    outRect.top = topMargin
    outRect.right = rightMargin
    outRect.bottom = bottomMargin
  }
}
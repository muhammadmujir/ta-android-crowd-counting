package com.final_project.crowd_counting.base.view.custom_view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import com.final_project.crowd_counting.R
import org.videolan.libvlc.util.VLCVideoLayout

class CustomVLCVideoLayout(context: Context, attrs: AttributeSet?): VLCVideoLayout(context, attrs) {

  private val customWidth: Int
  private val customeHeight: Int

  init {
    context.theme.obtainStyledAttributes(attrs, R.styleable.CustomVLCVideoLayout, 0, 0).apply {
      try {
        customWidth = getLayoutDimension(R.styleable.CustomVLCVideoLayout_custom_width, ViewGroup.LayoutParams.MATCH_PARENT)
        customeHeight = getLayoutDimension(R.styleable.CustomVLCVideoLayout_custom_height, ViewGroup.LayoutParams.WRAP_CONTENT)
      } finally {
        recycle()
      }
    }
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    layoutParams.width = customWidth
    layoutParams.height = customeHeight
  }

}
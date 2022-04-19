package com.final_project.crowd_counting.base.communicator

import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.MenuRes
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LifecycleOwner

interface IToolbarCommunicator {
  fun setTitleAndDescription(mTitle: String, mDescription: String?)
  fun setCustomeTitle(mTitle: String?)
  fun setCustomeImage(url: String?)
  fun setLogo(drawable: Drawable?)
  fun setOnNavigateBack(owner: LifecycleOwner, clickListener: View.OnClickListener?, isVisible: Boolean)
  fun setSearchView(owner: LifecycleOwner, hint: String?, initText: String?,
    onKeyboardSearch: ((String) -> Unit)?, afterTextChangedActoin: ((String) -> Unit)?,
    onFocusAction: ((view: View, b: Boolean) -> Unit)?
  )
  fun getCurrentSearchText(): String
  fun inflateMenu(@MenuRes menuResId: Int?, isAppendMenu: Boolean)
  fun setOnMenuItemClickListener(owner: LifecycleOwner, listener: Toolbar.OnMenuItemClickListener?)
  fun resetToolbarView()
  fun setToolbarVisibility(isShow: Boolean)
}
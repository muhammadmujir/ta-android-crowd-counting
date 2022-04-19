package com.final_project.crowd_counting.base.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheetDialogFragmentOnlyUI<VB: ViewBinding>: BottomSheetDialogFragment() {
  private var _viewBinding: VB? = null
  protected val viewBinding: VB get() = _viewBinding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    _viewBinding = getFragmentBinding(inflater, container)
    return viewBinding.root
  }

  abstract protected fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) : VB

}
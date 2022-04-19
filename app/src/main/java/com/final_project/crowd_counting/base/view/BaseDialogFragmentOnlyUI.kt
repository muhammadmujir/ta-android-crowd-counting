package com.final_project.crowd_counting.base.view

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.final_project.crowd_counting.base.utils.Util

abstract class BaseDialogFragmentOnlyUI<VB: ViewBinding>: DialogFragment() {
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

  protected open fun getDimension(): Pair<Int, Int>{
    return if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
      Pair(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    } else {
      val dimenPixel = Util.getScreenDimension(requireContext(), false)
      Pair(dimenPixel.second, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
  }

  override fun onStart() {
    super.onStart()
    dialog?.window?.setLayout(
      getDimension().first,
      getDimension().second
    )
  }

}
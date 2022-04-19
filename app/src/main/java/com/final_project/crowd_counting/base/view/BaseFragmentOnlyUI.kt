package com.final_project.crowd_counting.base.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.final_project.crowd_counting.base.communicator.ActivityObserver
import com.final_project.crowd_counting.base.communicator.IToolbarCommunicator

abstract class BaseFragmentOnlyUI<VB: ViewBinding>: Fragment() {
  private var _viewBinding: VB? = null
  protected val viewBinding: VB get() = _viewBinding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    if (shouldResetToolbarView()){
      requireActivity().lifecycle.addObserver(ActivityObserver{
        if (requireActivity() is IToolbarCommunicator){
          (requireActivity() as IToolbarCommunicator).resetToolbarView()
        }
      })
    }
    _viewBinding = getFragmentBinding(inflater, container)
    return viewBinding.root
  }

  abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) : VB

  abstract fun shouldResetToolbarView(): Boolean

  override fun onDestroyView() {
    super.onDestroyView()
    _viewBinding = null
  }

}
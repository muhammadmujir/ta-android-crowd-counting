package com.final_project.crowd_counting.base.view

import android.os.Bundle
import android.view.View
import androidx.viewbinding.ViewBinding
import com.final_project.crowd_counting.base.communicator.ILoadingCommunicator
import com.final_project.crowd_counting.base.communicator.IParentDialogCommunicator

abstract class BaseDialogFragment<VB: ViewBinding, VM: BaseViewModel>: BaseDialogFragmentOnlyUI<VB>(),
  ILoadingCommunicator {

  protected var mIParentParentCommunicator: IParentDialogCommunicator? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    mIParentParentCommunicator = parentFragment as? IParentDialogCommunicator
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    getVM().loading.observe(viewLifecycleOwner, {
      if (it.isLoading){
        isLoading(true)
      } else if (!it.retryCallback.isNullOrEmpty()){
        onError(it.message, it.retryCallback, it.startLoading)
      } else {
        isLoading(false)
      }
    })
  }

  abstract fun getVM(): VM

  override fun isLoading(loading: Boolean) {
    if (loading){
      (childFragmentManager.findFragmentByTag(ProgressDialogFragment.PROGRESS_LOADING) as? ProgressDialogFragment)?.dismiss()
      ProgressDialogFragment().show(childFragmentManager, ProgressDialogFragment.PROGRESS_LOADING)
    } else {
      dismissLoadingDialog()
    }
  }

  override fun onError(
    message: String, callbacks: Collection<() -> Unit>?, startLoading: ((Int) -> Unit)?
  ) {
    (childFragmentManager.findFragmentByTag(ProgressDialogFragment.PROGRESS_LOADING) as? ILoadingCommunicator)?.onError(message, callbacks, startLoading)
  }

  private fun dismissLoadingDialog(){
    childFragmentManager.fragments.filterIsInstance<ProgressDialogFragment>().forEach {
      it.dismiss()
    }
  }
}
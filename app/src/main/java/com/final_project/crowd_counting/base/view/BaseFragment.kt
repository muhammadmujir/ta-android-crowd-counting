package com.final_project.crowd_counting.base.view

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.viewbinding.ViewBinding
import com.final_project.crowd_counting.base.R
import com.final_project.crowd_counting.base.communicator.ILoadingCommunicator
import com.final_project.crowd_counting.base.view.ProgressDialogFragment.Companion.PROGRESS_LOADING

abstract class BaseFragment<VB:ViewBinding, VM: BaseViewModel>: BaseFragmentOnlyUI<VB>(), ILoadingCommunicator {

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    Log.d("baseFragObserve", "yes")
    getVM().loading.observe(viewLifecycleOwner, {
      Log.d("baseFragNotify", "yes")
      if (shouldHandleLoading()){
        if (it.isLoading){
          isLoading(true)
        } else if (!it.retryCallback.isNullOrEmpty()){
          onError(it.message, it.retryCallback, it.startLoading)
        } else {
          isLoading(false)
        }
      } else{
        (activity as ILoadingCommunicator).apply {
          if (it.isLoading){
            isLoading(true)
          } else if (!it.retryCallback.isNullOrEmpty()){
            onError(it.message, it.retryCallback, it.startLoading)
          } else {
            isLoading(false)
          }
        }
      }
    })
  }

  abstract fun getVM(): VM

  abstract fun shouldHandleLoading(): Boolean

  protected fun dialogConfirmation(message: String, callback: () -> Unit){
    val dialogFragment = ConfirmationDialogFragment.newInstance(message).apply {
      setResponseCommunicator(object : IResponseCommunicator {
        override fun onResponse(
          dialogResponse: DialogResponse,
          data: Bundle?
        ) {
          if (dialogResponse == DialogResponse.YES){
            callback()
          }
          dismiss()
        }
      })
    }
    childFragmentManager.beginTransaction().add(dialogFragment, null).commit()
  }

  override fun isLoading(loading: Boolean) {
    if (loading){
      Log.d("LOADING FRAG", "YES")
      (childFragmentManager.findFragmentByTag(PROGRESS_LOADING) as? ProgressDialogFragment)?.dismiss()
      ProgressDialogFragment().show(childFragmentManager, PROGRESS_LOADING)
    } else {
      Log.d("NOT LOADING FRAG", "YES")
      dismissLoadingDialog()
    }
  }

  override fun onError(message: String, callbacks: Collection<() -> Unit>?, startLoading: ((Int) -> Unit)?) {
    Log.d("ERROR FRAG", "YES")
    (childFragmentManager.findFragmentByTag(PROGRESS_LOADING) as? ILoadingCommunicator)?.onError(message, callbacks, startLoading)
  }

  private fun dismissLoadingDialog(){
    childFragmentManager.fragments.filterIsInstance<ProgressDialogFragment>().forEach {
      it.dismiss()
    }
  }
}
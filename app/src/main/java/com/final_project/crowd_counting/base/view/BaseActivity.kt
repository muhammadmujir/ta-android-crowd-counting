package com.final_project.crowd_counting.base.view

import androidx.appcompat.app.AppCompatActivity
import com.final_project.crowd_counting.base.communicator.ILoadingCommunicator
import com.final_project.crowd_counting.base.view.ProgressDialogFragment.Companion.PROGRESS_LOADING

abstract class BaseActivity: AppCompatActivity(), ILoadingCommunicator {

  override fun isLoading(loading: Boolean) {
    if (loading){
      ProgressDialogFragment().show(supportFragmentManager, PROGRESS_LOADING)
    } else {
      dismissLoadingDialog()
    }
  }

  override fun onError(message: String, callbacks: Collection<() -> Unit>?, startLoading: ((Int) -> Unit)?) {
    (supportFragmentManager.findFragmentByTag(PROGRESS_LOADING) as ILoadingCommunicator).onError(message, callbacks, startLoading)
  }

  private fun dismissLoadingDialog(){
    supportFragmentManager.fragments.filterIsInstance<ProgressDialogFragment>().forEach {
      it.dismiss()
    }
  }
}
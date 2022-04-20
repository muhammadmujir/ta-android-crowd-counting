package com.final_project.crowd_counting.base.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.final_project.crowd_counting.R
import com.final_project.crowd_counting.base.communicator.ILoadingCommunicator
import com.final_project.crowd_counting.databinding.FragmentProgressDialogBinding
import java.util.*

class ProgressDialogFragment : BaseDialogFragmentOnlyUI<FragmentProgressDialogBinding>(),
  ILoadingCommunicator {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    isCancelable = false
  }

  override fun getFragmentBinding(
    inflater: LayoutInflater, container: ViewGroup?
  ): FragmentProgressDialogBinding {
    return FragmentProgressDialogBinding.inflate(inflater, container, false)
  }

  override fun getDimension(): Pair<Int, Int> {
    return Pair(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
  }

  override fun isLoading(loading: Boolean) {
    viewBinding.groupError.isVisible = !loading
    viewBinding.progressBar.isVisible = loading
  }

  override fun onError(message: String, callbacks: Collection<() -> Unit>?, startLoading: ((Int) -> Unit)?) {
    with(viewBinding){
      progressBar.isVisible = false
      groupError.isVisible = true
      tvErrorMessage.text = message
      btnRetry.setOnClickListener {
        callbacks?.let {
          val callbackSize = it.size
          startLoading?.let { start -> start(callbackSize) }
          for (i in 1..callbackSize){
            Log.d("CALL ", "RETRY")
            (callbacks as? Queue)?.poll()?.let { call ->
              call()
            }
          }
        }
      }
    }
  }

  companion object {
    const val PROGRESS_LOADING = "ProgressFragmentLoading"
  }
}
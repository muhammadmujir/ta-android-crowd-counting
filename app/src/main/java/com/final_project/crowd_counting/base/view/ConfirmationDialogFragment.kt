package com.final_project.crowd_counting.base.view

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.final_project.crowd_counting.base.utils.Util
import com.final_project.crowd_counting.databinding.FragmentDialogConfirmationBinding

class ConfirmationDialogFragment: BaseDialogFragmentOnlyUI<FragmentDialogConfirmationBinding>() {

  private lateinit var iResponseCommunicator: IResponseCommunicator
  private var message: String = ""
  private var spannable: SpannableStringBuilder? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    message = arguments?.getString(KEY_MESSAGE).orEmpty()
    retainInstance = true
  }

  override fun getFragmentBinding(
    inflater: LayoutInflater, container: ViewGroup?
  ): FragmentDialogConfirmationBinding {
    return FragmentDialogConfirmationBinding.inflate(inflater, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    with(viewBinding){
      tvMessage.text = if (spannable == null) Util.makeBold(message, 0, message.length) else spannable
      btnYes.setOnClickListener {
        if (this@ConfirmationDialogFragment::iResponseCommunicator.isInitialized){
          iResponseCommunicator.onResponse(DialogResponse.YES, null)

        }
      }
      btnNo.setOnClickListener {
        if (this@ConfirmationDialogFragment::iResponseCommunicator.isInitialized){
          iResponseCommunicator.onResponse(DialogResponse.NO, null)
        }
      }
    }
  }

  override fun getDimension(): Pair<Int, Int> {
    return Pair(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
  }

  fun setResponseCommunicator(iCommunicator: IResponseCommunicator){
    iResponseCommunicator = iCommunicator
  }

  fun setSpannableStringBuilder(spannable: SpannableStringBuilder){
    this.spannable = spannable
  }

  companion object{
    const val KEY_DATA = "key_data"
    const val KEY_MESSAGE = "key_message"

    fun newInstance(message: String): ConfirmationDialogFragment{
      return ConfirmationDialogFragment().apply {
        arguments = Bundle().apply { putString(KEY_MESSAGE, message) }
      }
    }
  }

}

interface IResponseCommunicator{
  fun onResponse(dialogResponse: DialogResponse, data: Bundle?)
}

enum class DialogResponse{
  YES, NO
}
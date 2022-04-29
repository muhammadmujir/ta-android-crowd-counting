package com.final_project.crowd_counting.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.final_project.crowd_counting.R
import com.final_project.crowd_counting.base.source.network.ResponseWrapper
import com.final_project.crowd_counting.base.view.BaseDialogFragment
import com.final_project.crowd_counting.databinding.FragmentEditPasswordDialogBinding
import com.final_project.crowd_counting.profile.model.UpdatePasswordRequest
import com.final_project.crowd_counting.profile.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditPasswordDialogFragment : BaseDialogFragment<FragmentEditPasswordDialogBinding, UserViewModel>() {

  private val viewModel: UserViewModel by viewModels()

  override fun getVM(): UserViewModel = viewModel

  override fun getFragmentBinding(
    inflater: LayoutInflater, container: ViewGroup?
  ): FragmentEditPasswordDialogBinding {
    return FragmentEditPasswordDialogBinding.inflate(inflater, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    with(viewBinding){
      ibClose.setOnClickListener { dismiss() }
      btnSave.setOnClickListener {
        if (validateInput()){
          btnSave.isVisible = false
          progressBar.isVisible = true
          viewModel.updatePassword(UpdatePasswordRequest(
            viewBinding.etOldPassword.editText?.text.toString(),
            viewBinding.etNewPassword.editText?.text.toString()
          )
          )
        }
      }
    }
    initObserver()
  }

  private fun validateInput(): Boolean{
    var status = true
    with(viewBinding){
      if (etOldPassword.editText?.text.isNullOrEmpty()){
        etOldPassword.error = getString(R.string.empty_not_allowed_message, "Password Lama")
      } else {
        etOldPassword.error = null
      }
      if (etNewPassword.editText?.text.isNullOrEmpty()){
        etNewPassword.error = getString(R.string.empty_not_allowed_message, "Password Baru")
        status = false
      } else if(!etNewPassword.editText?.text.contentEquals(etConfirmNewPassword.editText?.text)){
        etNewPassword.error = getString(R.string.password_not_matched)
        status = false
      } else {
        etNewPassword.error = null
      }
    }
    return status
  }

  private fun initObserver(){
    viewModel.updateUser.observe(viewLifecycleOwner, { event ->
      event.getContentIfNotHandled()?.let {
        if (it.second.status == ResponseWrapper.Status.SUCCESS){
          Toast.makeText(context, getString(R.string.message_update_success, "password"), Toast.LENGTH_SHORT).show()
          dismiss()
        } else {
          Toast.makeText(context, it.second.body?.errors.toString(), Toast.LENGTH_SHORT).show()
          viewBinding.btnSave.isVisible = true
          viewBinding.progressBar.isVisible = false
        }
      }
    })
  }
}
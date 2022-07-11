package com.final_project.crowd_counting.profile

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.commitNow
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.final_project.crowd_counting.R
import com.final_project.crowd_counting.auth.AuthenticationActivity
import com.final_project.crowd_counting.base.communicator.ActivityObserver
import com.final_project.crowd_counting.base.communicator.IToolbarCommunicator
import com.final_project.crowd_counting.base.constant.Constant.USER_IMAGE
import com.final_project.crowd_counting.base.source.network.ResponseWrapper
import com.final_project.crowd_counting.base.source.session.SessionManager
import com.final_project.crowd_counting.base.utils.Util.getScreenDimension
import com.final_project.crowd_counting.base.utils.Util.loadCircularImage
import com.final_project.crowd_counting.base.utils.Util.validateEmpty
import com.final_project.crowd_counting.base.view.BaseFragment
import com.final_project.crowd_counting.base.view.CrowdCountingApp
import com.final_project.crowd_counting.databinding.FragmentProfileBinding
import com.final_project.crowd_counting.profile.model.UpdateProfileRequest
import com.final_project.crowd_counting.profile.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding, UserViewModel>() {
  private val viewModel: UserViewModel by viewModels()
  private var isEditProfile = false
  private var isSubmit = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    savedInstanceState?.let {
      isEditProfile = it.getBoolean(KEY_IS_EDIT_PROFILE)
    }
  }

  override fun getVM(): UserViewModel = viewModel

  override fun shouldHandleLoading(): Boolean = true

  override fun getFragmentBinding(
    inflater: LayoutInflater, container: ViewGroup?
  ): FragmentProfileBinding {
    requireActivity().lifecycle.addObserver(ActivityObserver{
      (requireActivity() as IToolbarCommunicator).setToolbarVisibility(false)
    })
    return FragmentProfileBinding.inflate(inflater, container, false)
  }

  override fun shouldResetToolbarView(): Boolean = true

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    with(viewBinding) {
      setProfileFormVisibility()
      registerFileManagerCallback{ imagePath?.let { ivProfile.loadCircularImage(it) } }
      ivProfile.setOnClickListener {
        if (isEditProfile) openFileManager()
      }
      btnEditProfile.setOnClickListener {
        if (isEditProfile) {
          isSubmit = true
          updateProfile()
        } else {
          showEditProfileForm()
        }
      }
      btnCancel.setOnClickListener {
        isEditProfile = false
        setProfileFormVisibility()
      }
      btnMyCamera.setOnClickListener {
        findNavController().navigate(R.id.to_camera_list)
      }
      btnChangePassword.setOnClickListener { showEditPasswordDialog() }
      btnLogout.setOnClickListener { logout() }
      if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        val dimenPixel = getScreenDimension(root.context, false)
        etName.layoutParams = ConstraintLayout.LayoutParams(
          dimenPixel.second, ConstraintLayout.LayoutParams.WRAP_CONTENT
        ).apply {
          startToStart = R.id.cl_container
          endToEnd = R.id.cl_container
          topToBottom = R.id.iv_profile
          bottomToTop = R.id.et_email
        }
        etEmail.layoutParams = ConstraintLayout.LayoutParams(
          dimenPixel.second, ConstraintLayout.LayoutParams.WRAP_CONTENT
        ).apply {
          startToStart = R.id.cl_container
          endToEnd = R.id.cl_container
          topToBottom = R.id.et_name
          bottomToTop = R.id.barrier_above_button
        }
        ivBg.visibility = View.GONE
        llContainer.layoutParams = ConstraintLayout.LayoutParams(
          dimenPixel.second, ConstraintLayout.LayoutParams.WRAP_CONTENT
        ).apply {
          startToStart = R.id.cl_container
          endToEnd = R.id.cl_container
          topToBottom = R.id.btn_edit_profile
          topMargin = (30 * resources.displayMetrics.density).toInt()
          bottomMargin = (16 * resources.displayMetrics.density).toInt()
        }
      }
    }
    initObserver()
    if (viewModel.updateUser.value == null)
      viewModel.getCurrentUser()
  }

  private fun initObserver(){
    viewModel.updateUser.observe(viewLifecycleOwner) { event ->
      event.peekContent().let {
        if (it.second.status == ResponseWrapper.Status.SUCCESS) {
          it.second.body?.data?.let { user ->
            with(viewBinding) {
              if (it.first in 0..1) {
                ivProfile.loadCircularImage(USER_IMAGE + it.second.body?.data?.id)
                tvEmail.text = user.email
                tvName.text = user.name
                if (isSubmit) {
                  isEditProfile = false
                  isSubmit = false
                }
                setProfileFormVisibility()
              }
              event.getContentIfNotHandled()?.let { _ ->
                if (it.first == 1)
                  Toast.makeText(
                    requireContext(),
                    getString(R.string.message_update_success, "profile"),
                    Toast.LENGTH_SHORT
                  ).show()
                if (it.first == 2)
                  Toast.makeText(
                    requireContext(),
                    getString(R.string.message_update_success, "password"),
                    Toast.LENGTH_SHORT
                  ).show()
              }
            }
          }
        } else {
          event.getContentIfNotHandled()?.let { _ ->
            Toast.makeText(
              requireContext(),
              it.second.body?.errors?.getOrNull(0).toString(),
              Toast.LENGTH_SHORT
            ).show()
          }
        }
      }
    }

    viewModel.updatePicture.observe(viewLifecycleOwner) {
      it.getContentIfNotHandled()?.let {
        if (it.status == ResponseWrapper.Status.SUCCESS) {
          Toast.makeText(
            requireContext(),
            getString(R.string.message_update_success, "picture"),
            Toast.LENGTH_SHORT
          ).show()
        } else {
          Toast.makeText(
            requireContext(),
            it.body?.errors?.getOrNull(0)?.toString(),
            Toast.LENGTH_SHORT
          ).show()
        }
      }
    }
  }

  private fun showEditProfileForm(){
    with(viewBinding){
      isEditProfile = true
      setProfileFormVisibility()
      etName.setText(tvName.text)
      etEmail.setText(tvEmail.text)
    }
  }

  private fun setProfileFormVisibility(){
    with(viewBinding){
      setViewOnEditProfile()
      groupTextView.isVisible = !isEditProfile
      groupEditText.isVisible = isEditProfile
    }
  }

  private fun setViewOnEditProfile(){
    with(viewBinding){
      if (!isEditProfile){
        ivBg.alpha = 1.0f
        llContainer.alpha = 1.0f
        btnMyCamera.isEnabled = true
        btnChangePassword.isEnabled = true
        btnLogout.isEnabled = true
        btnEditProfile.text = getString(R.string.edit_profile)
      } else {
        ivProfile.colorFilter = null
        ivBg.alpha = 0.4f
        llContainer.alpha = 0.4f
        btnMyCamera.isEnabled = false
        btnChangePassword.isEnabled = false
        btnLogout.isEnabled = false
        btnEditProfile.text = getString(R.string.save)
      }
    }
  }

  private fun updateProfile(){
    with(viewBinding){
      if (validateEmpty(listOf(etName, etEmail), listOf(getString(R.string.full_name), getString(R.string.email_address)))){
        viewModel.updateProfile(UpdateProfileRequest(etEmail.text.toString(), etName.text.toString()))
      }
    }
    createFileBody()?.let {
      viewModel.uploadImage(it)
    }
  }

  private fun showEditPasswordDialog(){
    childFragmentManager.commitNow {
      add(EditPasswordDialogFragment(), null)
    }
  }

  private fun logout(){
    dialogConfirmation(getString(R.string.are_you_sure_to_logout)) {
      SessionManager(requireContext()).clearSharedPreference()
      (requireContext().applicationContext as CrowdCountingApp).run {
        userToken = null
        refreshToken = null
      }
      startActivity(Intent(requireActivity(), AuthenticationActivity::class.java))
      requireActivity().finish()
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putBoolean(KEY_IS_EDIT_PROFILE, isEditProfile)
  }

  companion object{
    private const val KEY_IS_EDIT_PROFILE = "keyIsEditProfile"
    private const val IMAGE_PATH = "imagePath"
  }
}
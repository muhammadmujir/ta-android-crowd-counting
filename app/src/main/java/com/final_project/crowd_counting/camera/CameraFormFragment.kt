package com.final_project.crowd_counting.camera

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.final_project.crowd_counting.R
import com.final_project.crowd_counting.base.communicator.ActivityObserver
import com.final_project.crowd_counting.base.communicator.IToolbarCommunicator
import com.final_project.crowd_counting.base.constant.Constant.CAMERA_IMAGE
import com.final_project.crowd_counting.base.model.Camera
import com.final_project.crowd_counting.base.source.network.ResponseWrapper
import com.final_project.crowd_counting.base.utils.Util
import com.final_project.crowd_counting.base.utils.Util.loadImage
import com.final_project.crowd_counting.base.utils.Util.validateEmpty
import com.final_project.crowd_counting.base.view.BaseFragment
import com.final_project.crowd_counting.camera.model.CameraRequest
import com.final_project.crowd_counting.camera.viewmodel.CameraViewModel
import com.final_project.crowd_counting.camera.viewmodel.CameraViewModel.Companion.CREATE_KEY
import com.final_project.crowd_counting.databinding.FragmentCameraFormBinding
import dagger.hilt.android.AndroidEntryPoint

const val ARG_CAMERA = "argCamera"

@AndroidEntryPoint
class CameraFormFragment : BaseFragment<FragmentCameraFormBinding, CameraViewModel>() {
  private var camera: Camera? = null
  private val viewModel: CameraViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    arguments?.let {
      camera = it.getParcelable(ARG_CAMERA)
    }
    requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
      override fun handleOnBackPressed() {
         if (!redirectToPreviousScreen()) {
          isEnabled = false
          activity?.onBackPressed()
        }
      }
    })
  }

  override fun getVM(): CameraViewModel = viewModel

  override fun shouldHandleLoading(): Boolean = true

  override fun getFragmentBinding(
    inflater: LayoutInflater, container: ViewGroup?
  ): FragmentCameraFormBinding {
    requireActivity().lifecycle.addObserver(ActivityObserver{
      (requireActivity() as IToolbarCommunicator).run {
        setOnNavigateBack(this@CameraFormFragment, {
          redirectToPreviousScreen()
        }, true)
        setTitleAndDescription(getString(R.string.camera_form), null)
      }
    })
    return FragmentCameraFormBinding.inflate(inflater, container, false)
  }

  override fun shouldResetToolbarView(): Boolean = true

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    if (savedInstanceState == null) initData()
    registerFileManagerCallback{ imagePath?.let { loadCameraImage(it) } }
    viewBinding.ivCameraImage.setOnClickListener { openFileManager() }
    viewBinding.tvUploadImage.setOnClickListener { openFileManager() }
    viewBinding.btnSave.setOnClickListener { submitData() }
    initObser()
  }

  private fun initObser(){
    viewModel.camera.observe(viewLifecycleOwner) { event ->
      event.getContentIfNotHandled()?.let {
        if (it.second.status == ResponseWrapper.Status.SUCCESS) {
          uploadImage()
          if (it.first == CREATE_KEY)
            Toast.makeText(
              requireContext(),
              getString(R.string.message_create_success, getString(R.string.camera)),
              Toast.LENGTH_SHORT
            ).show()
          else
            Toast.makeText(
              requireContext(),
              getString(R.string.message_update_success, getString(R.string.camera)),
              Toast.LENGTH_SHORT
            ).show()
        } else {
          Toast.makeText(
            requireContext(),
            it.second.body?.errors?.firstOrNull().toString(),
            Toast.LENGTH_SHORT
          ).show()
        }
      }
    }

    viewModel.updatePicture.observe(viewLifecycleOwner) { event ->
      event.getContentIfNotHandled()?.let {
        if (it.status == ResponseWrapper.Status.SUCCESS) {
          Toast.makeText(
            requireContext(),
            getString(R.string.message_update_success, "image"),
            Toast.LENGTH_SHORT
          ).show()
        } else {
          Toast.makeText(
            requireContext(),
            it.body?.errors?.firstOrNull().toString(),
            Toast.LENGTH_SHORT
          ).show()
        }
      }
    }
  }

  private fun initData(){
    camera?.let {
      with(viewBinding){
        loadCameraImage(CAMERA_IMAGE+it.id)
        etRtspAddress.setText(it.rtspAddress)
        etLocation.setText(it.location)
        etArea.setText(it.area.toString())
        etMaxCrowd.setText(it.maxCrowdCount.toString())
        etDesc.setText(it.description)
        swActive.isChecked = it.isActive
        swPublic.isChecked = it.isPublic
      }
    }
  }

  private fun loadCameraImage(url: String){
    with(viewBinding){
      ivCameraImage.loadImage(url = url, errorListener = { e, target ->
        target?.onLoadFailed(null)
        tvUploadImage.isVisible = true
      }, successListner = {
        tvUploadImage.isVisible = false
      }, radius = listOf(10.0F,10.0F,10.0F,10.0F))
    }
  }

  private fun isThereUpdate(): Boolean{
    with(viewBinding){
      camera?.let {
        if (
          etRtspAddress.text.toString() != it.rtspAddress ||
          etLocation.text.toString() != it.location ||
          etArea.text.toString() != it.area.toString() ||
          etMaxCrowd.text.toString() != it.maxCrowdCount.toString() ||
          etDesc.text.toString() != it.description ||
          swActive.isChecked != it.isActive ||
          swPublic.isChecked != it.isPublic ||
          imagePath != null){
          return true
        }
      }
    }
    return false
  }

  private fun validateEmptyField(): Boolean{
    with(viewBinding){
      return validateEmpty(listOf(etRtspAddress,etLocation,etArea,etMaxCrowd), listOf(
        getString(R.string.rtsp_address),getString(R.string.location),
        getString(R.string.area), getString(R.string.max_crowd_count)
      ))
    }
  }

  private fun generateCameraRequest(): CameraRequest{
    return with(viewBinding){
      CameraRequest(
        etRtspAddress.text.toString(),
        etLocation.text.toString(),
        etDesc.text.toString(),
        etArea.text.toString().toFloat(),
        etMaxCrowd.text.toString().toInt(),
        swActive.isChecked,
        swPublic.isChecked
      )
    }
  }

  private fun uploadImage(){
    camera?.id?.let { cameraId ->
      createFileBody()?.let {
        viewModel.uploadImage(cameraId, it)
      }
    } ?: run {
      if (viewModel.camera.value?.peekContent()?.second?.status == ResponseWrapper.Status.SUCCESS){
        viewModel.camera.value?.peekContent()?.second?.body?.data?.id?.let { cameraId ->
          createFileBody()?.let {
            viewModel.uploadImage(cameraId, it)
          }
        }
      }
    }
  }

  private fun submitData(){
    camera?.id?.let {
      if (validateEmptyField() && isThereUpdate()) {
        viewModel.updateCamera(it, generateCameraRequest())
      } else {
        Toast.makeText(requireContext(), getString(R.string.no_update), Toast.LENGTH_SHORT).show()
      }
    } ?: run {
      if (validateEmptyField()){
        viewModel.createCamera(generateCameraRequest())
      }
    }
  }

  private fun redirectToPreviousScreen(): Boolean{
    if (viewModel.camera.value?.peekContent()?.second?.status == ResponseWrapper.Status.SUCCESS){
      findNavController().navigate(R.id.to_camera_list)
      return true
    } else {
      findNavController().popBackStack()
    }
    return false
  }
}
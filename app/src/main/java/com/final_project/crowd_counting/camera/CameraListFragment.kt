package com.final_project.crowd_counting.camera

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DiffUtil
import com.final_project.crowd_counting.R
import com.final_project.crowd_counting.base.communicator.ActivityObserver
import com.final_project.crowd_counting.base.communicator.IToolbarCommunicator
import com.final_project.crowd_counting.base.model.Camera
import com.final_project.crowd_counting.base.source.network.ResponseWrapper
import com.final_project.crowd_counting.base.view.BaseFragment
import com.final_project.crowd_counting.base.view.rv.SpacingItemDecoration
import com.final_project.crowd_counting.base.view.rv.adapter.PagingLoadStateAdapter
import com.final_project.crowd_counting.camera.adapter.CameraListByOwnerAdapter
import com.final_project.crowd_counting.camera.model.CameraRequest
import com.final_project.crowd_counting.camera.viewmodel.CameraViewModel
import com.final_project.crowd_counting.camera.viewmodel.CameraViewModel.Companion.DELETE_KEY
import com.final_project.crowd_counting.camera.viewmodel.CameraViewModel.Companion.UPDATE_KEY
import com.final_project.crowd_counting.databinding.FragmentCameraListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CameraListFragment : BaseFragment<FragmentCameraListBinding, CameraViewModel>() {
  private val viewModel: CameraViewModel by viewModels()
  private val privateCameraAdapter: CameraListByOwnerAdapter = CameraListByOwnerAdapter(object: DiffUtil.ItemCallback<Camera>(){
    override fun areItemsTheSame(oldItem: Camera, newItem: Camera): Boolean {
      return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Camera, newItem: Camera): Boolean {
      return oldItem == newItem
    }

  }, ::onItemClicked, ::onItemDeleted, ::onItemStatusUpdated)


  override fun getVM(): CameraViewModel = viewModel

  override fun shouldHandleLoading(): Boolean = true

  override fun getFragmentBinding(
    inflater: LayoutInflater, container: ViewGroup?
  ): FragmentCameraListBinding {
    requireActivity().lifecycle.addObserver(ActivityObserver{
      (requireActivity() as IToolbarCommunicator).run {
        setOnNavigateBack(this@CameraListFragment, {
          findNavController().popBackStack()
        }, true)
        setTitleAndDescription(getString(R.string.camera_list), null)
      }
    })
    return FragmentCameraListBinding.inflate(inflater, container, false)
  }

  override fun shouldResetToolbarView(): Boolean = true

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initRecyclerView()
    viewBinding.ibAddToCart.setOnClickListener { toCameraFormFragment() }
    initObserver()
    if (viewModel.cameraList.value == null) viewModel.getCameraListByOwner()
  }

  private fun initRecyclerView(){
    val margin = (10 * resources.displayMetrics.density).toInt()
    privateCameraAdapter.addLoadStateListener { loadState ->
      if (loadState.refresh is LoadState.NotLoading){
        viewBinding.tvEmptyMessage.isVisible = privateCameraAdapter.itemCount <= 0
        viewBinding.tvEmptyMessage.text = getString(R.string.item_is_empty, getString(R.string.camera_list))
      }
    }
    with(viewBinding.rvCameraList){
      adapter = privateCameraAdapter.withLoadState(
        PagingLoadStateAdapter(privateCameraAdapter),
        PagingLoadStateAdapter(privateCameraAdapter)
      )
      addItemDecoration(SpacingItemDecoration(margin, margin, margin, margin))
    }
  }

  private fun initObserver(){
    viewModel.cameraList.observe(viewLifecycleOwner, {
      privateCameraAdapter.submitData(lifecycle, it)
    })
    viewModel.camera.observe(viewLifecycleOwner, { event ->
      event.getContentIfNotHandled()?.let {
        if (it.second.status == ResponseWrapper.Status.SUCCESS){
          when(it.first){
            UPDATE_KEY -> {
              Toast.makeText(requireContext(), getString(R.string.message_update_success, getString(R.string.camera)), Toast.LENGTH_SHORT).show()
              viewModel.getCameraListByOwner()
            }
            DELETE_KEY -> {
              Toast.makeText(requireContext(), getString(R.string.message_delete_success, getString(R.string.camera)), Toast.LENGTH_SHORT).show()
              viewModel.getCameraListByOwner()
            }
          }
        } else {
          Toast.makeText(requireContext(), it.second.body?.errors?.firstOrNull().toString(), Toast.LENGTH_SHORT).show()
        }
      }
    })
  }

  private fun onItemClicked(data: Camera){
    toCameraFormFragment(Bundle().apply { putParcelable(ARG_CAMERA, data) })
  }

  private fun onItemStatusUpdated(data: Camera, status: Boolean){
    data.run {
      if (rtspAddress != null && location != null) {
        data.id?.let { id ->
          viewModel.updateCamera(id, CameraRequest(rtspAddress,location,description,area,maxCrowdCount,status,isPublic))
        }
      }
    }
  }

  private fun onItemDeleted(data: Camera){
    dialogConfirmation(
      getString(R.string.are_you_sure_to_delete_item,
        getString(R.string.camera_id, data.id.toString()))
    ) {
      data.id?.let { id ->
        viewModel.deleteCamera(id)
      }
    }
  }

  private fun toCameraFormFragment(arg: Bundle? = null){
    findNavController().navigate(R.id.to_camera_form, arg)
  }
}
package com.final_project.crowd_counting.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DiffUtil
import com.final_project.crowd_counting.R
import com.final_project.crowd_counting.base.communicator.ActivityObserver
import com.final_project.crowd_counting.base.communicator.IToolbarCommunicator
import com.final_project.crowd_counting.base.model.Camera
import com.final_project.crowd_counting.base.view.BaseFragment
import com.final_project.crowd_counting.base.view.rv.SpacingItemDecoration
import com.final_project.crowd_counting.base.view.rv.adapter.PagingLoadStateAdapter
import com.final_project.crowd_counting.databinding.FragmentHomeBinding
import com.final_project.crowd_counting.home.adapter.CameraListAdapter
import com.final_project.crowd_counting.home.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {
  private val viewModel: HomeViewModel by viewModels()
  private val privateCameraAdapter: CameraListAdapter = CameraListAdapter(object: DiffUtil.ItemCallback<Camera>(){
    override fun areItemsTheSame(oldItem: Camera, newItem: Camera): Boolean {
      return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Camera, newItem: Camera): Boolean {
      return oldItem == newItem
    }

  }, true, ::onItemClicked)
  private val publicCameraAdapter: CameraListAdapter = CameraListAdapter(object: DiffUtil.ItemCallback<Camera>(){
    override fun areItemsTheSame(oldItem: Camera, newItem: Camera): Boolean {
      return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Camera, newItem: Camera): Boolean {
      return oldItem == newItem
    }

  }, false, ::onItemClicked)

  override fun getVM(): HomeViewModel = viewModel

  override fun shouldHandleLoading(): Boolean = false

  override fun getFragmentBinding(
    inflater: LayoutInflater, container: ViewGroup?
  ): FragmentHomeBinding {
    requireActivity().lifecycle.addObserver(ActivityObserver{
      (requireActivity() as IToolbarCommunicator).setToolbarVisibility(false)
    })
    return FragmentHomeBinding.inflate(inflater, container, false)
  }

  override fun shouldResetToolbarView(): Boolean = true

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initRecyclerView()
    initObserver()
  }

  private fun initRecyclerView(){
    val margin = (10 * resources.displayMetrics.density).toInt()
    privateCameraAdapter.addLoadStateListener { loadState ->
      if (loadState.refresh is LoadState.NotLoading){
        viewBinding.tvPrivateCamera.isVisible = privateCameraAdapter.itemCount > 0
        viewBinding.rvPrivateCamera.isVisible = privateCameraAdapter.itemCount > 0
      }
    }
    with(viewBinding.rvPrivateCamera){
      adapter = privateCameraAdapter.withLoadState(
        PagingLoadStateAdapter(privateCameraAdapter),
        PagingLoadStateAdapter(privateCameraAdapter)
      )
      addItemDecoration(SpacingItemDecoration(margin, margin, margin, margin))
    }

    publicCameraAdapter.addLoadStateListener { loadState ->
      if (loadState.refresh is LoadState.NotLoading){
        viewBinding.tvPublicCamera.isVisible = publicCameraAdapter.itemCount > 0
        viewBinding.rvPublicCamera.isVisible = publicCameraAdapter.itemCount > 0
      }
    }
    with(viewBinding.rvPublicCamera){
      adapter = publicCameraAdapter.withLoadState(
        PagingLoadStateAdapter(publicCameraAdapter),
        PagingLoadStateAdapter(publicCameraAdapter)
      )
      addItemDecoration(SpacingItemDecoration(margin, margin, margin, margin))
    }
  }

  private fun initObserver(){
    viewModel.privateCameraList.observe(viewLifecycleOwner, {
      privateCameraAdapter.submitData(lifecycle, it)
    })
    viewModel.publicCameraList.observe(viewLifecycleOwner, {
      publicCameraAdapter.submitData(lifecycle, it)
    })
  }

  private fun onItemClicked(data: Camera){
    findNavController().navigate(R.id.to_camera_detail, Bundle().apply {
      putParcelable(ARG_CAMERA, data)
    })
  }

}
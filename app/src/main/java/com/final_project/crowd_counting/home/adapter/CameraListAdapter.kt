package com.final_project.crowd_counting.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.final_project.crowd_counting.R
import com.final_project.crowd_counting.base.constant.Constant.CAMERA_IMAGE
import com.final_project.crowd_counting.base.model.Camera
import com.final_project.crowd_counting.base.utils.Util.loadImage
import com.final_project.crowd_counting.base.view.rv.adapter.BasePagingAdapter
import com.final_project.crowd_counting.databinding.ItemCameraBinding

class CameraListAdapter(
  comparator: DiffUtil.ItemCallback<Camera>,
  private val onItemClicked: (Camera) -> Unit
): BasePagingAdapter<Camera>(comparator) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasePagingViewHolder<Camera> {
    return CameraListViewHolder(
      ItemCameraBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )
  }

  private inner class CameraListViewHolder(private val viewBinding: ItemCameraBinding): BasePagingViewHolder<Camera>(viewBinding.root){
    override fun bind(data: Camera, position: Int?) {
      with(viewBinding){
        root.context.run {
          ivCamera.loadImage(CAMERA_IMAGE+data.id)
          tvLocation.text = getString(R.string.location_is, data.location)
          tvArea.text = getString(R.string.area_width_is, data.area.toString())
          tvMaxCrowdCount.text = getString(R.string.max_crowd_is, data.maxCrowdCount.toString())
          tvDesc.text = getString(R.string.description_is, data.description)
        }
        root.setOnClickListener { onItemClicked(data) }
      }
    }
  }
}
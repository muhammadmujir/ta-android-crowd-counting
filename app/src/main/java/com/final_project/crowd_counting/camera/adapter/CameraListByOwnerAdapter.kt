package com.final_project.crowd_counting.camera.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.final_project.crowd_counting.R
import com.final_project.crowd_counting.base.constant.Constant
import com.final_project.crowd_counting.base.model.Camera
import com.final_project.crowd_counting.base.utils.Util.loadImage
import com.final_project.crowd_counting.base.utils.Util.truncate
import com.final_project.crowd_counting.base.view.rv.adapter.BasePagingAdapter
import com.final_project.crowd_counting.databinding.ItemCameraByOwnerBinding

class CameraListByOwnerAdapter(
  comparator: DiffUtil.ItemCallback<Camera>,
  private val onItemClicked: (Camera) -> Unit,
  private val onItemDeleted: (Camera) -> Unit,
  private val onItemStatusUpdated: (Camera, active: Boolean) -> Unit
): BasePagingAdapter<Camera>(comparator) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasePagingViewHolder<Camera> {
    return CameraListViewHolder(
      ItemCameraByOwnerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )
  }

  private inner class CameraListViewHolder(private val viewBinding: ItemCameraByOwnerBinding): BasePagingViewHolder<Camera>(viewBinding.root){
    override fun bind(data: Camera, position: Int?) {
      with(viewBinding){
        ivCamera.loadImage(Constant.CAMERA_IMAGE +data.id, listOf(10F,10F,10F,10F))
        tvCameraTitle.truncate()
        tvCameraTitle.text = root.context.getString(R.string.camera_id, data.id.toString())
        tvCameraLocation.truncate(false, 3)
        tvCameraLocation.text = data.location
        ibDelete.setOnClickListener {
          onItemDeleted(data)
        }
        swActive.isChecked = data.isActive
        swActive.setOnCheckedChangeListener { _, isChecked ->
          if (data.isActive != isChecked)
            onItemStatusUpdated(data, isChecked)
        }
        root.setOnClickListener { onItemClicked(data) }
      }
    }
  }
}
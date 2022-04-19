package com.final_project.crowd_counting.base.injection

import androidx.recyclerview.widget.DiffUtil
import com.final_project.crowd_counting.base.model.Product
import javax.inject.Inject

class ProductObjComparatorImpl @Inject constructor(): DiffUtil.ItemCallback<Product>() {
  override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean = oldItem.id == newItem.id

  override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean = oldItem == newItem
}
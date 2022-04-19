package com.final_project.crowd_counting.base.view.rv.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.final_project.crowd_counting.base.constant.Constant
import com.final_project.crowd_counting.base.databinding.ItemMoreItemBinding
import com.final_project.crowd_counting.base.databinding.ItemTravellerHomeProductBinding
import com.final_project.crowd_counting.base.utils.Util.loadImage

class TravellerHomeProductAdapter(
  data: List<String>,
  private val toSellerHomepage: () -> Unit = {}
): FooterAdapter<String>() {

  init {
    setItemList(data)
  }

  override fun getItemViewType(position: Int): Int {
    return if (isFooterVisible && position == mutableItemCount-1) {
      VIEW_TYPE_FOOTER
    } else {
      VIEW_TYPE_CONTENT
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRvViewHolder<String> {
    return if (viewType == VIEW_TYPE_CONTENT){
      TravellerHomeProductViewHolder(
        ItemTravellerHomeProductBinding.inflate(
          LayoutInflater.from(parent.context),parent,false
        )
      )
    } else {
      MoreItemViewHolder(ItemMoreItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
  }

  private inner class TravellerHomeProductViewHolder(
    private val viewBinding: ItemTravellerHomeProductBinding
  ): BaseRvViewHolder<String>(viewBinding.root){
    override fun bind(data: String, position: Int?) {
      viewBinding.ivProduct.loadImage(Constant.PRODUCT_IMAGE+data, listOf(10.0F, 10.0F, 10.0F, 10.0F))
    }
  }

  private inner class MoreItemViewHolder(private val viewBinding: ItemMoreItemBinding): BaseRvViewHolder<String>(viewBinding.root){
    override fun bind(data: String, position: Int?) {
      viewBinding.ibMoreItem.setOnClickListener { toSellerHomepage() }
    }
  }
}
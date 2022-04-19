package com.final_project.crowd_counting.base.view.rv.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.final_project.crowd_counting.base.R
import com.final_project.crowd_counting.base.constant.Constant
import com.final_project.crowd_counting.base.databinding.ItemOrderedProductBinding
import com.final_project.crowd_counting.base.model.OrderedProduct
import com.final_project.crowd_counting.base.utils.Util.formatRp
import com.final_project.crowd_counting.base.utils.Util.loadImage
import com.final_project.crowd_counting.base.utils.Util.makeBold
import com.final_project.crowd_counting.base.utils.Util.setColor
import com.final_project.crowd_counting.base.utils.Util.truncate

class OrderedProductAdapter(productList: List<OrderedProduct>): BaseRvAdapter<OrderedProduct>() {

  init {
    setItemList(productList, true)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRvViewHolder<OrderedProduct> {
    return TravellerOrderedProductViewHolder(
      ItemOrderedProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )
  }

  private inner class TravellerOrderedProductViewHolder(private val viewBinding: ItemOrderedProductBinding): BaseRvViewHolder<OrderedProduct>(viewBinding.root){
    override fun bind(data: OrderedProduct, position: Int?) {
      with(viewBinding){
        val context = root.context
        ivProduct.loadImage(Constant.PRODUCT_IMAGE+data.product?.id)
        tvProductAndCount.run {
          truncate()
          val title = "${data.quantity}X ${data.product?.title}"
          text = makeBold(title, 0, "${data.quantity}X".length)
        }
        val priceRp = data.product?.price.formatRp()
        val pricePerCount = context.getString(R.string.price_per_product, priceRp)
        tvPricePerProduct.text = makeBold(pricePerCount, 0, priceRp.length).setColor(
          priceRp.length, pricePerCount.length, ContextCompat.getColor(context, R.color.colorBlackLight_ACA8A8)
        )
      }
    }
  }
}
package com.final_project.crowd_counting.base.constant

object Constant {
  const val SERVER_URL = "http://192.168.43.194:5000"
  const val BASE_URL = "$SERVER_URL/api/v1.0/"
  const val IMAGE_URL = "$SERVER_URL/static/images"
  const val PRODUCT_IMAGE = "$IMAGE_URL/products/"
  const val PRODUCT_REQUEST_IMAGE = "$IMAGE_URL/product-requests/"
  const val USER_IMAGE = "$IMAGE_URL/profile-pictures/"

  const val PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123
  const val PICK_PHOTO = 10

  const val GET_RESPONSE = "getResponse"
  const val EDIT_RESPONSE = "editResponse"
  const val DELETE_RESPONSE = "deleteResponse"

  const val CURRENT_VIEWPAGER_POSITION = "currentViewPagerPosition"
  const val ORDER_STATUS = "orderStatus"

  const val SELECTED_COUNTRY = "selectedCountry"
  const val IS_INIT_STATE = "isInitState"
  const val IS_STATE_CHANGED = "isStateChanged"
  const val PREVIOUS_SCREEN = "prevScreen"
  const val IMAGE_PATH = "imagePath"
}
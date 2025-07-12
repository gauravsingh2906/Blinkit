package com.android.example.blinkit.roomDb

import android.media.Image
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CartProducts")
data class CartProducts(

    @PrimaryKey
    val productId : String="random",// can't apply nullability check here
    var productTitle :String?=null,
    var productQuantity:String?=null,
    var productPrice:String?=null,
    var productCount:Int?=null,
    var productStock:Int?=null,
    var productImage: String?=null,
    var productCategory : String?= null,
    var adminUid : String?=null,
    var productType:String?=null


)
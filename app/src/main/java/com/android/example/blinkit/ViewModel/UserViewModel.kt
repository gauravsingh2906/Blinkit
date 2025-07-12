package com.android.example.blinkit.ViewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.example.blinkit.Models.BestSeller
import com.android.example.blinkit.Models.Orders
import com.android.example.blinkit.Models.Product
import com.android.example.blinkit.Utils.Object
import com.android.example.blinkit.Utils.Utils
import com.android.example.blinkit.api.ApiUtilities
import com.android.example.blinkit.roomDb.CartProductDao
import com.android.example.blinkit.roomDb.CartProductDatabase
import com.android.example.blinkit.roomDb.CartProducts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.Headers

class UserViewModel(application: Application) : AndroidViewModel(application) {

    //Initialization
    val sharedPreferences: SharedPreferences =
        application.getSharedPreferences("My_Pref", MODE_PRIVATE)
    val cartProductDao: CartProductDao =
        CartProductDatabase.getDatabaseInstance(application).cartProductDao()

    private val _paymentStatus = MutableStateFlow<Boolean>(false)
    val paymentStatus = _paymentStatus


    //Firebase Call
    fun fetchAllTheProducts(): Flow<List<Product>> =
        callbackFlow {   // here we get the callback from the flow
            val db = FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts")

            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val products = ArrayList<Product>()

                    for (product in snapshot.children) {
                        val prod = product.getValue(Product::class.java)
                        products.add(prod!!)
                    }
                    trySend(products)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            }
            db.addValueEventListener(eventListener)
            awaitClose { db.removeEventListener(eventListener) }


        }

    fun getAllOrders(): Flow<List<Orders>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("Orders")
            .orderByChild("orderDate")

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orderList = ArrayList<Orders>()
                for (orders in snapshot.children.reversed()) {
                    val order = orders.getValue(Orders::class.java)

                    if (order != null) {
                        if (order.orderingUserId == Utils.getCurrentUserId()) {
                            orderList.add(order)
                        }
                    }
                }
                trySend(orderList)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }

        db.addValueEventListener(eventListener)
        awaitClose { db.removeEventListener(eventListener) }
    }

    fun getOrderedProducts(orderId: String): Flow<List<CartProducts>> = callbackFlow {

        val db =
            FirebaseDatabase.getInstance().getReference("Admins").child("Orders").child(orderId)

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val order = snapshot.getValue(Orders::class.java)
                trySend(order?.orderList!!)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        db.addValueEventListener(eventListener)

        awaitClose {
            db.removeEventListener(eventListener)
        }

    }



fun getCategoryProduct(category: String?): Flow<List<Product>> = callbackFlow {
    val db = FirebaseDatabase.getInstance().getReference("Admins")
        .child("ProductCategory/${category}")

    val eventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val products = ArrayList<Product>()
            for (product in snapshot.children) {
                val prod = product.getValue(Product::class.java)
                products.add(prod!!)
            }
            trySend(products)
        }

        override fun onCancelled(error: DatabaseError) {

        }

    }

    db.addValueEventListener(eventListener)

    awaitClose {
        db.removeEventListener(eventListener)
    }

}


//Room Db

suspend fun deleteCartProducts() {
    cartProductDao.deleteCartProducts()
}

fun getAll(): LiveData<List<CartProducts>> {
    return cartProductDao.getAllCartProducts()
}

suspend fun insertCartProducts(products: CartProducts) {
    cartProductDao.insertCartProduct(products)
}

suspend fun updateCartProducts(products: CartProducts) {
    cartProductDao.updateCartProducts(products)
}

suspend fun deleteCartProduct(productId: String) {
    cartProductDao.deleteCartProduct(productId)
}

fun updateItemCount(product: Product, itemCount: Int) {
    FirebaseDatabase.getInstance().getReference("Admins")
        .child("AllProducts/${product.productRandomId}").child("itemCount").setValue(itemCount)
    FirebaseDatabase.getInstance().getReference("Admins")
        .child("ProductCategory/${product.productCategory}/${product.productRandomId}")
        .child("itemCount").setValue(itemCount)

    FirebaseDatabase.getInstance().getReference("Admins")
        .child("ProductType/${product.productType}/${product.productRandomId}")
        .child("itemCount").setValue(itemCount)

}


//SharedPreferences
fun savingCartItemCount(itemCount: Int) {
    sharedPreferences.edit().putInt("itemCount", itemCount).apply()
}

fun fetchTotalCartItemCount(): MutableLiveData<Int> {
    val totalItemCount = MutableLiveData<Int>()
    totalItemCount.value = sharedPreferences.getInt("itemCount", 0)
    return totalItemCount
}

fun saveAddressStatus() {
    sharedPreferences.edit().putBoolean("addressStatus", true).apply()
}

fun getAddressStatus(): MutableLiveData<Boolean> {
    val status = MutableLiveData<Boolean>()
    status.value = sharedPreferences.getBoolean("addressStatus", false)
    return status
}

fun saveUserAddress(address: String) {
    Utils.getCurrentUserId()
        ?.let {
            FirebaseDatabase.getInstance().getReference("AllUsers").child("Users").child(it)
                .child("address").setValue(address)
        }
}

fun saveOrderProducts(orders: Orders) {
    FirebaseDatabase.getInstance().getReference("Admins").child("Orders").child(orders.orderId!!)
        .setValue(orders)
}

fun getUserAddress(callback: (String?) -> Unit) {
    val db = FirebaseDatabase.getInstance().getReference("AllUsers").child("Users").child(Utils.getCurrentUserId()!!).child("address")

    db.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                val address = snapshot.getValue(String::class.java)
                callback(address)
            } else {
                callback(null)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            callback(null)

        }

    })
}

    fun getUserNumber(callback: (String?) -> Unit) {
        val db = FirebaseDatabase.getInstance().getReference("AllUsers").child("Users").child(Utils.getCurrentUserId()!!).child("userPhoneNumber")

        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userPhoneNumber = snapshot.getValue(String::class.java)
                    callback(userPhoneNumber)
                } else {
                    callback(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)

            }

        })

}

    fun saveAddress(address: String) {
         FirebaseDatabase.getInstance().getReference("AllUsers").child("Users").child(Utils.getCurrentUserId()!!).child("address").setValue(address)
    }

    fun saveNumber(number: String){
        FirebaseDatabase.getInstance().getReference("AllUsers").child("Users").child(Utils.getCurrentUserId()!!).child("userPhoneNumber").setValue(number)
    }

    fun logOutUser() {
        FirebaseAuth.getInstance().signOut()
    }

    fun fetchProductTypes():Flow<List<BestSeller>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("ProductType")

        val eventListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val productTypeList = ArrayList<BestSeller>()
                for (productType in snapshot.children) {
                    val productTypeName =productType.key  //key in the sense ice cream


                    val productList = ArrayList<Product>()

                    for (products in productType.children) {
                        val product = products.getValue(Product::class.java)
                        if (product != null) {
                            productList.add(product)
                        }

                    }


                    val bestSeller = BestSeller(productType = productTypeName, products = productList)
                    productTypeList.add(bestSeller)

                }
                trySend(productTypeList)

            }

            override fun onCancelled(error: DatabaseError) {

            }

        }

        db.addValueEventListener(eventListener)
        awaitClose { db.removeEventListener(eventListener) }
    }

fun saveProductsAfterOrders(stock: Int, product: CartProducts) {
    FirebaseDatabase.getInstance().getReference("Admins")
        .child("AllProducts/${product.productId}").child("itemCount").setValue(0)
    FirebaseDatabase.getInstance().getReference("Admins")
        .child("ProductCategory/${product.productCategory}/${product.productId}")
        .child("itemCount").setValue(0)



    Utils.showToast(getApplication(), "Order Successfully")

    FirebaseDatabase.getInstance().getReference("Admins")
        .child("AllProducts/${product.productId}").child("productStock").setValue(stock)
    FirebaseDatabase.getInstance().getReference("Admins")
        .child("ProductCategory/${product.productCategory}/${product.productId}")
        .child("productStock").setValue(stock)
}

// retrofit

@SuppressLint("SuspiciousIndentation")
suspend fun checkPayment(headers: Map<String, String>) {
    val res =
        ApiUtilities.statusApi.checkStatus(headers, Object.MERCHANTID, Object.merchantTransactionId)
    if (res.body() != null && res.body()!!.success) {
        _paymentStatus.value = true
    } else {
        _paymentStatus.value = false
    }
}

}
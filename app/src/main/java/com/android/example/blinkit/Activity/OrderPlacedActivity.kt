package com.android.example.blinkit.Activity

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.android.example.blinkit.Adapter.AdapterCartProducts
import com.android.example.blinkit.CartListener
import com.android.example.blinkit.Models.Orders
import com.android.example.blinkit.R
import com.android.example.blinkit.Utils.Object
import com.android.example.blinkit.Utils.Utils
import com.android.example.blinkit.ViewModel.UserViewModel
import com.android.example.blinkit.auth.SignInFragment
import com.android.example.blinkit.databinding.ActivityOrderPlacedBinding
import com.android.example.blinkit.databinding.AddressLayoutBinding
import com.google.firebase.auth.FirebaseAuth

import kotlinx.coroutines.launch
import org.json.JSONObject
import java.nio.charset.Charset
import java.security.MessageDigest


class OrderPlacedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderPlacedBinding
    private val viewModel: UserViewModel by viewModels()
    private var category: String? = null
    private var cartListener: CartListener? = null
    private lateinit var adapterCartProducts: AdapterCartProducts


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderPlacedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStatusBarColor()
        getAllCartProducts()
        onPlaceOrderClicked()

     //   initializePhonePay()

    }



//    private fun initializePhonePay() {
//        val data = JSONObject()
//         PhonePe.init(this, PhonePeEnvironment.SANDBOX, Object.MERCHANTID, "")
//
//        data.put("merchantId",Object.MERCHANTID)
//        data.put("merchantTransactionId",Object.merchantTransactionId)
//        data.put("amount",200)
//        data.put("mobileNumber","9899662963")
//        data.put("callbackUrl","https://webhook.site/callback-url")
//
//        val paymentInstrument = JSONObject()
//        paymentInstrument.put("type","UPI_INTENT")
//        paymentInstrument.put("targetApp","com.phonepe.simulator")
//        data.put("paymentInstrument",paymentInstrument)
//
//        val deviceContext = JSONObject()
//        deviceContext.put("deviceOS","ANDROID")
//        deviceContext.put("deviceContext",deviceContext)
//
//        val payloadBase64 = Base64.encodeToString(
//            data.toString().toByteArray(Charset.defaultCharset()),Base64.NO_WRAP
//        )
//
//        val checksum = sha256(payloadBase64+Object.apiEndPoint+Object.SALT_KEY) +"###1"
//        b2BGPRequest = B2BPGRequestBuilder()
//            .setData(payloadBase64)
//            .setChecksum(checksum)
//            .setUrl(Object.apiEndPoint)
//            .build()
//
//
//
//    }

    private fun sha256(input:String):String {
        val bytes = input.toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") {str,it -> str + "%02x".format(it)}
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onPlaceOrderClicked() {
        binding.btnNexts.setOnClickListener {
            viewModel.getAddressStatus().observe(this){ status ->
                if (status) {
                    //paymentWork
                 //   getPaymentView()

                } else {
                    val addressLayoutBinding =
                        AddressLayoutBinding.inflate(LayoutInflater.from(this))

                    val alertDialog = AlertDialog.Builder(this)
                        .setView(addressLayoutBinding.root)
                        .create()
                    alertDialog.show()

                    addressLayoutBinding.btnAdd.setOnClickListener {
                        saveAddress(alertDialog, addressLayoutBinding)
                    }
                //    getPaymentView()

                }

            }
        }
    }


    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkStatus() {

      val xVerify = sha256("/pg/v1/status/${Object.MERCHANTID}/${Object.merchantTransactionId}${Object.SALT_KEY}")+"###1"
      val headers = mapOf(
          "Content-Type" to "application/json",
          "X-VERIFY" to xVerify,
          "X-MERCHANT-ID" to Object.MERCHANTID
      )

        lifecycleScope.launch {
            viewModel.checkPayment(headers)
            viewModel.paymentStatus.collect{status->

                if(status) {

                    Utils.showToast(this@OrderPlacedActivity,"PaymentDone")
                    saveOrder()
                    lifecycleScope.launch {
                        viewModel.deleteCartProducts()
                    }
                    viewModel.savingCartItemCount(0)
                    cartListener?.hideCartLayout()
                    startActivity(Intent(this@OrderPlacedActivity, MainActivity2::class.java))
                    finish()
                } else{

                    saveOrder()
                    lifecycleScope.launch {
                        viewModel.deleteCartProducts()
                    }
                    viewModel.savingCartItemCount(0)
                    cartListener?.hideCartLayout()
                    startActivity(Intent(this@OrderPlacedActivity, MainActivity2::class.java))
                    finish()
                    Utils.showToast(this@OrderPlacedActivity,"PaymentFailed")
                }


        }
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    val phonePayView = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
     //   if(it.resultCode== RESULT_OK) {

            checkStatus()

     //   }
    }
    @RequiresApi(Build.VERSION_CODES.O)
//    private fun getPaymentView() {
//
//        try {
//            PhonePe.getImplicitIntent(this,b2BGPRequest,"com.phonepe.simulator")
//                .let {
//                    if (it != null) {
//                        phonePayView.launch(it)
//
//                    }
//                }
//        } catch (e:PhonePeInitException) {
//            Utils.showToast(this,e.message.toString())
//        }
//
//
//
//    }



    private fun saveOrder() {
        viewModel.getAll().observe(this) {

            if (it.isNotEmpty()) {
                viewModel.getUserAddress { address ->
                    val order = Orders(
                        orderId = Utils.getRandomId(),
                        orderList = it,
                        address = address,
                        orderStatus = 0,
                        orderDate = Utils.getCurrentDate(),
                        orderingUserId = Utils.getCurrentUserId()
                    )

                    viewModel.saveOrderProducts(order)
                }
                for (product in it) {
                    val count = product.productCount  // some error here
                    val stock = product.productStock?.minus(count!!)

                    Log.d("Count",count.toString())
                    Log.d("Stock",stock.toString())


                    Utils.showToast(this@OrderPlacedActivity,"Count:${count}")
                    Utils.showToast(this@OrderPlacedActivity,"Stock:${stock}")

                    if (stock != null) {
                        viewModel.saveProductsAfterOrders(stock, product)
//                        startActivity(Intent(this, MainActivity2::class.java))
//                        finish()
                    }
                }


            }


        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveAddress(alertDialog: AlertDialog, addressLayoutBinding: AddressLayoutBinding) {
        Utils.showDialog(this)
        val userPinCode = addressLayoutBinding.etPinCode.text.toString()
        val userPhoneNumber = addressLayoutBinding.etPhoneNumber.text.toString()
        val userState = addressLayoutBinding.etState.text.toString()
        val userDistrict = addressLayoutBinding.etDistrict.text.toString()
        val userAddress = addressLayoutBinding.etDescriptiveAddress.text.toString()

        val address = "$userPinCode,$userDistrict($userState),$userAddress,$userPhoneNumber"



        lifecycleScope.launch {
            viewModel.saveUserAddress(address)
            viewModel.saveAddressStatus()
        }
        Utils.showToast(this, "Saved..")
        alertDialog.dismiss()
        Utils.hideDialog()
       // getPaymentView()



    }

    private fun getAllCartProducts() {
        viewModel.getAll().observe(this) { cartProductsList ->
            adapterCartProducts = AdapterCartProducts()
            binding.rvProductItems.adapter = adapterCartProducts
            adapterCartProducts.differ.submitList(cartProductsList)

            var totalPrice = 0

            for (products in cartProductsList) {
                val price = products.productPrice?.toInt()
                val itemCount = products.productCount!!.toInt()

                totalPrice += (price?.times(itemCount)!!)

            }

            binding.tvSubTotal.text ="₹"+totalPrice.toString()


            if (totalPrice < 200) {
                binding.tvDeliveryCharge.text = "₹15"
                totalPrice += 15
            }
            binding.tvTotalAmount.text ="₹"+totalPrice.toString()
        }
    }

    private fun setStatusBarColor() {
        window?.apply {
            val statusBarColors = ContextCompat.getColor(this@OrderPlacedActivity, R.color.yellow)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
         finish()
    }
}
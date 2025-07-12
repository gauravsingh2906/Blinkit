package com.android.example.blinkit.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.android.example.blinkit.Activity.MainActivity2
import com.android.example.blinkit.R
import com.android.example.blinkit.Utils.Utils
import com.android.example.blinkit.databinding.FragmentSignInBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit


class SignInFragment : Fragment() {

    private lateinit var binding:FragmentSignInBinding
    lateinit var auth:FirebaseAuth
    var number:String?=""
    lateinit var resendToken:PhoneAuthProvider.ForceResendingToken
     lateinit var storedVerificationId:String
    //  private lateinit var callbacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentSignInBinding.inflate(layoutInflater)

        auth= FirebaseAuth.getInstance()

        val currentUser =auth.currentUser
        if(currentUser!=null) {
            startActivity(Intent(requireContext(), MainActivity2::class.java))
            requireActivity().finishAffinity()
        }






        activity?.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )

         number =binding.etUserNumb.text.toString()

        getUserNumber()
        onContinueButtonClick()


        return binding.root
    }

    private fun onContinueButtonClick() {
        binding.button.setOnClickListener {
            number =binding.etUserNumb.text.toString()

            if(number!!.isEmpty()  ||number!!.length!=10) {
                Utils.showToast(requireContext(), "Please Enter a Valid Number")
            } else{
               
             //   sendVerificationCode(number!!)
                val bundle = Bundle()
                bundle.putString("number",number)
                findNavController().navigate(R.id.action_signInFragment_to_otpFragment,bundle)


                // startActivity(intent)

            }
        }
    }

    private fun sendVerificationCode(number: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+91$number")
            .setTimeout(30L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
           // startActivity(Intent(requireActivity(), MainActivity::class.java))
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.

        }


        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {

            storedVerificationId = verificationId
            resendToken = token



//            val bundle = Bundle()
//            bundle.putString("number",number)
//            bundle.putString("verificationId",storedVerificationId)
//            findNavController().navigate(R.id.action_signInFragment_to_otpFragment,bundle)




        }

    }

    private fun getUserNumber() {

        binding.etUserNumb.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }


            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val num = s?.length

                if(num==10) {
                    binding.button.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.green))
                } else{
                    binding.button.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.greyish_blue))
                }

            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

    }


    }





//    private fun getUserNumber() {
//
////
////binding.etUserNumb.addTextChangedListener( object :TextWatcher{
////
////    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
////
////    }
////
////    @SuppressLint("SuspiciousIndentation")
////    override fun onTextChanged(num: CharSequence?, start: Int, before: Int, count: Int) {
////        val leng = num?.length
////
////                 if (leng== 10) {
//////                     binding.button.requestFocus()
//////                     binding.button.setBackgroundColor(ContextCompat.getColor(
//////                         requireContext(),
//////                         R.color.green
//////                     ))
////                 } else {
//////                     binding.button.requestFocus()
//////                     binding.button.setBackgroundColor(ContextCompat.getColor(
//////                         requireContext(),
//////                         R.color.greyish_blue
//////                     ))
////                 }
////             }
////
////
////    override fun afterTextChanged(s: Editable?) {
////
////    }
////
////    })
//
//    }


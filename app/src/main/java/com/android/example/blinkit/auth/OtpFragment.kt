package com.android.example.blinkit.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.android.example.blinkit.Activity.MainActivity2
import com.android.example.blinkit.Models.Users
import com.android.example.blinkit.R
import com.android.example.blinkit.Utils.Utils
import com.android.example.blinkit.databinding.FragmentOtpBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.TimeUnit


class OtpFragment : Fragment() {

    private lateinit var binding:FragmentOtpBinding
    var userNumber:String?=""
    var otpId:String?=""
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    lateinit var resendToken:PhoneAuthProvider.ForceResendingToken
    lateinit var storedVerificationId:String


    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentOtpBinding.inflate(layoutInflater)

        auth=FirebaseAuth.getInstance()
        database= FirebaseDatabase.getInstance()

        val bundle = arguments
        val storeVerificationId = bundle?.getString("verificationId")
        val number = bundle?.getString("number")





        getUserNumber()
        onBackButtonClicked()
        sendVerificationCode(number!!)


        binding.button2.setOnClickListener {
            val otp = binding.firstPinView.text.toString()

                if(otp.isNotEmpty()) {

                    val credential: PhoneAuthCredential? = PhoneAuthProvider.getCredential(otpId!!,otp)
                    if (credential != null) {
                        this.signInWithPhoneAuthCredentialO(credential)
                    }
                    startActivity(Intent(requireContext(), MainActivity2::class.java))
                    requireActivity().finish()

//                    requireActivity().finish()

                } else{
                    Toast.makeText(context, "Enter the otp", Toast.LENGTH_SHORT).show()
                }

          //  startActivity(Intent(requireContext(), MainActivity2::class.java))
        }






    return binding.root
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

        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.

        }


        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {

            otpId = verificationId
           resendToken = token



//            val bundle = Bundle()
//            bundle.putString("number",number)
//            bundle.putString("verificationId",storedVerificationId)
//            findNavController().navigate(R.id.action_signInFragment_to_otpFragment,bundle)




        }

    }



    private fun onBackButtonClicked() {
        binding.materialToolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_otpFragment_to_signInFragment)
        }
    }

    private fun getUserNumber() {
        val bundle = arguments
        userNumber = bundle?.getString("number")
        binding.tvUserNumber.text=userNumber
    }

    private fun signInWithPhoneAuthCredentialO(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {

                    val user = Users(uId = Utils.getCurrentUserId(), userPhoneNumber = userNumber, address = " ")
                    database.getReference("AllUsers").child("Users").child(user.uId!!).setValue(user)
                        .addOnSuccessListener {
                            Utils.showToast(requireContext(),"we get here")

                        } .addOnFailureListener { exception ->
                            Utils.showToast(requireContext(),"FAILED Successfully")
                            // Handle saving failure
                            Log.w("Fragment", "Failed to save user data: $exception")
                        }






                } else {
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    Toast.makeText(requireContext(), "asfjhb", Toast.LENGTH_SHORT).show()
                }
            }


    }

}
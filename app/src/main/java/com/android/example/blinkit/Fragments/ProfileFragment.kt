package com.android.example.blinkit.Fragments

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.android.example.blinkit.Activity.MainActivity

import com.android.example.blinkit.R
import com.android.example.blinkit.Utils.Utils
import com.android.example.blinkit.ViewModel.UserViewModel
import com.android.example.blinkit.auth.SignInFragment
import com.android.example.blinkit.databinding.AddressBookLayoutBinding
import com.android.example.blinkit.databinding.FragmentProfileBinding


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel:UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentProfileBinding.inflate(layoutInflater)

        onOrdersLayoutClicked()
        onAddressLayoutClicked()
        onLogOutClicked()
        setNumber()





        return binding.root
    }

    private fun setNumber() {
   viewModel.getUserNumber {
       binding.number.text=it.toString()
   }
    }

    private fun onLogOutClicked() {
        binding.llLogOut.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            val alertDialog:AlertDialog = builder.create()
            builder.setTitle("Log out")
            .setMessage("Do you want to log out ?")
            .setPositiveButton("Yes") { dialog, which ->
                viewModel.logOutUser()
                startActivity(Intent(requireContext(),MainActivity::class.java))
                requireActivity().finish()
            }.setNegativeButton("No", DialogInterface.OnClickListener { _,_ ->
               alertDialog.dismiss()

            }).show()
        }
    }

    private fun onAddressLayoutClicked() {
        binding.llAddress.setOnClickListener {
            val addressBookLayoutBinding:AddressBookLayoutBinding = AddressBookLayoutBinding.inflate(LayoutInflater.from(requireContext()))
           Utils.showDialog(requireContext())
            viewModel.getUserAddress {
                addressBookLayoutBinding.etAddress.setText(it.toString())
                Utils.hideDialog()
            }

            val alertDialog = AlertDialog.Builder(requireContext())
                .setView(addressBookLayoutBinding.root)
                .create()
            alertDialog.show()

            addressBookLayoutBinding.btnEdit.setOnClickListener {
                addressBookLayoutBinding.etAddress.isEnabled=true
            }
            addressBookLayoutBinding.btnSave.setOnClickListener {
                viewModel.saveAddress(addressBookLayoutBinding.etAddress.text.toString())
                alertDialog.dismiss()
                Utils.showToast(requireContext(),"Address Updated")
            }
        }
    }

    private fun logOut() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete Account")
        builder.setCancelable(false)
        builder.setMessage("Are you Sure You Want to Delete the Account")
        builder.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
//            findNavController().navigate(R.id.action_profileFragment_to_signInFragment)
        }).show()
        builder.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()

        }).show()
    }

    private fun onOrdersLayoutClicked() {
        binding.llOrders.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_ordersFragment)
        }
    }


}
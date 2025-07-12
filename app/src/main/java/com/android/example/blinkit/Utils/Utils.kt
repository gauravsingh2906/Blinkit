package com.android.example.blinkit.Utils


import android.content.Context
import android.icu.util.LocaleData
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import com.android.example.blinkit.R
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Utils {

    private var dialog: AlertDialog?=null

    fun showDialog(context: Context) {
        dialog= AlertDialog.Builder(context).setView(R.layout.dialog).setCancelable(false).create()
        dialog!!.show()
    }

    fun hideDialog() {
        dialog?.dismiss()
    }



    fun showToast(context: Context,message:String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private var firebaseAuthInstance:FirebaseAuth?=null
    fun getAuthInstance():FirebaseAuth {
        if(firebaseAuthInstance!=null) {
            firebaseAuthInstance=FirebaseAuth.getInstance()
        }
        return firebaseAuthInstance!!
    }

    fun getCurrentUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid.toString()
    }

    fun getRandomId(): String? {
        return (1..20).map{(('A'..'Z')+('a'..'z')+('0'..'9')).random()}.joinToString(separator = "")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDate():String? {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        return currentDate.format(formatter)
    }




}
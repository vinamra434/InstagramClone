package com.mindorks.bootcamp.instagram.utils.common

import android.app.AlertDialog
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.ui.base.BaseActivity
import kotlinx.android.synthetic.main.custom_loading.*

class DialogLoading(private val activity: BaseActivity<*>) {

    private lateinit var dialog: AlertDialog

    fun startLoading(){
        val builder = AlertDialog.Builder(activity)
        val layoutInflater = activity.layoutInflater

        builder.setView(layoutInflater.inflate(R.layout.custom_loading, null))

        builder.setCancelable(false)

        dialog = builder.create()
        dialog.show()
    }

    fun dismissLoading() {
        dialog.dismiss()
    }

}
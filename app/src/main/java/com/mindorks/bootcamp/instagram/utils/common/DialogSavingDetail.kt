package com.mindorks.bootcamp.instagram.utils.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.mindorks.bootcamp.instagram.R
import kotlinx.android.synthetic.main.custom_loading.*
import kotlinx.android.synthetic.main.dialog_discard_change.*

class DialogSavingDetail : DialogFragment() {

    companion object {

        const val TAG = "DialogSavingDetail"
        private const val KEY_MESSAGE = "message"

        fun newInstance(message: String): DialogSavingDetail =
            DialogSavingDetail().apply {
                arguments = Bundle().apply {
                    putString(KEY_MESSAGE, message)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isCancelable = false
        return inflater.inflate(R.layout.custom_loading, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loading_text.text = arguments?.getString(KEY_MESSAGE)
    }

}
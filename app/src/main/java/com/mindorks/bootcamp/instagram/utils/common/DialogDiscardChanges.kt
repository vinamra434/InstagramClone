package com.mindorks.bootcamp.instagram.utils.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.mindorks.bootcamp.instagram.R
import kotlinx.android.synthetic.main.dialog_discard_change.*

class DialogDiscardChanges: DialogFragment() {

    companion object {

        const val TAG = "DiscardImageDialog"

        fun newInstance(): DialogDiscardChanges {
            val args = Bundle()
            val fragment = DialogDiscardChanges()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_discard_change, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        yes.setOnClickListener {

            val dialogListener = activity as DialogListener
            dialogListener.onOptionSelected(Choice.YES)
            dismiss()
        }
        no.setOnClickListener {
            val dialogListener = activity as DialogListener
            dialogListener.onOptionSelected(Choice.No)
            dismiss()
        }
    }

    interface DialogListener {
        fun onOptionSelected(option: Choice)
    }

    enum class Choice {
        YES,
        No
    }

}
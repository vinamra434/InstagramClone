package com.mindorks.bootcamp.instagram.utils.common

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.mindorks.bootcamp.instagram.R
import kotlinx.android.synthetic.main.dialog_image_selection.*

class DialogImageSelection: DialogFragment() {

    companion object {

        const val TAG = "ImageSelection"

        fun newInstance(): DialogImageSelection {
            val args = Bundle()
            val fragment = DialogImageSelection()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var listener: DialogListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_image_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fromCamera.setOnClickListener {
            listener.onOptionSelected(DialogListener.Selection.CAMERA)
            dismiss()
        }
        fromGallery.setOnClickListener {
            listener.onOptionSelected(DialogListener.Selection.GALLERY)
            dismiss()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as DialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                context.toString() +
                        "must implement DialogListener"
            )
        }
    }

}
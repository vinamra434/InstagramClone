package com.mindorks.bootcamp.instagram.utils.common

interface DialogListener {
    fun onOptionSelected(option: Selection)
    enum class Selection {
        CAMERA,
        GALLERY
    }
}
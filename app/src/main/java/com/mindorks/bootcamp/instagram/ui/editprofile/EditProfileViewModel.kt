package com.mindorks.bootcamp.instagram.ui.editprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.data.model.Image
import com.mindorks.bootcamp.instagram.data.model.User
import com.mindorks.bootcamp.instagram.data.remote.Networking
import com.mindorks.bootcamp.instagram.data.remote.request.UpdateProfileRequest
import com.mindorks.bootcamp.instagram.data.repository.PhotoRepository
import com.mindorks.bootcamp.instagram.data.repository.UserRepository
import com.mindorks.bootcamp.instagram.ui.base.BaseViewModel
import com.mindorks.bootcamp.instagram.utils.common.*
import com.mindorks.bootcamp.instagram.utils.log.Logger
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelper
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import java.io.File
import java.io.InputStream

class EditProfileViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    private val userRepository: UserRepository,
    private val photoRepository: PhotoRepository,
    private val directory: File
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    companion object {
        const val TAG = "EditProfileViewModel"
    }

    private val validationsList: MutableLiveData<List<Validation>> = MutableLiveData()

    private var user = userRepository.getCurrentUser()!!

    private val headers = mapOf(
        Pair(Networking.HEADER_API_KEY, Networking.API_KEY),
        Pair(Networking.HEADER_USER_ID, user.id),
        Pair(Networking.HEADER_ACCESS_TOKEN, user.accessToken)
    )

    private var uploadUrl: String? = null

    val nameField: MutableLiveData<String> = MutableLiveData()
    val bioField: MutableLiveData<String> = MutableLiveData()
    val email: MutableLiveData<String> = MutableLiveData()
    val profileUrl: MutableLiveData<Image> = MutableLiveData()

    val nameFieldValidation: LiveData<Resource<Int>> = filterValidation(Validation.Field.FULLNAME)
    val loading: MutableLiveData<Boolean> = MutableLiveData() //normal loading
    val imageUpdateLoading: MutableLiveData<Boolean> = MutableLiveData() // message loading
    val profileUpdateLoading: MutableLiveData<Boolean> = MutableLiveData() // message loading

    val openImageSelectionDialog = MutableLiveData<Event<Boolean>>()
    val showDiscardDialog =
        MutableLiveData<Event<Boolean>>() // if changes are not saved then show dialog
    val updatedUser = MutableLiveData<Event<User>>()

    private fun filterValidation(field: Validation.Field) =
        Transformations.map(validationsList) {
            it.find { validation -> validation.field == field }
                ?.run { return@run this.resource }
                ?: Resource.unknown()
        }

    init {
        nameField.postValue(user.name)
        bioField.postValue(user.tagline)
        email.postValue(user.email)
        profileUrl.postValue(user.profilePicUrl?.let { Image(it, headers) })
    }

    override fun onCreate() {}

    fun onNameChanged(name: String) = nameField.postValue(name)

    fun onBioChanged(bio: String) = bioField.postValue(bio)

    fun onSave() {
        if (isEdited()) {
            val name = nameField.value?.trim()
            val bio = bioField.value?.trim()

            val validations = Validator.validateProfileFields(name)
            validationsList.postValue(validations)

            //first upload image then upload details

            if (validations.isNotEmpty() && name != null) {
                val successValidation = validations.filter { it.resource.status == Status.SUCCESS }
                if (successValidation.size == validations.size && checkInternetConnectionWithMessage()) {
                    profileUpdateLoading.postValue(true)
                    compositeDisposable.addAll(
                        userRepository.updateUserProfile(
                            user,
                            UpdateProfileRequest(name, uploadUrl, bio) //update user object
                        )
                            .subscribeOn(schedulerProvider.io())
                            .subscribe(
                                {
                                    user = User(
                                        user.id,
                                        name,
                                        user.email,
                                        user.accessToken,
                                        uploadUrl,
                                        bio
                                    )
                                    userRepository.saveCurrentUser(user)
                                    profileUpdateLoading.postValue(false)
                                    updatedUser.postValue(Event(user))
                                },
                                {
                                    handleNetworkError(it)
                                    profileUpdateLoading.postValue(false)
                                }
                            )
                    )
                }
            }
        }
    }

    fun onCancel() {

        if (isEdited()) {
            showDiscardDialog.postValue(Event(true))
        } else {
            //nothing changed
            showDiscardDialog.postValue(Event(false))
        }
    }

    private fun isEdited() =
        uploadUrl != null || user.name != nameField.value?.trim() || (bioField.value?.trim() != user.tagline ?: "")

    fun onChangeImage() {
        openImageSelectionDialog.postValue(Event(true))
    }

    fun onGalleryImageSelected(inputStream: InputStream) {
        loading.postValue(true)
        compositeDisposable.add(
            Single.fromCallable {
                FileUtils.saveInputStreamToFile(
                    inputStream, directory, "gallery_img_temp", 500
                )
            }
                .subscribeOn(schedulerProvider.io())
                .subscribe(
                    {
                        if (it != null) {
                            uploadPhoto(it)
                        } else {
                            messageStringId.postValue(Resource.error(R.string.try_again))
                        }
                        loading.postValue(false)
                    },
                    {
                        loading.postValue(false)
                        messageStringId.postValue(Resource.error(R.string.try_again))
                    }
                )
        )
    }

    fun onCameraImageTaken(cameraImageProcessor: () -> String) {
        loading.postValue(true)
        compositeDisposable.add(
            Single.fromCallable { cameraImageProcessor() }
                .subscribeOn(schedulerProvider.io())
                .subscribe(
                    {
                        File(it).apply {
                            uploadPhoto(this)
                        }
                        loading.postValue(false)
                    },
                    {
                        loading.postValue(false)
                        messageStringId.postValue(Resource.error(R.string.try_again))
                    }
                )
        )
    }

    private fun uploadPhoto(imageFile: File) {
        Logger.d("DEBUG", imageFile.path)
        imageUpdateLoading.postValue(true)
        compositeDisposable.add(
            photoRepository.uploadPhoto(imageFile, user)
                .subscribeOn(schedulerProvider.io())
                .subscribe(
                    {
                        //display image and save to preferences
                        Logger.d("DEBUG", it)
                        profileUrl.postValue(Image(it, headers))

                        uploadUrl = it
                        imageUpdateLoading.postValue(false)

                    },
                    {
                        handleNetworkError(it)
                        imageUpdateLoading.postValue(false)
                    }
                )

        )
    }

    override fun onCleared() {
        super.onCleared()
        Logger.d(TAG, "onCleared")
    }
}
package com.mindorks.bootcamp.instagram.di

import javax.inject.Qualifier

/*to differentiate between methods with same return type*/

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationContext

@Qualifier
@Retention(AnnotationRetention.SOURCE)
annotation class ActivityContext

@Qualifier
@Retention(AnnotationRetention.SOURCE)
annotation class TempDirectory

@Qualifier
@Retention(AnnotationRetention.SOURCE)
annotation class ImageDialog

@Qualifier
@Retention(AnnotationRetention.SOURCE)
annotation class ProfileDialog
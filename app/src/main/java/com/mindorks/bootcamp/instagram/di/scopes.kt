package com.mindorks.bootcamp.instagram.di

import javax.inject.Scope

/*annotation to acts as a bag*/
@Scope
@Retention(AnnotationRetention.SOURCE)
annotation class ActivityScope

@Scope
@Retention(AnnotationRetention.SOURCE)
annotation class FragmentScope

@Scope
@Retention(AnnotationRetention.SOURCE)
annotation class ViewModelScope

@Scope
@Retention(AnnotationRetention.SOURCE)
annotation class DialogFragmentScope
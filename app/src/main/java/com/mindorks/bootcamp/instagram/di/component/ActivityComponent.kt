package com.mindorks.bootcamp.instagram.di.component

import com.mindorks.bootcamp.instagram.di.ActivityScope
import com.mindorks.bootcamp.instagram.di.module.ActivityModule
import com.mindorks.bootcamp.instagram.ui.editprofile.EditProfileActivity
import com.mindorks.bootcamp.instagram.ui.login.LoginActivity
import com.mindorks.bootcamp.instagram.ui.main.MainActivity
import com.mindorks.bootcamp.instagram.ui.signup.SignUpActivity
import com.mindorks.bootcamp.instagram.ui.splash.SplashActivity
import dagger.Component

/*every component needs a scope*/
/*intermediate between activity/fragment view-model and its corresponding module*/
@ActivityScope
@Component(
    dependencies = [ApplicationComponent::class],
    modules = [ActivityModule::class]
)
interface ActivityComponent {

    fun inject(activity: SplashActivity)

    fun inject(activity: LoginActivity)

    fun inject(activity: SignUpActivity)

    fun inject(activity: MainActivity)

    fun inject(activity: EditProfileActivity)

}
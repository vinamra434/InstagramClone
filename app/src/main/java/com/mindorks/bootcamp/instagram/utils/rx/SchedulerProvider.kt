package com.mindorks.bootcamp.instagram.utils.rx

import io.reactivex.Scheduler
import javax.inject.Singleton

interface SchedulerProvider {

    fun computation(): Scheduler

    fun io(): Scheduler

    fun ui(): Scheduler
}
package com.hello.mihe.app.launcher.event


interface BaseEvent {
    fun eventName(): String
    fun eventValue(): Any
}


class HideAppEvent(
    private val eventValue: Boolean = false,
) : BaseEvent {
    override fun eventName() = "event_profile_owner_app_change"
    override fun eventValue() = eventValue
}
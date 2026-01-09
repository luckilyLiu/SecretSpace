package com.hello.mihe.app.launcher.config

import com.hello.mihe.app.launcher.MiheApp
import com.hello.mihe.app.launcher.api.models.IpRecordResponse
import com.hello.mihe.app.launcher.utils.SharedPrefUtils
import java.util.UUID

object UserManager {
    var userId: Long = SharedPrefUtils.getLongData(MiheApp.app, "user_manager_user_id")
    var guestId: String = SharedPrefUtils.getStringData(MiheApp.app, "user_manager_guest_id")
    var ip: String = SharedPrefUtils.getStringData(MiheApp.app, "user_manager_ip")
    var countryEn: String = SharedPrefUtils.getStringData(MiheApp.app, "user_manager_country_en")
    var countryZh: String = SharedPrefUtils.getStringData(MiheApp.app, "user_manager_country_zh")
    var city: String = SharedPrefUtils.getStringData(MiheApp.app, "user_manager_city")

    fun login(userId: Long) {
        this.userId = userId;
        SharedPrefUtils.saveData(MiheApp.app, "user_manager_user_id", userId)
        this.guestId = UUID.randomUUID().toString()
        SharedPrefUtils.saveData(MiheApp.app, "user_manager_guest_id", this.guestId)
    }

    fun saveIpInfo(ipRecordResponse: IpRecordResponse) {
        this.ip = ipRecordResponse.ip ?: ""
        this.city = ipRecordResponse.city ?: ""
        this.countryEn = ipRecordResponse.country_en ?: ""
        this.countryZh = ipRecordResponse.country_zh ?: ""
    }
}
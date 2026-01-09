package com.hello.mihe.app.launcher.constant

import com.hello.mihe.app.launcher.utils.EnvUtil
import com.hello.mihe.app.launcher.utils.LanguageUtil


private val intl_host =
    if (EnvUtil.isStaging) {
        "https://m.staging2.p1staff.com"
    } else {
        "https://m.tantanapp.com/"
    }

private val BASE_HOST_URL_GOOGLE =
    "$intl_host/fep/tantan/frontend/tantan-frontend-app-pages-v2/src/pages/toolProductionInternationality/index.html"


fun URL_APP_PRIVACY() =
    if (LanguageUtil.languageIsEn()) {
        "${BASE_HOST_URL_GOOGLE}?speed=true&_bid=1004637#/privacy"
    } else {
        "${BASE_HOST_URL_GOOGLE}?speed=true&_bid=1004637#/privacy"

    }

fun URL_APP_AGREEMENT() =
    if (LanguageUtil.languageIsEn()) {
        "${BASE_HOST_URL_GOOGLE}?speed=true&_bid=1004637#/protocol"
    } else {
        "${BASE_HOST_URL_GOOGLE}?speed=true&_bid=1004637#/protocol"
    }

val URL_APP_AGREEMENT = "https://m.tantanapp.com/fep/tantan/frontend/tantan-frontend-app-pages-v2/src/pages/toolProductionInternationality/index.html?speed=true&_bid=1004637#/hideAllProtocol"

val URL_APP_PRIVACY =
    "https://m.tantanapp.com/fep/tantan/frontend/tantan-frontend-app-pages-v2/src/pages/toolProductionInternationality/index.html?speed=true&_bid=1004637#/hideAllPrivacy"

// 埋点相关
private val HOST_NAME =
    if (EnvUtil.isStaging) "melon-gateway-stage.immomo.com"
    else {
        "melon-gateway-useast.immomo.com"
    }

const val SCHEME_NAME = "vboxappoversea"
val SAVE_URL = "$HOST_NAME/datawarehouse/point/report/noencrypt/save"
val SAVE_SCHEMA_URL =
    "$HOST_NAME/datawarehouse/point/report/saveSchemaBatch/noencrypt/datainf_hellodata_client_point"
val ELEMENT_URL = "$HOST_NAME/datawarehouse/point/report/noencrypt/getElements"
val REGISTRATION_URL =
    "$HOST_NAME/datawarehouse/points/validation/register/noencrypt/registerDeviceId"





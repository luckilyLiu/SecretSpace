package com.hello.mihe.app.launcher.autotracker

import com.hello.mihe.app.launcher.MiheApp
import com.hello.mihe.app.launcher.constant.SCHEME_NAME
import com.immomo.autotracker.android.sdk.Core
import com.immomo.autotracker.android.sdk.MATConfigOptions
import com.immomo.autotracker.android.sdk.MATEventType
import com.immomo.autotracker.android.sdk.MomoAutoTrackerAPI
import org.json.JSONObject

class SensorsAnalyticsSdkHelper private constructor() {

  private var inited: Boolean = false

  companion object {
    fun getInstance(): SensorsAnalyticsSdkHelper {
      return SensorsAnalyticsSdkHolder.INSTANCE
    }
  }

  fun init() {
    if (inited) {
      return
    }
    Core.getInstance().schemeName = SCHEME_NAME
    val configOptions = MATConfigOptions("")
    configOptions
      .setAutoTrackEventType(
        MATEventType.APP_VIEW_SCREEN or
          MATEventType.APP_CLICK or
          MATEventType.APP_START or
          MATEventType.APP_DISAPPEAR_SCREED
      )
      .enableJavaScriptBridge(true)
      .enableVisualizedAutoTrack(true)

    MomoAutoTrackerAPI.registerRequestImpl(SandBoxMATRequestBridger())
    MomoAutoTrackerAPI.registerParamsBridgerImpl(SandBoxMATParamBridger())
    MomoAutoTrackerAPI.init(MiheApp.app)
    MomoAutoTrackerAPI.startWithConfigOptions(MiheApp.app, configOptions)
    MomoAutoTrackerAPI.sharedInstance(MiheApp.app).trackFragmentAppViewScreen()
    inited = true
  }

  fun trackPV(eventName: String) {
    trackPV(eventName, JSONObject())
  }

  fun trackPV(eventName: String, jsonObject: JSONObject) {
    MomoAutoTrackerAPI.sharedInstance().trackAppEventPointWithEventType(eventName, "PV", jsonObject)
  }

  fun trackMV(eventName: String) {
    trackMV(eventName, JSONObject())
  }

  fun trackMV(eventName: String, jsonObject: JSONObject) {
    MomoAutoTrackerAPI.sharedInstance().trackAppEventPointWithEventType(eventName, "MV", jsonObject)
  }

  fun trackMC(eventName: String) {
    trackMC(eventName, JSONObject())
  }

  fun trackMC(eventName: String, jsonObject: JSONObject) {
    MomoAutoTrackerAPI.sharedInstance().trackAppEventPointWithEventType(eventName, "MC", jsonObject)
  }

  private object SensorsAnalyticsSdkHolder {
    val INSTANCE = SensorsAnalyticsSdkHelper()
  }
}

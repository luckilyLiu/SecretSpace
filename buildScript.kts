import com.tantanapp.library.script.BuildConfig
import com.tantanapp.library.script.BuildExecutor
import com.tantanapp.library.script.Param

class BuildExecutorImpl() : BuildExecutor() {
  // 继承BuildExecutor，简单复写buildConfig方法即可，更多可复写的方法请查看源码
  // https://gitlab.p1staff.com/android/backend/tantan-build/blob/master/libBuildScript/src/main/java/com/tantanapp/library/script/BuildExecutor.kt

  override fun buildConfig(param: Param): BuildConfig {
    // 从param中可以读取到jenkins打包时传入的参数，打包时根据参数打出需要的包
    if (param.debug) {
      // command是真实的打包命令数组，会在shell中执行的，第一个是命令，后面的是参数
      // 如果简单的命令可以执行写./gradlew …… ，需要复杂操作的可以自己写成sh脚本，如下例中所示
      val command = arrayOf<String>("./release_apk_debug.sh","debug")
      // path是执行完打包命令后用于上传的打包产物，array中的文件夹的内容都会被上传，请勿上传无用的产物，会导致磁盘空间浪费
      val path = arrayOf<String>("mihe_apks/debug")
      return BuildConfig(command, path)
    } else {
      val command = arrayOf<String>("./release_apk_release.sh","release")
      val path = arrayOf<String>("mihe_apks/release")
      return BuildConfig(command, path)
    }
  }
}

// ===================================== real function ，do not modify ===========================================
BuildExecutorImpl().start(args)

System.exit(
  0
)

// ./buildScript.bat appName=sandbox mrHash=100  branchName=branchName buildMsg="test build"
 // urlCallback="http://www.baidu.com" debug=true startTime=10000
 // extraArgs="{androidPlugins:[{name:\"core\",appVersion:\"5.5.9.2\",latestVersion:{version:\"5.5.9.2_core_2\",url:\"https://static.tancdn.com/tantan/5.5.9.2_core_2_withqt_gms_v8_dxx.apk\",md5:\"6a5b3728f62aa860be17a24375301aa0\"},forceSkipVersions:[]},{name:\"feed\",appVersion:\"5.5.9.2\",latestVersion:{version:\"5.5.9.2_feed_1\",url:\"https://static.tancdn.com/tantan/5.5.9.2_feed_1_withqt_gms_v8_dxx.apk\",md5:\"cb1ae981236669a997371233ebef8f95\"},forceSkipVersions:[]},{name:\"host\",appVersion:\"5.5.9.2\",latestVersion:{version:\"5.5.9.2_host_1\",url:\"https://apk.p1staff.com/signed-release-apks/tantan_3559200_5.5.9.2/host/1/5.5.9.2_host_1_withqt_gms_v8_dxx.apk\",md5:\"\"},forceSkipVersions:[]}]}"

import com.tantanapp.library.script.BuildConfig
import com.tantanapp.library.script.BuildExecutor
import com.tantanapp.library.script.Param

class BuildExecutorImpl() : BuildExecutor() {
  // 继承BuildExecutor，简单复写buildConfig方法即可，更多可复写的方法请查看源码

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
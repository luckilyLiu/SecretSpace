java -version
./gradlew clean
#
./gradlew bundleLawnWithQuickstepMiheDebug  --stacktrace
./gradlew assembleLawnWithQuickstepMiheDebug --stacktrace


save_path=mihe_apks/debug

mkdir -p $save_path

cp build/outputs/apk/lawnWithQuickstepMihe/debug/*.apk $save_path
cp build/outputs/bundle/lawnWithQuickstepMiheDebug/*.aab $save_path
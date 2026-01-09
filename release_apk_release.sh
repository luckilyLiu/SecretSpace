java -version
./gradlew clean
#
./gradlew bundleLawnWithQuickstepMiheRelease  --stacktrace
./gradlew assembleLawnWithQuickstepMiheRelease --stacktrace


save_path=mihe_apks/release

mkdir -p $save_path

cp build/outputs/apk/lawnWithQuickstepMihe/release/*.apk $save_path
cp build/outputs/bundle/lawnWithQuickstepMiheRelease/*.aab $save_path
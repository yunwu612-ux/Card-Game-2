修复说明：
- 将 targetSdk 从 28 升级至 35，以修复 Release 构建中 ExpiredTargetSdkVersion 导致的 lintVitalRelease 失败。
- minSdk 保持 23，因此最低 Android 6.0 的兼容目标不变。
- applicationId、versionCode、versionName 和 Release 签名配置保持不变。
- GitHub Actions 构建前请继续配置 CARDSTORY_KEYSTORE_BASE64、CARDSTORY_STORE_PASSWORD、CARDSTORY_KEY_ALIAS、CARDSTORY_KEY_PASSWORD 四个 Repository secrets。
- 本包尚未在实际 GitHub Actions 环境中运行验证；上传后请以 Actions 结果为准。

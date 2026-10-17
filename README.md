# 卡牌物语 · Android V2.0

科幻机甲主题的竖屏卡牌收集与商店经营游戏。原生 Android Java 项目，支持 Android 6.0（API 23）及以上设备。

## V2.0 内容

- 120 张独立编号的机甲卡牌 PNG 立绘资源（`app/src/main/res/drawable-nodpi/card_001.png` 至 `card_120.png`）。
- 2 列卡牌图鉴：全部、已拥有、未收集筛选。
- 卡牌详情弹窗、稀有度、战力、经营收益、等级升级。
- 标准单抽、十连招募、星核招募与重复卡牌转化。
- 店铺经营、顾客成交、商店升级、员工收益加成。
- 每日补给、经营进度提示、本地 SharedPreferences 存档。
- GitHub Actions 自动构建 Debug APK；配置固定签名 Secrets 后额外构建 Release APK。

## GitHub Actions 构建

将整个 `CardStory` 文件夹中的内容上传至仓库根目录（包括隐藏的 `.github` 文件夹），进入 Actions 运行 `CardStory Android V2.0 Build`。构建完成后从 Artifacts 下载 APK。

### 发布签名 Secrets

沿用 V1.0 已生成的固定签名，勿重新生成密钥。仓库 `Settings → Secrets and variables → Actions` 中应有：

- `CARDSTORY_KEYSTORE_BASE64`
- `CARDSTORY_STORE_PASSWORD`
- `CARDSTORY_KEY_ALIAS`
- `CARDSTORY_KEY_PASSWORD`

签名文件和密码不要提交到公开仓库。Secrets 不齐全时，工作流仍会构建 Debug APK，但不会产出签名 Release APK。

## 版本升级与存档

- `applicationId` 保持 `com.cardstory.game`。
- V2.0 的 `versionCode` 为 2，`versionName` 为 `2.0`。
- 必须继续使用 V1.0 的同一发布密钥签名，才能覆盖安装 Release 版本。
- 游戏存档保存在应用私有本地数据中。正常覆盖安装通常会保留存档；卸载应用或清除应用数据会删除本地存档，因此更新前仍建议备份。

## 构建说明

本项目通过 GitHub Actions 的 Ubuntu runner、JDK 17 和 Gradle 8.9 构建。当前工作环境没有安装 Gradle/Android SDK，因此本 ZIP 尚未在此处实际编译；请以 GitHub Actions 的构建结果为准。

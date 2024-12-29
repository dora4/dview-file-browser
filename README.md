dview-file-browser
![Release](https://jitpack.io/v/dora4/dview-file-browser.svg)
--------------------------------

##### 卡名：Dora视图 File Browser 
###### 卡片类型：效果怪兽
###### 属性：暗
###### 星级：4
###### 种族：魔法师族
###### 攻击力/防御力：400/1700
###### 效果：此卡不会因为对方卡的效果而破坏，并可使其无效化。此卡攻击里侧守备表示的怪兽时，若攻击力高于其守备力，则给予对方此卡原攻击力的伤害，并抽一张卡。每当此卡攻击破坏对方怪兽送去墓地时，你可以破坏对方场上一张魔法或陷阱卡。此卡通常召唤成功的场合，可以从卡组选择一张魔法或陷阱卡加入手卡，之后卡组洗切。

#### Gradle依赖配置

```groovy
// 添加以下代码到项目根目录下的build.gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
// 添加以下代码到app模块的build.gradle
dependencies {
    implementation("com.github.dora4:dora:1.2.29")
    implementation("com.github.getActivity:XXPermissions:18.2")
    implementation("com.github.dora4:dview-file-browser:1.10")
}
```

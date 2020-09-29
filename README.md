[![Version](https://img.shields.io/badge/version-0.0.4-green.svg)](https://bintray.com/xujiaji/maven/ripple-checkbox)

# RippleCheckBox
简洁，舒服，波纹动画，勾选动画，高度可控的波纹CheckBox

> 设计参考：[dribbble](https://dribbble.com/shots/3967195-ToDo-Task-List)

![](https://raw.githubusercontent.com/xujiaji/xujiaji.github.io/pictures/RippleCheckBox/RippleCheckBox_dribbble.gif)

## Screenshot

![](https://raw.githubusercontent.com/xujiaji/xujiaji.github.io/pictures/RippleCheckBox/RippleCheckBox.gif)

> [下载案例Apk](https://github.com/xujiaji/RippleCheckBox/releases/tag/v1.0)

> 使用该控件的开源项目：[玩清单](https://github.com/xujiaji/todo)

## Dependencies
```
implementation 'com.github.xujiaji:ripple-checkbox:0.0.4'
```

## Use
> px： 值为像素单位；<br>
> 0x： 值为颜色，如黑色0xFF000000<br>
> (0-360)： 值的范围0-360<br>

|作用|方法|xml属性|
|-|-|-|
|选中状态      |`setCurrentStatus(Status)`|`rcbStatus                `|
|选中状态，第二个<br>参数是否开启动画效果|`setCurrentStatus(Status,boolean)`||
|中心圆半径    |`setCenterCircleRadius(int)` *(px)*|`rcbCenterCircleRadius     `|
|中心圆线条粗细 |`setCenterCircleStrokeWidth(float)`|`rcbCenterCircleStrokeWidth`|
|中心圆颜色    |`setCenterCircleColor(int)` *(0x)*|`rcbCenterCircleColor      `|
|“√”的粗细    |`setRightStrokeWidth(float)`|`rcbRightStrokeWidth       `|
|“√”的颜色   |`setRightColor(int)` *(0x)* |`rcbRightColor             `|
|“√”的绘制时间 |`setRippleDuration(int)` *(ms)*|`rcbRightDuration          `|
|“√”的启始角度   | `setRightStartDegree(int)` *(0-360)*|`rcbRightStartDegree       `|
|“√”的中间点   |`setRightCenterDegree(int)` *(0-360)*|`rcbRightCenterDegree      `|
|“√”的结束点   |`setRightEndDegree(int)` *(0-360)*|`rcbRightEndDegree         `|
|“√”的拐角平滑度|`setRightCorner(int)` *(px)*|`rcbRightCorner            `|
|波纹圆粗细     |`setRippleStrokeWidth(float)`|`rcbRippleStrokeWidth      `|
|波纹圆颜色     |`setRippleColor(int)` *(0x)*|`rcbRippleColor            `|
|波纹外边距     |`setRippleMargin(int)` *(px)*|`rcbRippleMargin           `|
|波纹扩散时长   |`setRippleDuration(int)` *(ms)*|`rcbRippleDuration         `|

> xml使用案例：

``` xml
<com.xujiaji.library.RippleCheckBox
    android:id="@+id/rippleCheckBox2"
    android:layout_width="100dp"
    android:layout_height="100dp"
    app:rcbCenterCircleColor="@color/colorPrimary"
    app:rcbCenterCircleRadius="12dp"
    app:rcbStatus="HOOK"
    app:rcbRightColor="@color/colorPrimary"
    app:rcbRightStrokeWidth="2dp"
    app:rcbRippleColor="@color/colorPrimaryDark"
    app:rcbRippleStrokeWidth="3dp" />
```

# License
```
   Copyright 2018 XuJiaji

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
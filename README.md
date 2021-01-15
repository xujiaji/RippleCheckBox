[![Version](https://img.shields.io/badge/version-0.0.5-green.svg)](https://bintray.com/xujiaji/maven/ripple-checkbox)

# RippleCheckBox
简洁，舒服，波纹动画，勾选动画，高度可控的波纹CheckBox

> 设计参考：[dribbble](https://dribbble.com/shots/3967195-ToDo-Task-List)

![](https://raw.githubusercontent.com/xujiaji/xujiaji.github.io/pictures/RippleCheckBox/RippleCheckBox_dribbble.gif)

## Screenshot

![](https://raw.githubusercontent.com/xujiaji/xujiaji.github.io/pictures/RippleCheckBox/RippleCheckBox.gif)

> [下载案例Apk](https://github.com/xujiaji/RippleCheckBox/releases/tag/v0.0.5)

> 使用该控件的项目：[玩清单](https://www.coolapk.com/apk/211388)

## Dependencies
```
implementation 'com.github.xujiaji:ripple-checkbox:0.0.5'
```

## Use
> px： 值为像素单位；<br>
> 0x： 值为颜色，如黑色0xFF000000<br>
> (0-360)： 值的范围0-360<br>

|作用|方法|xml属性|
|-|-|-|
|选中状态      |`setCurrentStatus(Status)`|`rcbStatus                `|
|设置圆圈单击后是叉还是勾|`setCircleClickedStatus(Status)`|`rcbCircleClickedStatus`|
|设置圆圈长按后是叉还是勾|`setCircleLongClickedStatus(Status)`|`rcbCircleLongClickedStatus`|
|设置勾勾单击后是圆还是叉|`setHookClickedStatus(Status)`|`rcbHookClickedStatus`|
|设置勾勾长按后是圆还是叉|`setHookLongClickedStatus(Status)`|`rcbHookLongClickedStatus`|
|设置叉叉单击后是圆还是勾|`setCrossClickedStatus(Status)`|`rcbCrossClickedStatus`|
|设置叉叉长按后是圆还是勾|`setCrossLongClickedStatus(Status)`|`rcbCrossLongClickedStatus`|
|是否启用点击（默认启用）|`setEnableClick(boolean)`|`rcbEnableClick`|
|是否启用长按（默认启用）|`setEnableLongClick(boolean)`|`rcbEnableLongClick`|
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
|叉叉粗细|`setDeleteStrokeWidth()`|`rcbDeleteStrokeWidth`|
|叉叉颜色|`setDeleteColor()`|`rcbDeleteColor`|
|叉叉动画时长|`setRippleDuration()`|`rcbDeleteDuration`|
|叉叉棱角弧度|`setDeleteCorner()`|`rcbDeleteCorner`|
|叉叉缩放比例|`setDeleteScale()`|`rcbDeleteScale`|

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
            app:rcbRippleStrokeWidth="3dp"
            app:rcbDeleteScale="0.8"
            app:rcbDeleteColor="@android:color/holo_red_light"/>
```

> 具体实用案例请参照demo代码~

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
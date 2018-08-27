# ConfirmationCodeView
ConfirmationCodeView is a Android custom view for inputing confirmation code.


[![Platform](https://img.shields.io/badge/platform-android-green.svg)](http://developer.android.com/index.html)
<img src="https://img.shields.io/badge/license-MIT-green.svg?style=flat">
[![SDK](https://img.shields.io/badge/API-12%2B-green.svg?style=flat)](https://android-arsenal.com/api?level=11)
[![Download](https://api.bintray.com/packages/wray/rayMaven/ConfirmationCodeView/images/download.svg)](https://bintray.com/wray/rayMaven/ConfirmationCodeView/_latestVersion)

## Demo 
<img src="/screenshot/screenshot.gif" width="360px"/>

### Gradle
```groovy
dependencies{
    compile 'com.wray:ConfirmationCodeView:1.0.0@aar'
}
```
## Maven
```xml
<dependency>
  <groupId>com.wray</groupId>
  <artifactId>ConfirmationCodeView</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
```
## Attributes instruction
attribute|instruction|value
---|---|---
[ccvCount](/CCView/src/main/res/values/attrs.xml)|The confirmation code length.|integer
[ccvLineWidth](/CCView/src/main/res/values/attrs.xml)|The width of the code border or underline.|dimension
[ccvSize](/CCView/src/main/res/values/attrs.xml)|the size of each code box.|dimension
[ccvPadding](/CCView/src/main/res/values/attrs.xml)|the padding of each code box.|dimension
[ccvText](/CCView/src/main/res/values/attrs.xml)|the content string.(The part beyond the length will be truncated)|string
[ccvContentType](/CCView/src/main/res/values/attrs.xml)|The content input type.<br>- mixture : both letter and digit.<br>- letter : accept letter only.<br>- digit : accept digit only|string
[ccvDirection](/CCView/src/main/res/values/attrs.xml)|The input direction.<br>- ltr : from left to right.<br>- rtl : from right to left|string
[ccvBorderShape](/CCView/src/main/res/values/attrs.xml)|The shape of the border.<br>- line : use underline only.<br>- square : use a square border.<br>- squareLine : use underline with square content area.<br>- rectangle : use a rectangle border.<br>- circle : use a circle border.|string
[ccvBorderColor](/CCView/src/main/res/values/attrs.xml)|The border color.|color \| reference
[ccvBorderSelectColor](/CCView/src/main/res/values/attrs.xml)|The border selection color.|color \| reference
[ccvBorderRadius](/CCView/src/main/res/values/attrs.xml)|The border corner radius.|dimension
[ccvPasswordMode](/CCView/src/main/res/values/attrs.xml)|Indicate the password mode.|boolean
[ccvTextColor](/CCView/src/main/res/values/attrs.xml)|The text color.|color \| reference
[ccvTextSelectColor](/CCView/src/main/res/values/attrs.xml)|The text selection color.|color \| reference
[ccvPasswordColor](/CCView/src/main/res/values/attrs.xml)|The password color.|color \| reference
[ccvPasswordSelectColor](/CCView/src/main/res/values/attrs.xml)|The password selection color.|color \| reference
[ccvContentColor](/CCView/src/main/res/values/attrs.xml)|The content background color.|color \| reference
[ccvContentSelectColor](/CCView/src/main/res/values/attrs.xml)|The content selection background color.|color \| reference

## Useage
#### XML
Simple Use：
```xml
<com.wray.ccview.ConfirmationCodeView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"/>
 ```
 More Attibutes：
 ```xml
 <com.wray.ccview.ConfirmationCodeView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:ccvBorderShape="square"
            app:ccvCount="4"
            app:ccvContentType="letter"
            app:ccvDirection="rtl"
            app:ccvSize="45dp" />
 ```
 #### Code
 ```Java
 ConfirmationCodeView ccv;
 ......
 ccv.setBorderColor(color);
 ccv.setBorderSelectColor(color);
 ccv.setContentColor(android.R.color.white);
 ccv.setTextColor(color);
 ccv.setTextSelectColor(android.R.color.white);
 ccv.setContentSelectColor(color);
 ccv.setPasswordColor(color);
 ccv.setPasswordSelectColor(android.R.color.white);
 ccv.setOnInputCompletionListener(new ConfirmationCodeView.OnInputCompletionListener() {
            @Override
            public void onCompletion(ConfirmationCodeView ccv, String content) {
                Log.d("", "Your Input Code : " + content);
            }
        });
 ```
 
### License
```
The MIT License (MIT)

Copyright (c) 2018 Ray

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

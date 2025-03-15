# KavehColorPicker

[![](https://jitpack.io/v/Mohammad3125/KavehColorPicker.svg)](https://jitpack.io/#Mohammad3125/KavehColorPicker)

This library provides a customizable color picker for android with simple interface 

<a href="https://ibb.co/qFPc5sH"><img src="https://i.ibb.co/vvKTYPN/color-picker.png" alt="color-picker" border="0" /></a>

#### Features 
- ColorPicker: changes values of brightness and satruation in HSV color model
- HueSlider: changes hue values of HSV color model and passes it into ColorPicker
- AlphaSlider: changes alpha value of provided color by ColorPicker
- State of components are preserved during configuration changes (screen rotation etc...)
- View size change events are implemented correctly to provide nice animations and layout changes
- Components of this library both work together and separately
- Change the value of ColorPicker and Sliders via code


# SDK 🔢
It supports API 18 (APIs below 18 aren't tested)

# How to make library work 🔨

The library has 3 main views called `KavehColorPicker` `KavehHueSlider` `KavehColorAlphaSlider`
To add them to xml layout do the following:
```xml
    <ir.kotlin.kavehcolorpicker.KavehColorPicker
        android:id="@+id/colorPickerView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:layout_constraintEnd_toStartOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <ir.kotlin.kavehcolorpicker.KavehHueSlider
        android:id="@+id/hueSlider"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:layout_constraintEnd_toStartOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="parent" />

    <ir.kotlin.kavehcolorpicker.KavehHueSlider
        android:id="@+id/colorAlphaSlider"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:layout_constraintEnd_toStartOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="parent" />
```

To connect views to eachother in your code find the views by id then connect them to eachother:

```kotlin
        val colorPicker = findViewById<KavehColorPicker>(R.id.colorPickerView)
        val hueSlider = findViewById<KavehHueSlider>(R.id.hueSlider)
        val colorAlphaSlider = findViewById<KavehColorAlphaSlider>(R.id.colorAlphaSlider)
        
        colorPicker.alphaSliderView = colorAlphaSlider
        colorPicker.hueSliderView = hueSlider
```


To extract color from color picker do the following on `KavehColorPicker`
```kotlin
// Kotlin
colorPicker.color
// Java
colorPicker.getColor();
```

# Listeners 👂

Each view has its own listener to be used separately. Users on Kotlin can call the methods described below and Java users can
call the same method name but instead pass an interface. 
If you connected `KavehHueSlider` and `KavehColorAlphaSlider` to `KavehColorPicker` , you don't need to call hue and alpha listeners
because they are calculated in `KavehColorPicker`
```kotlin
        // KavehColorPicker
        colorPicker.setOnColorChangedListener { color ->

        }

        // KavehHueSlider
        hueSlider.setOnHueChangedListener { hue, argbColor ->
            // Hue value is between [0..360]
            // argbColor is just the color int representation of hue value with full brightness and saturation.
        }

        // KavehColorAlphaSlider
        colorAlphaSlider.setOnAlphaChangedListener { alpha ->
            // Alpha value between [0..1]
        }
```

# Customization
You can change indicator's (circle knob on sliders) stroke size in xml
```xml
app:sliderStrokeSize="7dp"
```
In code
```kotlin
slider.strokeSize = 24f
```

You can change indicator's stroke color in xml
```xml
 app:sliderStrokeColor="@android:color/black"
```
In code
```kotlin
slider.strokeColor = Color.BLACK
```

You can change sliders (only `KavehHueSlider` and `KavehColorAlphaSlider`) line's cap in xml
```xml
app:sliderBarStrokeCap="Butt|Round|Square"
```

In code
```kotlin
slider.lineStrokeCap = Paint.Cap.ROUND
```

You can change indicator's size only in `KavehColorPicker`
```kotlin
// Default value is 24dp
colorPicker.circleIndicatorRadius = 24f
```

You can change the color of `KavehColorPicker' via 'color' setter
```kotlin
kavehColorPicker.color = Color.parseColor("#962626")
kavehColorPicker.color = Color.argb(128,255,255,255)
// Changing the color property in KavehColorPicker also changes the values of other sliders if the color was set after setting hue and alpha slider to KavehColorPicker.
// This code changes the color of KavehColorPicker that causes other sliders to change value also.
kavehColorPicker.alphaSliderView = alphaSlider
kavehColorPicker.hueSliderView = hueSlider
kavehColorPicker.color = Color.argb(128,255,255,255)
// But if you change color before setting the other sliders to KavehColorPicker then the other sliders won't change.
kavehColorPicker.color = Color.argb(128,255,255,255)
kavehColorPicker.alphaSliderView = alphaSlider
kavehColorPicker.hueSliderView = hueSlider
```
You can change the hue of 'KavehHueSlider' via 'hue' setter
```kotlin
// Hue between [0..360]
kavehHueSlider.hue = 52f
```
You can change the alpha of 'KavehAlphaSlider' via 'alphaValue' setter
```kotlin
// Alpha between [0..1]
kavehAlphaSlider.alphaValue = 0.4f
```

# How to add dependency
Add jitpack repository to your top-level `build.gradle`
```gradle
  repositories {
        ..
        ..
        maven { url 'https://jitpack.io' }
    }

```
Or if it doesn't find library or repositories aren't defined here go to `settings.gradle` and add it
```gradle
  dependencyResolutionManagement {
    ..
    repositories {
      ..
      ..
      maven { url 'https://jitpack.io' }
    }
}
```
Finally add dependency to your `build.gradle`
```gradle
 implementation 'com.github.Mohammad3125:KavehColorPicker:v1.0.0'
```

# Changelog
- **version 1.1.0**: added ability to change the value of sliders via code which causes other sliders to change value also. see customization section for more information.

# License
```
MIT License

Copyright (c) 2023 Mohammad Hossein Naderi

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

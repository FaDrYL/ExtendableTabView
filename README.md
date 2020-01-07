[![](https://jitpack.io/v/FaDrYL/ExtendableTabView.svg)](https://jitpack.io/#FaDrYL/ExtendableTabView)
# ExtendableTabView
A custom Tab.



## Screenshots
> Sample of animation
<img src="https://github.com/FaDrYL/ExtendableTabView/blob/master/Screenshots/animateSample.gif" alt="Sample of animation" width="250">

> Images
<img src="https://github.com/FaDrYL/ExtendableTabView/blob/master/Screenshots/Screenshot_1.png" alt="Screenshot 1" width="250">
<img src="https://github.com/FaDrYL/ExtendableTabView/blob/master/Screenshots/Screenshot_2.png" alt="Screenshot 2" width="250">
<img src="https://github.com/FaDrYL/ExtendableTabView/blob/master/Screenshots/Screenshot_3.png" alt="Screenshot 3" width="250">


## Customize
### Orientation (int)
| Extend to | int |
|:---------:|:---:|
|    Top    |  0  |
|   Bottom  |  1  |
|    Left   |  2  |
|   Right   |  3  |
```xml
<com.yl.widgets.extendabletabview.ExtendableTabView
	android:id="@+id/extendableTabView"
	android:layout_width="match_parent"
	android:layout_height="200dp"
	android:layout_marginBottom="10dp"
	android:layout_marginLeft="10dp"
	app:layout_constraintBottom_toBottomOf="parent"
	app:layout_constraintLeft_toLeftOf="parent"
	app:layout_constraintRight_toRightOf="parent"
	app:orientation_selector="2"
	app:orientation_extend="0"
	app:tab_width="25dp"
	app:tab_height="40dp"/>
```
### Add Item(s)
```Java
LinearLayout ll_basic = (LinearLayout) getLayoutInflater().inflate(R.layout.tab_basic, null);
final TextView tv_textSize_hint = ll_basic.findViewById(R.id.tab_basic_textSize_hint);
SeekBar seekBar = ll_basic.findViewById(R.id.tab_basic_textSize);
seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        tv_textSize_hint.setText(String.valueOf(progress) + "sp");
        ((TextView) findViewById(R.id.main_text)).setTextSize(TypedValue.COMPLEX_UNIT_SP, progress);
    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) { }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) { }
});

ExtendableTabView tab = findViewById(R.id.extendableTabView);
ArrayList<String> strs = new ArrayList<String>();
strs.add("BASIC");
strs.add("90");
strs.add("100");
LinearLayout[] lls = new LinearLayout[]{ll_basic,
        null,
        (LinearLayout) View.inflate(getApplicationContext(), R.layout.sample_body_3, null)};


/* without expand:  .addItem(String title, OnClickListener onClickListener)
 *                  .addItems(ArrayList<String>, ArrayList<OnClickListener>)
 *
 * with expand:     .addItem(String title, LinearLayout LinearLayoutlinearLayout)
 *                  .addItems(ArrayList<String>, LinearLayout[])
*/
tab.addItems(strs, lls);
// or (for add one item): tab.addItem("BASIC", ll_basic)
```


## How to use
### Gradle
##### Step 1. Add it in your root build.gradle at the end of repositories:
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
##### Step 2. Add the dependency:
```
dependencies {
    ...
    implementation 'com.github.FaDrYL:ExtendableTabView:Tag'
}
```

### Maven
##### Step 1. Add the JitPack repository to your build file
```
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
##### Step 2. Add the dependency:
```
<dependency>
    <groupId>com.github.FaDrYL</groupId>
    <artifactId>ExtendableTabView</artifactId>
    <version>Tag</version>
</dependency>
```
	

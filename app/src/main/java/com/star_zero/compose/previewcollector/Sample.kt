package com.star_zero.compose.previewcollector

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.star_zero.compose.previewcollector.ui.theme.MyTheme

@Composable
fun Foo() {
    Column {
        Text(text = "Jetpack")
        Text(text = "Compose")
    }
}

@Composable
fun Bar() {
    Column {
        Text(text = "Android")
        Text(text = "Kotlin")
    }
}

@Preview
@Composable
fun PreviewFoo() {
    MyTheme {
        Foo()
    }
}

@Preview
@Composable
fun PreviewBar() {
    MyTheme {
        Bar()
    }
}

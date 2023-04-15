package nl.marc_apps.tts_demo

import org.jetbrains.compose.web.css.*

object AppStyle : StyleSheet() {
    init {
        "body" style {
            margin(32.px)
            fontFamily("Roboto", "sans-serif")
        }

        "section" style {
            marginBottom(16.px)
        }

        "button" style {
            paddingLeft(16.px)
            paddingRight(16.px)
            paddingTop(4.px)
            paddingBottom(4.px)
        }
    }
}

package com.smartcash.app.navigation

import java.net.URLDecoder
import java.net.URLEncoder

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Earn : Screen("earn")
    object Wallet : Screen("wallet")
    object Profile : Screen("profile")
    data class WebView(
        val url: String = "{url}",
        val title: String = "{title}",
    ) : Screen("webview/{url}/{title}") {
        companion object {
            const val ROUTE = "webview/{url}/{title}"

            fun routeFor(url: String, title: String): String {
                val encodedUrl = URLEncoder.encode(url, "UTF-8")
                val encodedTitle = URLEncoder.encode(title, "UTF-8")
                return "webview/$encodedUrl/$encodedTitle"
            }

            fun decodeUrl(encoded: String): String {
                return URLDecoder.decode(encoded, "UTF-8")
            }

            fun decodeTitle(encoded: String): String {
                return URLDecoder.decode(encoded, "UTF-8")
            }
        }
    }
}

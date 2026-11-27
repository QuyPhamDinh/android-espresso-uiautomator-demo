package com.wdiodemoapp

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import com.wdiodemoapp.databinding.ActivityMainBinding
import com.wdiodemoapp.ui.drag.DragFragment
import com.wdiodemoapp.ui.forms.FormsFragment
import com.wdiodemoapp.ui.home.HomeFragment
import com.wdiodemoapp.ui.login.LoginFragment
import com.wdiodemoapp.ui.swipe.SwipeFragment
import com.wdiodemoapp.ui.webview.WebViewFragment

class MainActivity : AppCompatActivity() {

    private data class TabItem(
        val id: Int,
        val tag: String,
        val container: View,
        val icon: ImageView,
        val label: TextView,
        val factory: () -> Fragment,
    )

    private lateinit var binding: ActivityMainBinding
    private lateinit var tabs: List<TabItem>
    private var activeTabId: Int = R.id.nav_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout(),
            )
            view.updatePadding(
                left = insets.left,
                top = insets.top,
                right = insets.right,
                bottom = insets.bottom,
            )
            WindowInsetsCompat.CONSUMED
        }

        tabs = listOf(
            TabItem(R.id.nav_home, TAG_HOME, binding.navHome, binding.navHomeIcon, binding.navHomeLabel) { HomeFragment() },
            TabItem(R.id.nav_webview, TAG_WEBVIEW, binding.navWebview, binding.navWebviewIcon, binding.navWebviewLabel) { WebViewFragment() },
            TabItem(R.id.nav_login, TAG_LOGIN, binding.navLogin, binding.navLoginIcon, binding.navLoginLabel) { LoginFragment() },
            TabItem(R.id.nav_forms, TAG_FORMS, binding.navForms, binding.navFormsIcon, binding.navFormsLabel) { FormsFragment() },
            TabItem(R.id.nav_swipe, TAG_SWIPE, binding.navSwipe, binding.navSwipeIcon, binding.navSwipeLabel) { SwipeFragment() },
            TabItem(R.id.nav_drag, TAG_DRAG, binding.navDrag, binding.navDragIcon, binding.navDragLabel) { DragFragment() },
        )

        if (savedInstanceState != null) {
            activeTabId = savedInstanceState.getInt(KEY_ACTIVE_TAB, R.id.nav_home)
        } else {
            initFragments()
        }

        tabs.forEach { tab ->
            tab.container.setOnClickListener {
                showTab(tab.id)
            }
        }

        val initialTab = resolveDeepLinkTab(intent) ?: activeTabId
        showTab(initialTab)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val deepLinkTab = resolveDeepLinkTab(intent) ?: return
        showTab(deepLinkTab)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_ACTIVE_TAB, activeTabId)
    }

    private fun initFragments() {
        val tx = supportFragmentManager.beginTransaction()
        tabs.forEach { tab ->
            val fragment = tab.factory()
            tx.add(R.id.fragment_container, fragment, tab.tag)
            if (tab.id != R.id.nav_home) {
                tx.hide(fragment)
            }
        }
        tx.commitNow()
    }

    private fun showTab(targetTabId: Int) {
        activeTabId = targetTabId
        val tx = supportFragmentManager.beginTransaction()
        tabs.forEach { tab ->
            var fragment = supportFragmentManager.findFragmentByTag(tab.tag)
            if (fragment == null) {
                fragment = tab.factory()
                tx.add(R.id.fragment_container, fragment, tab.tag)
            }
            val selected = tab.id == targetTabId
            if (selected) {
                tx.show(fragment)
            } else {
                tx.hide(fragment)
            }
            styleTab(tab, selected)
        }
        tx.commitAllowingStateLoss()
    }

    private fun styleTab(tab: TabItem, selected: Boolean) {
        val color = ContextCompat.getColor(
            this,
            if (selected) R.color.wdio_orange else R.color.text_secondary,
        )
        tab.container.isSelected = selected
        tab.label.setTextColor(color)
        ImageViewCompat.setImageTintList(tab.icon, ColorStateList.valueOf(color))
    }

    private fun resolveDeepLinkTab(intent: Intent?): Int? {
        val data: Uri = intent?.data ?: return null
        if (data.scheme != "wdio") return null

        val route = (data.host ?: data.path?.trimStart('/') ?: "").lowercase()
        return when (route) {
            "", "home" -> R.id.nav_home
            "webview" -> R.id.nav_webview
            "login" -> R.id.nav_login
            "forms" -> R.id.nav_forms
            "swipe" -> R.id.nav_swipe
            "drag" -> R.id.nav_drag
            else -> R.id.nav_home
        }
    }

    companion object {
        private const val KEY_ACTIVE_TAB = "active_tab"
        private const val TAG_HOME = "tab_home"
        private const val TAG_WEBVIEW = "tab_webview"
        private const val TAG_LOGIN = "tab_login"
        private const val TAG_FORMS = "tab_forms"
        private const val TAG_SWIPE = "tab_swipe"
        private const val TAG_DRAG = "tab_drag"
    }
}

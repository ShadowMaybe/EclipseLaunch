package me.shadow.eclipselaunch.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.movtery.anim.AnimPlayer
import com.movtery.anim.animations.Animations
import me.shadow.eclipselaunch.R
import me.shadow.eclipselaunch.ui.compose.EclipseMiuixTheme
import me.shadow.eclipselaunch.ui.compose.SettingsScreen

class SettingsFragment : FragmentWithAnim() {
    companion object {
        const val TAG: String = "SettingsFragment"
    }

    private var composeView: ComposeView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        composeView = ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                EclipseMiuixTheme {
                    SettingsScreen()
                }
            }
        }
        return composeView!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // All content is now Compose
    }

    override fun slideIn(animPlayer: AnimPlayer) {
        composeView?.let {
            animPlayer.apply(AnimPlayer.Entry(it, Animations.BounceInRight))
        }
    }

    override fun slideOut(animPlayer: AnimPlayer) {
        composeView?.let {
            animPlayer.apply(AnimPlayer.Entry(it, Animations.FadeOutLeft))
        }
    }
}

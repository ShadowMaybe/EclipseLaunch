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
import me.shadow.eclipselaunch.ui.compose.AboutScreen
import me.shadow.eclipselaunch.ui.compose.ContributorItem
import me.shadow.eclipselaunch.utils.ZHTools
import me.shadow.eclipselaunch.utils.path.UrlManager

class AboutFragment : FragmentWithAnim() {
    companion object {
        const val TAG: String = "AboutFragment"
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
                me.shadow.eclipselaunch.ui.compose.EclipseMiuixTheme {
                    val ctx = requireContext()
                    val contributors = listOf(
                        ContributorItem(
                            ctx.resources.getDrawable(R.drawable.ic_pojav_full, ctx.theme),
                            "PojavLauncherTeam",
                            getString(R.string.about_PojavLauncher_desc),
                            "Github",
                            "https://github.com/PojavLauncherTeam/PojavLauncher"
                        ),
                        ContributorItem(
                            ctx.resources.getDrawable(R.drawable.image_about_movtery, ctx.theme),
                            "\u58a8\u5317MovTery",
                            getString(R.string.about_MovTery_desc),
                            getString(R.string.about_access_space),
                            "https://space.bilibili.com/2008204513"
                        ),
                        ContributorItem(
                            ctx.resources.getDrawable(R.drawable.image_about_mcmod, ctx.theme),
                            "MC \u767e\u79d1",
                            getString(R.string.about_mcmod_desc),
                            getString(R.string.about_access_link),
                            UrlManager.URL_MCMOD
                        ),
                        ContributorItem(
                            ctx.resources.getDrawable(R.drawable.image_about_verafirefly, ctx.theme),
                            "Vera-Firefly",
                            getString(R.string.about_VeraFirefly_desc),
                            getString(R.string.about_access_space),
                            "https://space.bilibili.com/1412062866"
                        ),
                        ContributorItem(
                            ctx.resources.getDrawable(R.drawable.image_about_lingmuqiuzhu, ctx.theme),
                            "\u60c9\u6728\u6e3c\u7af9",
                            getString(R.string.about_LingMuQiuZhu_desc),
                            getString(R.string.about_access_space),
                            "https://space.bilibili.com/515165764"
                        ),
                        ContributorItem(
                            ctx.resources.getDrawable(R.drawable.image_about_shirosakimio, ctx.theme),
                            "ShirosakiMio",
                            getString(R.string.about_ShirosakiMio_desc),
                            getString(R.string.about_access_space),
                            "https://space.bilibili.com/35801833"
                        ),
                        ContributorItem(
                            ctx.resources.getDrawable(R.drawable.image_about_bangbang93, ctx.theme),
                            "bangbang93",
                            getString(R.string.about_bangbang93_desc),
                            getString(R.string.about_button_support_development),
                            "https://afdian.com/a/bangbang93"
                        )
                    )

                    AboutScreen(
                        contributors = contributors,
                        onOpenLink = { url ->
                            ZHTools.openLink(requireActivity(), url)
                        },
                        onBack = {
                            ZHTools.onBackPressed(requireActivity())
                        }
                    )
                }
            }
        }
        return composeView!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // All content is now Compose — nothing to initialize
    }

    override fun slideIn(animPlayer: AnimPlayer) {
        composeView?.let {
            animPlayer.apply(AnimPlayer.Entry(it, Animations.BounceInDown))
        }
    }

    override fun slideOut(animPlayer: AnimPlayer) {
        composeView?.let {
            animPlayer.apply(AnimPlayer.Entry(it, Animations.FadeOutUp))
        }
    }
}

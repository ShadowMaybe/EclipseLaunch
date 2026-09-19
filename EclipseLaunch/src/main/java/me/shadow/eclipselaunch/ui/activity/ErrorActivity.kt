package me.shadow.eclipselaunch.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import me.shadow.eclipselaunch.InfoCenter
import me.shadow.eclipselaunch.R
import me.shadow.eclipselaunch.ui.compose.EclipseMiuixTheme
import me.shadow.eclipselaunch.ui.compose.ErrorScreen
import me.shadow.eclipselaunch.utils.ZHTools
import net.kdt.pojavlaunch.Tools

class ErrorActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val extras = intent.extras
        extras ?: run {
            finish()
            return
        }

        if (extras.getBoolean(BUNDLE_IS_LAUNCHER_CRASH, false)) {
            showLauncherCrash(extras)
            return
        }
        if (extras.getBoolean(BUNDLE_IS_GAME_CRASH, false)) {
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
            showGameCrash(extras)
            return
        }
        if (extras.getBoolean(BUNDLE_EASTER_EGG, false)) {
            showEasterEgg()
            return
        }

        finish()
    }

    private fun showLauncherCrash(extras: Bundle) {
        val throwable = extras.getSerializable(BUNDLE_THROWABLE) as Throwable?
        val stackTrace = if (throwable != null) Tools.printToString(throwable) else "<null>"
        val strSavePath = extras.getString(BUNDLE_SAVE_PATH)
        val errorText = "$strSavePath :\r\n\r\n$stackTrace"

        setContent {
            EclipseMiuixTheme {
                ErrorScreen(
                    errorTitle = InfoCenter.replaceName(this, R.string.error_fatal),
                    errorText = errorText,
                    onConfirm = { finish() },
                    onRestart = {
                        startActivity(Intent(this@ErrorActivity, SplashActivity::class.java))
                    },
                    onShareLog = { ZHTools.shareLogs(this) }
                )
            }
        }
    }

    private fun showGameCrash(extras: Bundle) {
        val code = extras.getInt(BUNDLE_CODE, 0)
        if (code == 0) {
            finish()
            return
        }
        val errorText = if (extras.getBoolean(BUNDLE_IS_SIGNAL))
            getString(R.string.game_singnal_message, code)
        else
            getString(R.string.game_exit_message, code)

        setContent {
            EclipseMiuixTheme {
                ErrorScreen(
                    errorTitle = getString(R.string.generic_wrong_tip),
                    errorText = errorText,
                    showTip = true,
                    showNoScreenshot = true,
                    onConfirm = { finish() },
                    onRestart = {
                        startActivity(Intent(this@ErrorActivity, SplashActivity::class.java))
                    },
                    onShareLog = { ZHTools.shareLogs(this) }
                )
            }
        }
    }

    private fun showEasterEgg() {
        setContent {
            EclipseMiuixTheme {
                ErrorScreen(
                    errorTitle = InfoCenter.replaceName(this, R.string.error_fatal),
                    errorText = "",
                    isEasterEgg = true,
                    onConfirm = { finish() },
                    onRestart = { },
                    onShareLog = { }
                )
            }
        }
    }

    companion object {
        private const val BUNDLE_IS_LAUNCHER_CRASH = "is_launcher_crash"
        private const val BUNDLE_IS_GAME_CRASH = "is_game_crash"
        private const val BUNDLE_IS_SIGNAL = "is_signal"
        private const val BUNDLE_CODE = "code"
        private const val BUNDLE_THROWABLE = "throwable"
        private const val BUNDLE_SAVE_PATH = "save_path"
        private const val BUNDLE_EASTER_EGG = "easter_egg"

        @JvmStatic
        fun showLauncherCrash(ctx: Context, savePath: String?, th: Throwable?) {
            val intent = Intent(ctx, ErrorActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(BUNDLE_THROWABLE, th)
            intent.putExtra(BUNDLE_SAVE_PATH, savePath)
            intent.putExtra(BUNDLE_IS_LAUNCHER_CRASH, true)
            ctx.startActivity(intent)
        }

        @JvmStatic
        fun showExitMessage(
            ctx: Context,
            code: Int,
            isSignal: Boolean
        ) {
            val intent = Intent(ctx, ErrorActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(BUNDLE_CODE, code)
            intent.putExtra(BUNDLE_IS_LAUNCHER_CRASH, false)
            intent.putExtra(BUNDLE_IS_SIGNAL, isSignal)
            intent.putExtra(BUNDLE_IS_GAME_CRASH, true)
            ctx.startActivity(intent)
        }

        @JvmStatic
        fun showEasterEgg(ctx: Context) {
            val intent = Intent(ctx, ErrorActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(BUNDLE_EASTER_EGG, true)
            ctx.startActivity(intent)
        }
    }
}

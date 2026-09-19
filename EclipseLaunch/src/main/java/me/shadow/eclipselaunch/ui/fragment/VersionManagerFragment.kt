package me.shadow.eclipselaunch.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.movtery.anim.AnimPlayer
import com.movtery.anim.animations.Animations
import me.shadow.eclipselaunch.R
import me.shadow.eclipselaunch.feature.version.NoVersionException
import me.shadow.eclipselaunch.feature.version.Version
import me.shadow.eclipselaunch.feature.version.VersionsManager
import me.shadow.eclipselaunch.task.Task
import me.shadow.eclipselaunch.task.TaskExecutors
import me.shadow.eclipselaunch.ui.compose.EclipseMiuixTheme
import me.shadow.eclipselaunch.ui.compose.VersionManagerScreen
import me.shadow.eclipselaunch.ui.dialog.TipDialog
import me.shadow.eclipselaunch.utils.ZHTools
import me.shadow.eclipselaunch.utils.file.FileDeletionHandler
import net.kdt.pojavlaunch.Tools
import java.io.File

class VersionManagerFragment : FragmentWithAnim() {
    companion object {
        const val TAG: String = "VersionManagerFragment"
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
                    VersionManagerScreen(
                        onModsClick = { openMods() },
                        onGamePathClick = { openPath { it } },
                        onResourcePathClick = { openPath { File(it, "/resourcepacks") } },
                        onWorldPathClick = { openPath { File(it, "/saves") } },
                        onShaderPathClick = { openPath { File(it, "/shaderpacks") } },
                        onScreenshotPathClick = { openPath { File(it, "/screenshots") } },
                        onLogsPathClick = { openPath { File(it, "/logs") } },
                        onCrashReportPathClick = { openPath { File(it, "/crash-reports") } },
                        onVersionSettingsClick = { openVersionSettings() },
                        onVersionRenameClick = { renameVersion() },
                        onVersionCopyClick = { copyVersion() },
                        onVersionDeleteClick = { deleteVersion() }
                    )
                }
            }
        }
        return composeView!!
    }

    private fun getVersion(): Version? {
        val activity = requireActivity()
        return VersionsManager.getCurrentVersion() ?: run {
            Tools.showError(activity, getString(R.string.version_manager_no_installed_version), NoVersionException("There is no installed version"))
            null
        }
    }

    private fun openMods() {
        val version = getVersion() ?: return
        val bundle = Bundle()
        val modsDir = File(version.getGameDir(), "mods").apply { mkdirs() }
        bundle.putString(ModsFragment.BUNDLE_ROOT_PATH, modsDir.absolutePath)
        ZHTools.swapFragmentWithAnim(this, ModsFragment::class.java, ModsFragment.TAG, bundle)
    }

    private fun openPath(transform: (File) -> File) {
        val version = getVersion() ?: return
        val dir = transform(File(version.getGameDir().absolutePath)).apply { mkdirs() }
        val bundle = Bundle()
        bundle.putString(FilesFragment.BUNDLE_LOCK_PATH, dir.absolutePath)
        bundle.putString(FilesFragment.BUNDLE_LIST_PATH, dir.absolutePath)
        bundle.putBoolean(FilesFragment.BUNDLE_QUICK_ACCESS_PATHS, false)
        ZHTools.swapFragmentWithAnim(this, FilesFragment::class.java, FilesFragment.TAG, bundle)
    }

    private fun openVersionSettings() {
        ZHTools.swapFragmentWithAnim(this, VersionConfigFragment::class.java, VersionConfigFragment.TAG, null)
    }

    private fun renameVersion() {
        val version = getVersion() ?: return
        VersionsManager.openRenameDialog(requireActivity(), version) {
            Tools.backToMainMenu(requireActivity())
        }
    }

    private fun copyVersion() {
        val version = getVersion() ?: return
        VersionsManager.openCopyDialog(requireActivity(), version)
    }

    private fun deleteVersion() {
        val activity = requireActivity()
        val version = getVersion() ?: return
        TipDialog.Builder(activity)
            .setTitle(R.string.generic_warning)
            .setMessage(activity.getString(R.string.version_manager_delete_tip, version.getVersionName()))
            .setWarning()
            .setConfirmClickListener {
                FileDeletionHandler(
                    activity,
                    listOf(version.getVersionPath()),
                    Task.runTask {
                        VersionsManager.refresh("VersionManagerFragment:versionDelete")
                    }.ended(TaskExecutors.getAndroidUI()) {
                        Tools.backToMainMenu(activity)
                    }
                ).start()
            }
            .showDialog()
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

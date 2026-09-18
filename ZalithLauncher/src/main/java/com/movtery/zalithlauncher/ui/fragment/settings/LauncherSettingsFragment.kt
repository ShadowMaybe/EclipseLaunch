package com.movtery.zalithlauncher.ui.fragment.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.movtery.anim.AnimPlayer
import com.movtery.anim.animations.Animations
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.databinding.SettingsFragmentLauncherBinding
import com.movtery.zalithlauncher.event.single.PageOpacityChangeEvent
import com.movtery.zalithlauncher.setting.AllSettings
import com.movtery.zalithlauncher.ui.fragment.CustomBackgroundFragment
import com.movtery.zalithlauncher.ui.fragment.FragmentWithAnim
import com.movtery.zalithlauncher.ui.fragment.settings.wrapper.BaseSettingsWrapper
import com.movtery.zalithlauncher.ui.fragment.settings.wrapper.ListSettingsWrapper
import com.movtery.zalithlauncher.ui.fragment.settings.wrapper.SeekBarSettingsWrapper
import com.movtery.zalithlauncher.ui.fragment.settings.wrapper.SwitchSettingsWrapper
import com.movtery.zalithlauncher.utils.CleanUpCache.Companion.start
import com.movtery.zalithlauncher.utils.ZHTools
import net.kdt.pojavlaunch.LauncherActivity
import org.greenrobot.eventbus.EventBus

class LauncherSettingsFragment() : AbstractSettingsFragment(R.layout.settings_fragment_launcher, SettingCategory.LAUNCHER) {
    private lateinit var binding: SettingsFragmentLauncherBinding
    private var parentFragment: FragmentWithAnim? = null

    constructor(parentFragment: FragmentWithAnim?) : this() {
        this.parentFragment = parentFragment
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SettingsFragmentLauncherBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val context = requireContext()

        SwitchSettingsWrapper(
            context,
            AllSettings.checkLibraries,
            binding.checkLibrariesLayout,
            binding.checkLibraries
        )

        SwitchSettingsWrapper(
            context,
            AllSettings.verifyManifest,
            binding.verifyManifestLayout,
            binding.verifyManifest
        )

        SwitchSettingsWrapper(
            context,
            AllSettings.resourceImageCache,
            binding.resourceImageCacheLayout,
            binding.resourceImageCache
        )

        SwitchSettingsWrapper(
            context,
            AllSettings.addFullResourceName,
            binding.addFullResourceNameLayout,
            binding.addFullResourceName
        )

        ListSettingsWrapper(
            context,
            AllSettings.downloadSource,
            binding.downloadSourceLayout,
            binding.downloadSourceTitle,
            binding.downloadSourceValue,
            R.array.download_source_names, R.array.download_source_values
        )

        SeekBarSettingsWrapper(
            context,
            AllSettings.maxDownloadThreads,
            binding.maxDownloadThreadsLayout,
            binding.maxDownloadThreadsTitle,
            binding.maxDownloadThreadsSummary,
            binding.maxDownloadThreadsValue,
            binding.maxDownloadThreads,
            ""
        )

        ListSettingsWrapper(
            context,
            AllSettings.launcherTheme,
            binding.launcherThemeLayout,
            binding.launcherThemeTitle,
            binding.launcherThemeValue,
            R.array.launcher_theme_names, R.array.launcher_theme_values
        ).setRequiresReboot()

        BaseSettingsWrapper(
            context,
            binding.customBackgroundLayout
        ) {
            parentFragment?.apply {
                ZHTools.swapFragmentWithAnim(
                    this,
                    CustomBackgroundFragment::class.java,
                    CustomBackgroundFragment.TAG,
                    null
                )
            }
        }

        SwitchSettingsWrapper(
            context,
            AllSettings.animation,
            binding.animationLayout,
            binding.animation
        )

        SeekBarSettingsWrapper(
            context,
            AllSettings.animationSpeed,
            binding.animationSpeedLayout,
            binding.animationSpeedTitle,
            binding.animationSpeedSummary,
            binding.animationSpeedValue,
            binding.animationSpeed,
            "ms"
        )

        SeekBarSettingsWrapper(
            context,
            AllSettings.pageOpacity,
            binding.pageOpacityLayout,
            binding.pageOpacityTitle,
            binding.pageOpacitySummary,
            binding.pageOpacityValue,
            binding.pageOpacity,
            "%"
        ).setOnSeekBarProgressChangeListener {
            EventBus.getDefault().post(PageOpacityChangeEvent(it))
        }

        SwitchSettingsWrapper(
            context,
            AllSettings.enableLogOutput,
            binding.enableLogOutputLayout,
            binding.enableLogOutput
        )

        SwitchSettingsWrapper(
            context,
            AllSettings.quitLauncher,
            binding.quitLauncherLayout,
            binding.quitLauncher
        )

        BaseSettingsWrapper(
            context,
            binding.cleanUpCacheLayout
        ) {
            start(context)
        }

        BaseSettingsWrapper(
            context,
            binding.checkUpdateLayout
        ) {
            android.widget.Toast.makeText(context, "Update checking is disabled", android.widget.Toast.LENGTH_SHORT).show()
        }

        BaseSettingsWrapper(
            context,
            binding.curseforgeApiKeyLayout
        ) {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_text, null)
            val titleView = dialogView.findViewById<android.widget.TextView>(R.id.title_view)
            val messageView = dialogView.findViewById<android.widget.TextView>(R.id.message_view)
            val textEdit = dialogView.findViewById<android.widget.EditText>(R.id.text_edit)
            val cancelButton = dialogView.findViewById<android.widget.Button>(R.id.cancel_button)
            val confirmButton = dialogView.findViewById<android.widget.Button>(R.id.confirm_button)
            
            titleView.text = getString(R.string.curseforge_api_key_title)
            messageView.visibility = View.VISIBLE
            messageView.text = getString(R.string.curseforge_api_key_desc)
            textEdit.hint = getString(R.string.curseforge_api_key_hint)
            textEdit.setText(com.movtery.zalithlauncher.InfoDistributor.CURSEFORGE_API_KEY)
            
            val dialog = android.app.AlertDialog.Builder(context)
                .setView(dialogView)
                .create()
            
            cancelButton.setOnClickListener { dialog.dismiss() }
            confirmButton.setOnClickListener {
                val key = textEdit.text.toString()
                dialog.dismiss()
            }
            
            dialog.show()
        }

        val notificationPermissionRequest = SwitchSettingsWrapper(
            context,
            AllSettings.notificationPermissionRequest,
            binding.notificationPermissionRequestLayout,
            binding.notificationPermissionRequest
        )
        setupNotificationRequestPreference(notificationPermissionRequest)
    }

    override fun slideIn(animPlayer: AnimPlayer) {
        animPlayer.apply(AnimPlayer.Entry(binding.root, Animations.BounceInDown))
    }

    private fun setupNotificationRequestPreference(notificationPermissionRequest: SwitchSettingsWrapper) {
        val activity = requireActivity()
        if (activity is LauncherActivity) {
            if (ZHTools.checkForNotificationPermission()) notificationPermissionRequest.setGone()
            notificationPermissionRequest.switchView.setOnCheckedChangeListener { _, _ ->
                activity.askForNotificationPermission {
                    notificationPermissionRequest.mainView.visibility = View.GONE
                }
            }
        } else {
            notificationPermissionRequest.mainView.visibility = View.GONE
        }
    }
}
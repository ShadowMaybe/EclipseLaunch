package me.shadow.eclipselaunch.ui.fragment.download.addon

import me.shadow.eclipselaunch.R
import me.shadow.eclipselaunch.feature.mod.modloader.FabricLikeUtils

class DownloadQuiltFragment : DownloadFabricLikeFragment(FabricLikeUtils.QUILT_UTILS, R.drawable.ic_quilt) {
    companion object {
        const val TAG: String = "DownloadQuiltFragment"
    }
}
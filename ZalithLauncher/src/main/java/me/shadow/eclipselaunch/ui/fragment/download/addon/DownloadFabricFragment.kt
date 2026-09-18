package me.shadow.eclipselaunch.ui.fragment.download.addon

import me.shadow.eclipselaunch.R
import me.shadow.eclipselaunch.feature.mod.modloader.FabricLikeUtils

class DownloadFabricFragment : DownloadFabricLikeFragment(FabricLikeUtils.FABRIC_UTILS, R.drawable.ic_fabric) {
    companion object {
        const val TAG: String = "DownloadFabricFragment"
    }
}
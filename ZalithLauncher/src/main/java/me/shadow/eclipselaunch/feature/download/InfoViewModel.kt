package me.shadow.eclipselaunch.feature.download

import androidx.lifecycle.ViewModel
import me.shadow.eclipselaunch.feature.download.item.InfoItem
import me.shadow.eclipselaunch.feature.download.platform.AbstractPlatformHelper

class InfoViewModel : ViewModel() {
    var platformHelper: AbstractPlatformHelper? = null
    var infoItem: InfoItem? = null
}
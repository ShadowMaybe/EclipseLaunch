package me.shadow.eclipselaunch.feature.unpack

import android.content.Context
import me.shadow.eclipselaunch.feature.log.Logging.e
import me.shadow.eclipselaunch.utils.CopyDefaultFromAssets.Companion.copyFromAssets
import me.shadow.eclipselaunch.utils.path.PathManager
import net.kdt.pojavlaunch.Tools

class UnpackSingleFilesTask(val context: Context) : AbstractUnpackTask() {
    override fun isNeedUnpack(): Boolean = true

    override fun run() {
        runCatching {
            copyFromAssets(context)
            Tools.copyAssetFile(context, "resolv.conf", PathManager.DIR_DATA, false)
        }.getOrElse { e("AsyncAssetManager", "Failed to unpack critical components !") }
    }
}
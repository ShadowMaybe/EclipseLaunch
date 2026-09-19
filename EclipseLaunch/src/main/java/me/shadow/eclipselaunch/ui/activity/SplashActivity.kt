package me.shadow.eclipselaunch.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.compose.runtime.mutableIntStateOf
import me.shadow.eclipselaunch.InfoCenter
import me.shadow.eclipselaunch.InfoDistributor
import me.shadow.eclipselaunch.R
import me.shadow.eclipselaunch.feature.unpack.Components
import me.shadow.eclipselaunch.feature.unpack.Jre
import me.shadow.eclipselaunch.feature.unpack.OnTaskRunningListener
import me.shadow.eclipselaunch.feature.unpack.UnpackComponentsTask
import me.shadow.eclipselaunch.feature.unpack.UnpackJreTask
import me.shadow.eclipselaunch.feature.unpack.UnpackSingleFilesTask
import me.shadow.eclipselaunch.task.Task
import me.shadow.eclipselaunch.ui.compose.EclipseMiuixTheme
import me.shadow.eclipselaunch.ui.compose.SplashScreen
import me.shadow.eclipselaunch.ui.dialog.TipDialog
import me.shadow.eclipselaunch.utils.StoragePermissionsUtils
import net.kdt.pojavlaunch.LauncherActivity
import net.kdt.pojavlaunch.MissingStorageActivity
import net.kdt.pojavlaunch.Tools
import java.util.concurrent.atomic.AtomicInteger

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    private var isStarted: Boolean = false
    private val items: MutableList<InstallableItem> = ArrayList()
    private var completedTasksCount = AtomicInteger(0)
    // Counter to force Compose recomposition when items change
    private val recomposeCounter = mutableIntStateOf(0)

    private lateinit var installText: String
    private lateinit var startText: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initItems()
        installText = getString(R.string.splash_screen_installing)
        startText = getString(R.string.splash_screen_apply)

        setContent {
            EclipseMiuixTheme {
                // Read recomposeCounter to trigger recomposition
                @Suppress("UNUSED_EXPRESSION")
                recomposeCounter.intValue

                SplashScreen(
                    title = InfoDistributor.APP_NAME,
                    statusText = if (isStarted) installText else startText,
                    items = items,
                    startEnabled = !isStarted,
                    onStartClick = {
                        if (isStarted) return@SplashScreen
                        isStarted = true
                        recomposeCounter.intValue++ // trigger recomposition to disable button
                        startAllTasks()
                    }
                )
            }
        }

        if (!Tools.checkStorageRoot()) {
            startActivity(Intent(this, MissingStorageActivity::class.java))
            finish()
            return
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P && !StoragePermissionsUtils.hasStoragePermissions(this)) {
            TipDialog.Builder(this)
                .setTitle(R.string.generic_warning)
                .setMessage(InfoCenter.replaceName(this, R.string.permissions_write_external_storage))
                .setWarning()
                .setConfirmClickListener { requestStoragePermissions() }
                .setCancelClickListener { checkEnd() }
                .showDialog()
        } else {
            checkEnd()
        }
    }

    private fun requestStoragePermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            STORAGE_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            checkEnd()
        }
    }

    private fun initItems() {
        Components.entries.forEach {
            val unpackComponentsTask = UnpackComponentsTask(this, it)
            if (!unpackComponentsTask.isCheckFailed()) {
                items.add(
                    InstallableItem(
                        it.displayName,
                        it.summary?.let { it1 -> getString(it1) },
                        unpackComponentsTask
                    )
                )
            }
        }
        Jre.entries.forEach {
            val unpackJreTask = UnpackJreTask(this, it)
            if (!unpackJreTask.isCheckFailed()) {
                items.add(
                    InstallableItem(
                        it.jreName,
                        getString(it.summary),
                        unpackJreTask
                    )
                )
            }
        }
        items.sort()
    }

    private fun checkEnd() {
        items.forEachIndexed { index, item ->
            if (!item.task.isNeedUnpack()) {
                item.isFinished = true
                updateTaskCount(index)
            }
        }
        Task.runTask {
            UnpackSingleFilesTask(this).run()
        }.execute()
    }

    private fun startAllTasks() {
        items.forEachIndexed { index, item ->
            if (!item.isFinished) {
                Thread {
                    item.task.setTaskRunningListener(object : OnTaskRunningListener {
                        override fun onTaskStart() {
                            item.isRunning = true
                            runOnUiThread { recomposeCounter.intValue++ }
                        }

                        override fun onTaskEnd() {
                            item.isRunning = false
                            item.isFinished = true
                            updateTaskCount(index)
                        }
                    })
                    item.task.run()
                }.start()
            }
        }
    }

    private fun updateTaskCount(index: Int) {
        completedTasksCount.incrementAndGet()
        runOnUiThread {
            recomposeCounter.intValue++
        }
        if (completedTasksCount.get() >= items.size) {
            runOnUiThread {
                toMain()
            }
        }
    }

    private fun toMain() {
        startActivity(Intent(this, LauncherActivity::class.java))
        finish()
    }

    companion object {
        private const val STORAGE_PERMISSION_REQUEST_CODE: Int = 100
    }
}

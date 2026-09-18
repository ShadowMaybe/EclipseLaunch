package me.shadow.eclipselaunch.feature.download.enums

import me.shadow.eclipselaunch.feature.download.platform.AbstractPlatformHelper
import me.shadow.eclipselaunch.feature.download.platform.curseforge.CurseForgeHelper
import me.shadow.eclipselaunch.feature.download.platform.modrinth.ModrinthHelper

enum class Platform(val pName: String, val helper: AbstractPlatformHelper) {
    MODRINTH("Modrinth", ModrinthHelper()),
    CURSEFORGE("CurseForge", CurseForgeHelper())
}
package me.shadow.eclipselaunch.setting.unit

import me.shadow.eclipselaunch.setting.Settings.Manager

class IntSettingUnit(key: String, defaultValue: Int) : AbstractSettingUnit<Int>(key, defaultValue) {
    override fun getValue() = Manager.getValue(key, defaultValue) { it.toIntOrNull() }
}
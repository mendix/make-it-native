package com.mendix.developerapp.sampelapps

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

class SampleAppsProvider(private val config: SampleAppsConfig) {
    val appsCount: Int get() = config.apps.size

    fun getApp(index: Int): SampleApp {
        return config.apps[index]
    }

    fun getApp(id: String): SampleApp? {
        return config.apps.find {
            it.id == id
        }
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class SampleAppsConfig {
    val version = 1
    val apps: Array<SampleApp> = arrayOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SampleAppsConfig

        if (!apps.contentEquals(other.apps)) return false

        return true
    }

    override fun hashCode(): Int {
        return apps.contentHashCode()
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class SampleApp {
    var id: String = ""
    var name: String = ""
    var description: String = ""
    var runtimeUrl: String = ""

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SampleApp

        if (id != other.id) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (runtimeUrl != other.runtimeUrl) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + runtimeUrl.hashCode()
        return result
    }
}

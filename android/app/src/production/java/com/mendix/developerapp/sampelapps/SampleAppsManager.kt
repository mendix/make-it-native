package com.mendix.developerapp.sampelapps

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.fasterxml.jackson.databind.ObjectMapper
import com.mendix.mendixnative.react.fs.FileBackend
import java.io.File

const val sampleAppsDirectory = "SampleApps"
const val sampleAppsConfigFileName = "sample_apps.json"
const val sampleAppsZipFileName = "sampleapps.zip"

class SampleAppViewInfo(val id: String, val name: String, val description: String, val splashFilePath: String)

class SampleAppsManager(context: Context, documentsPath: String) {
    val initialized = MutableLiveData(false)
    private val fileBackend = FileBackend(context)

    private var sampleAppsProvider: SampleAppsProvider
    val apps get() = this.sampleAppsProvider.apps

    private val samplesAppsDirectory: String = File(documentsPath, sampleAppsDirectory).absolutePath

    init {
        val newSampleAppsConfigFile = readNewSampleAppsConfigFile(context)
        val installedSampleAppsConfigFile = readInstalledSampleAppsConfigFile()

        val configChanged = newSampleAppsConfigFile.version != installedSampleAppsConfigFile.version || newSampleAppsConfigFile.apps.size != installedSampleAppsConfigFile.apps.size || newSampleAppsConfigFile.apps.any {
            installedSampleAppsConfigFile.apps.find { localApp -> localApp == it } == null
        }
        val isMissingProjects = newSampleAppsConfigFile.apps.any {
            !File(samplesAppsDirectory, it.id).exists()
        }
        if (hasSampleAppsZip(context) && (configChanged || isMissingProjects)) {
            unpackSampleProjects(context)
        }

        val localConfigFile = readInstalledSampleAppsConfigFile()
        sampleAppsProvider = SampleAppsProvider(localConfigFile)

        initialized.value = true
    }

    fun selectProject(id: String) {
        sampleAppJSBundlePath = resolveMendixBundleFilepath(id)
        appName = sampleAppsProvider.getApp(id)?.name
    }

    fun getSampleAppInfo(index: Int): SampleAppViewInfo {
        sampleAppsProvider.getApp(index).let {
            return SampleAppViewInfo(it.id, it.name, it.description, resolvePreviewImagePath(it.id))
        }
    }

    private fun resolvePreviewImagePath(appId: String): String {
        return File(samplesAppsDirectory, "$appId${File.separator}splash.png").absolutePath
    }

    private fun unpackSampleProjects(context: Context) {
        copySampleZipToDocuments(context)
        unzipSampleApps()
        copySamplesAppsConfig(context)
        cleanAfter()
    }

    private fun hasSampleAppsZip(context: Context): Boolean {
        return try {
            context.assets.open(sampleAppsZipFileName)
            true
        } catch (exception: Exception) {
            false
        }
    }

    private fun unzipSampleApps() {
        fileBackend.unzip(samplesAppsDirectory + File.separator + sampleAppsZipFileName, samplesAppsDirectory)
    }


    private fun copySampleZipToDocuments(context: Context) {
        fileBackend.copyAssetToPath(context, sampleAppsZipFileName, samplesAppsDirectory + File.separator + sampleAppsZipFileName)
    }

    private fun copySamplesAppsConfig(context: Context) {
        fileBackend.copyAssetToPath(context, sampleAppsConfigFileName, samplesAppsDirectory + File.separator + sampleAppsConfigFileName)
    }

    private fun cleanAfter() {
        File(samplesAppsDirectory, sampleAppsZipFileName).delete()
    }

    private fun readNewSampleAppsConfigFile(context: Context): SampleAppsConfig {
        return try {
            val remoteSampleAppsConfigFile = context.assets.open(sampleAppsConfigFileName).bufferedReader().use {
                it.readText()
            }
            ObjectMapper().readValue(remoteSampleAppsConfigFile, SampleAppsConfig::class.java)
        } catch (e: java.lang.Exception) {
            SampleAppsConfig()
        }
    }

    private fun readInstalledSampleAppsConfigFile(): SampleAppsConfig {
        val localSampleAppsConfigFile = File(samplesAppsDirectory, sampleAppsConfigFileName)
        if (!localSampleAppsConfigFile.exists())
            return SampleAppsConfig()
        return ObjectMapper().readValue(localSampleAppsConfigFile, SampleAppsConfig::class.java)
    }

    private fun resolveMendixBundleFilepath(id: String): String {
        return File(samplesAppsDirectory, "${id}/assets/index.android.bundle").absolutePath
    }

    companion object {
        var sampleAppJSBundlePath: String? = null
        var appName: String? = null

        fun resetSelectedProject() {
            sampleAppJSBundlePath = null
            appName = null
        }
    }
}

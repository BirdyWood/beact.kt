package org.bw.beact

object VersionUtil {
    val applicationVersion: String
        get() {
            val version = VersionUtil::class.java.getPackage().implementationVersion
            return version ?: "unable to reach"
        }
}
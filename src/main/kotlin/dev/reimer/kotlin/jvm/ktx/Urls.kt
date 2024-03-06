package dev.reimer.kotlin.jvm.ktx

import java.net.URL
import java.net.URI

fun String.toURL() = URI(this).toURL()

fun URL.copy(
    protocol: String = this.protocol,
    host: String = this.host,
    port: Int = this.port,
    file: String = this.file
): URL {
    return URI(protocol, "$host:$port", file).toURL()
}
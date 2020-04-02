package dev.reimer.kotlin.jvm.ktx

import java.net.URL

fun String.toURL() = URL(this)

fun URL.copy(
    protocol: String = this.protocol,
    host: String = this.host,
    port: Int = this.port,
    file: String = this.file
): URL {
    return URL(protocol, host, port, file)
}
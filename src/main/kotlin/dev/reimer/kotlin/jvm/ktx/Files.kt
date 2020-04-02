package dev.reimer.kotlin.jvm.ktx

import java.io.BufferedWriter
import java.io.File
import java.nio.charset.Charset

fun <R> useTempFile(
    prefix: String = "tmp",
    suffix: String? = null,
    directory: File? = null,
    block: (File) -> R
): R = createTempFile(prefix, suffix, directory).let { model ->
    val result = block(model)
    model.delete()
    result
}

fun Sequence<String>.writeLinesTo(bufferedWriter: BufferedWriter) {
    bufferedWriter.use { writer ->
        forEach { line ->
            writer.write(line)
            writer.newLine()
        }
    }
}

fun Sequence<String>.writeLinesTo(file: File, charset: Charset = Charsets.UTF_8) =
    writeLinesTo(file.bufferedWriter(charset))

fun File.prepareNewFile() {
    parentFile.mkdirs()
    delete()
    createNewFile()
}

val File.totalLines
    get() =
        useLines { lines -> lines.count() }
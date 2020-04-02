package dev.reimer.kotlin.jvm.ktx

import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.util.zip.*

private const val defaultBufferSize = 512
private val defaultDeflater = Deflater()
private val defaultInflater = Inflater()
private const val defaultFileMode = ZipFile.OPEN_READ
private val defaultCharset: Charset = Charsets.UTF_8

fun InputStream.checked(checksum: Checksum) = CheckedInputStream(this, checksum)

fun OutputStream.checked(checksum: Checksum) = CheckedOutputStream(this, checksum)

fun InputStream.deflated(
    deflater: Deflater = defaultDeflater,
    bufferSize: Int = defaultBufferSize
): DeflaterInputStream {
    return if (deflater === defaultDeflater && bufferSize == defaultBufferSize) {
        DeflaterInputStream(this)
    } else DeflaterInputStream(this, deflater, bufferSize)
}

fun OutputStream.deflated(
    deflater: Deflater = defaultDeflater,
    bufferSize: Int = defaultBufferSize,
    syncFlush: Boolean = false
): DeflaterOutputStream {
    return if (deflater === defaultDeflater && bufferSize == defaultBufferSize) {
        DeflaterOutputStream(this, syncFlush)
    } else DeflaterOutputStream(this, deflater, bufferSize, syncFlush)
}

fun InputStream.gzipped(bufferSize: Int = defaultBufferSize) = GZIPInputStream(this, bufferSize)

fun OutputStream.gzipped(
    bufferSize: Int = defaultBufferSize,
    syncFlush: Boolean = false
) = GZIPOutputStream(this, bufferSize, syncFlush)

fun InputStream.inflated(
    inflater: Inflater = defaultInflater,
    bufferSize: Int = defaultBufferSize
): InflaterInputStream {
    return if (inflater === defaultInflater && bufferSize == defaultBufferSize) {
        InflaterInputStream(this)
    } else InflaterInputStream(this, inflater, bufferSize)
}

fun OutputStream.inflated(
    inflater: Inflater = defaultInflater,
    bufferSize: Int = defaultBufferSize
): InflaterOutputStream {
    return if (inflater === defaultInflater && bufferSize == defaultBufferSize) {
        InflaterOutputStream(this)
    } else InflaterOutputStream(this, inflater, bufferSize)
}

fun File.zipped(
    mode: Int = defaultFileMode,
    charset: Charset = defaultCharset
) = ZipFile(this, mode, charset)

fun InputStream.zipped(charset: Charset = defaultCharset) = ZipInputStream(this, charset)

fun OutputStream.zipped(charset: Charset = defaultCharset) = ZipOutputStream(this, charset)

operator fun ZipInputStream.iterator(): Iterator<ZipEntry> {
    return object : Iterator<ZipEntry> {
        lateinit var entry: ZipEntry

        override fun hasNext(): Boolean {
            entry = nextEntry ?: return false
            return true
        }

        override fun next() = entry
    }
}

fun ZipOutputStream.putNextEntry(entry: ZipEntry, block: OutputStream.() -> Unit) {
    putNextEntry(entry)
    try {
        block()
    } finally {
        closeEntry()
    }
}

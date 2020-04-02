package dev.reimer.kotlin.jvm.ktx

fun String.stripSubDomain(): String = substringAfter(delimiter = '.', missingDelimiterValue = "")

fun String.stripSubDomains(): String {
    if (containsAtLeastTwice('.')) {
        return stripSubDomain().stripSubDomains()
    }
    return this
}

fun String.containsAtLeastTwice(char: Char) = indexOf(char) != lastIndexOf(char)

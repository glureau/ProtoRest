package com.glureau.geno.utils

import java.io.FileReader
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

/**
 * Created by Greg on 22/10/2017.
 */
object ImportInjection {
    private fun insertImportsAfterPackage(kotlinFileContent: String, imports: List<String>): String {
        val result = ArrayList<String>()
        for (s in kotlinFileContent.split("\n".toRegex()).toTypedArray()) {
            result.add(s)
            if (s.startsWith("package ")) {
                result.add("")
                imports.mapTo(result) { "import $it;" }
            }
        }
        return result.joinToString("\n")
    }

    fun injectImport(filePath: String, imports: List<String>) {
        val reader = FileReader(filePath)
        val fileContent = insertImportsAfterPackage(reader.readText(), imports)
        Files.write(Paths.get(filePath), fileContent.toByteArray(), StandardOpenOption.TRUNCATE_EXISTING)
    }

    fun injectImport(filePath: String, import: String) {
        injectImport(filePath, listOf(import))
    }

}
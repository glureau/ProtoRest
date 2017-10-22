package com.glureau.geno

/**
 * Created by Greg on 22/10/2017.
 */
object ImportInjection {
    fun injectImports(kotlinFileContent: String, imports: List<String>): String {
        val result = ArrayList<String>()
        for (s in kotlinFileContent.split("\n".toRegex()).toTypedArray()) {
            result.add(s)
            if (s.startsWith("package ")) {
                result.add("")
                for (i in imports) {
                    result.add("import $i;")
                }
            }
        }
        return result.joinToString("\n")
    }

    fun injectImport(kotlinFileContent: String, import: String): String {
        return injectImports(kotlinFileContent, listOf(import))
    }

}
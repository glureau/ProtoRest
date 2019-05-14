package com.glureau.geno.scan

import com.glureau.geno.scan.data.DataClass
import com.glureau.geno.scan.data.DataClassScanner
import javax.annotation.processing.Messager
import javax.annotation.processing.RoundEnvironment

data class ScanResult(val classes: List<DataClass>)

object Scanner {
    fun scan(roundEnv: RoundEnvironment, messager: Messager): ScanResult {
        val classes = DataClassScanner.scan(roundEnv, messager)
        return ScanResult(classes)
    }
}
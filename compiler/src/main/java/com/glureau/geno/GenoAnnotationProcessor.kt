package com.glureau.geno

import com.glureau.geno.annotation.*
import com.glureau.geno.generators.data.DaoGenerator
import com.glureau.geno.generators.data.EntityGenerator
import com.glureau.geno.generators.view.BindingHolderGenerator
import com.glureau.geno.generators.view.BindingRecyclerViewAdapterGenerator
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.asClassName
import org.w3c.dom.Document
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Created by Greg on 21/10/2017.
 */
@AutoService(Processor::class) // Some issues with META-INF generation, the META-INF is duplicated in the project to avoid that...
@SupportedSourceVersion(SourceVersion.RELEASE_6) // Generate Warnings, not sure it's very helpful :/
class GenoAnnotationProcessor : AbstractProcessor() {

    private lateinit var messager: Messager
    private lateinit var filer: Filer

    private lateinit var bindingHolderGenerator: BindingHolderGenerator
    private lateinit var bindingRecyclerViewAdapterGenerator: BindingRecyclerViewAdapterGenerator
    private lateinit var entityGenerator: EntityGenerator
    private lateinit var daoGenerator: DaoGenerator

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        messager = processingEnv.messager
        filer = processingEnv.filer

        bindingHolderGenerator = BindingHolderGenerator(messager)
        bindingRecyclerViewAdapterGenerator = BindingRecyclerViewAdapterGenerator(messager)
        entityGenerator = EntityGenerator(messager)
        daoGenerator = DaoGenerator(messager)
    }

    companion object {
        private val SUPPORTED_ANNOTATIONS = arrayOf(
                CustomView::class.java,
                Endpoint::class.java,
                EndpointParam::class.java,
                Image::class.java,
                RestError::class.java)
                .map { it.canonicalName }.toSet()
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        return SUPPORTED_ANNOTATIONS
    }

    override fun process(annotations: Set<TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        generateViews(roundEnv)
        return false
    }

    private fun generateViews(roundEnv: RoundEnvironment) {
        val elements = roundEnv.getElementsAnnotatedWith(CustomView::class.java)
        val outputDir = processingEnv.options["kapt.kotlin.generated"]
        for (it in elements) {
            if (it.kind != ElementKind.CLASS) {
                messager.printMessage(Diagnostic.Kind.WARNING, "@CustomView should be use on class, skipping generation", it)
                continue
            }

            if (useAndroidBinding(it as TypeElement)) {
                bindingHolderGenerator.generate(it, xmlCustomLayout(it).second, outputDir)
                bindingRecyclerViewAdapterGenerator.generate(it, outputDir)
                entityGenerator.generate(it, outputDir)
                daoGenerator.generate(it, outputDir)
            }
        }
    }

    private fun useAndroidBinding(element: TypeElement): Boolean {
        val className = element.asClassName()
        val (xmlLayoutFile, xmlDoc) = xmlCustomLayout(element)
        if (xmlDoc.documentElement.nodeName != "layout") {
            messager.printMessage(Diagnostic.Kind.WARNING, "$className is not using Android Data Binding ($xmlLayoutFile should start with 'layout' but starts with '${xmlDoc.documentElement.nodeName}'")
            return false
        }
        return true
    }

    private fun xmlCustomLayout(element: TypeElement): Pair<File, Document> {
        val viewName = element.getAnnotation(CustomView::class.java).viewName
        val xmlLayoutFile = File("compiler-test/src/main/res/layout/$viewName.xml")
        val xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlLayoutFile)
        return Pair(xmlLayoutFile, xmlDoc)
    }
}


package com.glureau.geno

import com.glureau.geno.annotation.Endpoint
import com.glureau.geno.annotation.EndpointParam
import com.glureau.geno.annotation.Image
import com.glureau.geno.annotation.RestError
import com.glureau.geno.annotation.data.ManyToMany
import com.glureau.geno.annotation.network.RestApi
import com.glureau.geno.annotation.storage.ViewModel
import com.glureau.geno.annotation.view.CustomView
import com.glureau.geno.generators.data.DaoGenerator
import com.glureau.geno.generators.data.DatabaseGenerator
import com.glureau.geno.generators.data.EntityGenerator
import com.glureau.geno.generators.data.ManyToManyEntityGenerator
import com.glureau.geno.generators.view.RecyclerViewAdapterGenerator
import com.glureau.geno.generators.view.ViewHolderGenerator
import com.glureau.geno.scan.Scanner
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import org.w3c.dom.Document
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
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

    private lateinit var viewHolderGenerator: ViewHolderGenerator
    private lateinit var recyclerViewAdapterGenerator: RecyclerViewAdapterGenerator
    private lateinit var entityGenerator: EntityGenerator
    private lateinit var daoGenerator: DaoGenerator
    private lateinit var databaseGenerator: DatabaseGenerator
    private lateinit var manyToManyEntityGenerator: ManyToManyEntityGenerator

    private val generatedClassesInfos = mutableMapOf<ClassName, GeneratedClassesInfo>()

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        messager = processingEnv.messager
        filer = processingEnv.filer

        viewHolderGenerator = ViewHolderGenerator(messager)
        recyclerViewAdapterGenerator = RecyclerViewAdapterGenerator(messager)
        entityGenerator = EntityGenerator(messager)
        daoGenerator = DaoGenerator(messager)
        databaseGenerator = DatabaseGenerator(messager)
        manyToManyEntityGenerator = ManyToManyEntityGenerator(messager)
    }

    companion object {
        private val SUPPORTED_ANNOTATIONS = arrayOf(
                CustomView::class.java,
                RestApi::class.java,
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
        return generateClasses(roundEnv)
    }


    private fun generateClasses(roundEnv: RoundEnvironment): Boolean {
        val scanResult = Scanner.scan(roundEnv, messager)
        val customViews = roundEnv.getElementsAnnotatedWith(CustomView::class.java) as Set<TypeElement>?
                ?: return false // Null : nothing to process
        val viewModels = roundEnv.getElementsAnnotatedWith(ViewModel::class.java) as Set<TypeElement>?
                ?: return false // Null : nothing to process
        val restApis = roundEnv.getElementsAnnotatedWith(RestApi::class.java) as Set<TypeElement>?
                ?: return false // Null : nothing to process
        val relationships = roundEnv.getElementsAnnotatedWith(ManyToMany::class.java) as Set<TypeElement>?
                ?: return false // Null : nothing to process
        val outputDir = processingEnv.options["kapt.kotlin.generated"]

        for (it in customViews) {
            if (useAndroidBinding(it, outputDir)) {
                val info = info(it)
                viewHolderGenerator.generate(it, xmlCustomLayout(it, outputDir).second, outputDir, info)
                recyclerViewAdapterGenerator.generate(it, outputDir, info)
            }
        }

        for (it in viewModels) {
            val info = info(it)
            entityGenerator.generate(it, outputDir, info)
            daoGenerator.generate(it, outputDir, info, relationships)
        }

        relationships.forEach {
            val info = info(it)
            manyToManyEntityGenerator.generate(it, outputDir, info)
        }
        databaseGenerator.generate(generatedClassesInfos, outputDir)
        return true
    }

    private fun info(elem: TypeElement): GeneratedClassesInfo {
        var info = generatedClassesInfos[elem.asClassName()]
        if (info == null) {
            info = GeneratedClassesInfo()
            generatedClassesInfos[elem.asClassName()] = info
        }
        return info
    }

    private fun useAndroidBinding(element: TypeElement, outputDir: String?): Boolean {
        val className = element.asClassName()
        val (xmlLayoutFile, xmlDoc) = xmlCustomLayout(element, outputDir)
        if (xmlDoc.documentElement.nodeName != "layout") {
            messager.printMessage(Diagnostic.Kind.WARNING, "$className is not using Android Data Binding ($xmlLayoutFile should start with 'layout' but starts with '${xmlDoc.documentElement.nodeName}'")
            return false
        }
        return true
    }

    private fun xmlCustomLayout(element: TypeElement, outputDir: String?): Pair<File, Document> {
        val viewName = element.getAnnotation(CustomView::class.java).viewName
        System.out.println("xmlCustomLayout")
        System.out.println(outputDir)
        val xmlLayoutFile = File("${outputDir?.substringBefore("compiler-test")}/compiler-test/src/main/res/layout/$viewName.xml")
        val xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlLayoutFile)
        return Pair(xmlLayoutFile, xmlDoc)
    }
}


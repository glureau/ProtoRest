package com.glureau.geno

import com.glureau.geno.AnnotationHelper.getAnnotationClassValue
import com.glureau.geno.annotation.*
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import java.io.File
import java.io.FileReader
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.tools.Diagnostic

/**
 * Created by Greg on 21/10/2017.
 */
@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_6) // Generate Warnings, not sure it's very helpful :/
class GenoAnnotationProcessor : AbstractProcessor() {

    private lateinit var messager: Messager
    private lateinit var filer: Filer

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        messager = processingEnv.messager
        filer = processingEnv.filer
    }

    companion object {
        private val SUPPORTED_ANNOTATIONS = arrayOf(
                CustomView::class.java,
                Endpoint::class.java,
                EndpointParam::class.java,
                Image::class.java,
                RestError::class.java)
                .map { it.canonicalName }.toSet()

        private val ANDROID_VIEW = ClassName("android.view", "View")
        private val ANDROID_VIEW_GROUP = ClassName("android.view", "ViewGroup")
        private val ANDROID_ACTIVITY = ClassName("android.app", "Activity")
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        return SUPPORTED_ANNOTATIONS
    }

    override fun process(annotations: Set<TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        generateViews(roundEnv)

        processingEnv.messager.printMessage(Diagnostic.Kind.WARNING, "A little step for me...")
        return false
    }

    private fun generateViews(roundEnv: RoundEnvironment) {
        val elements = roundEnv.getElementsAnnotatedWith(CustomView::class.java)
        for (it in elements) {
            if (it.kind != ElementKind.CLASS) {
                messager.printMessage(Diagnostic.Kind.WARNING, "@CustomView should be use on class, skipping generation", it)
                continue
            }
            generateView(it as TypeElement)
        }
    }

    private fun generateView(element: TypeElement) {
        val className = element.asClassName()
        val simpleClassName = className.simpleName() + "ViewManager"
        val packageName = className.packageName()
        val instanceName = className.simpleName().decapitalize()

        val R = getAnnotationClassValue(element, CustomView::class, "R")
        val viewName = element.getAnnotation(CustomView::class.java).viewName
        val kaeImport = "kotlinx.android.synthetic.main.$viewName.view.*"

        val fields = element.enclosedElements.filter { it.kind == ElementKind.FIELD }.map { it as VariableElement }
        var fillBuilder = FunSpec.builder("fill")
                .addParameter("view", ANDROID_VIEW)
        fields.forEach { field ->
            val fieldName = field.simpleName.toString()
            val viewKae = "geno_$fieldName"
            fillBuilder = fillBuilder.beginControlFlow("if (view.${viewKae} != null)")
                    .addStatement("view.${viewKae}.text = githubUser.$fieldName")
                    .endControlFlow()
        }

        val file = FileSpec.builder(packageName, simpleClassName)
                .indent("    ")
                .addType(TypeSpec.classBuilder(simpleClassName)
                        .primaryConstructor(FunSpec.constructorBuilder()
                                .addParameter(instanceName, className)
                                .build())
                        .addProperty(PropertySpec.builder(instanceName, className)
                                .initializer(instanceName)
                                .build())
                        .addFunction(fillBuilder.build())
                        .companionObject(TypeSpec.companionObjectBuilder(simpleClassName)
                                .addFunction(FunSpec.builder("inflate")
                                        .addParameter("activity", ANDROID_ACTIVITY)
                                        .addParameter("root", ANDROID_VIEW_GROUP.asNullable())
                                        .addStatement("return activity.layoutInflater.inflate($R.layout.$viewName, root, false)")
                                        .returns(ANDROID_VIEW)
                                        .build())
                                .build())
                        .build())
                .build()

        val path = File("./compiler-test/build/generated/source/kapt/debug")
        messager.printMessage(Diagnostic.Kind.WARNING, "Writing class -----------------------------------------------------------------------------")
        file.writeTo(path)

        val reader = FileReader(path.absolutePath + "/" + file.packageName.replace(".", "/") + "/" + file.name + ".kt")
        val fileContent = ImportInjection.injectImport(reader.readText(), kaeImport)
        Files.write(Paths.get(path.absolutePath + "/" + file.packageName.replace(".", "/") + "/" + file.name + ".kt"), fileContent.toByteArray(), StandardOpenOption.TRUNCATE_EXISTING)

/*
        messager.printMessage(Diagnostic.Kind.WARNING, "XML -----------------------------------------------------------------------------")

        val resDir = File("./compiler-test/build/generated/resource/kapt/debug")
        resDir.mkdirs()
        val pathXml = File(resDir.path + "/$instanceName.xml")
        val writer = FileWriter(pathXml)
        writer.write("""
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools">

    <data>

        <variable
            name="githubUser"
            type="com.glureau.geno.test.GithubUser" />

    </data>
    <LinearLayout
        android:layout_width="wrap_content"
        android:layout_height="wrap_content">

        <TextView
            android:id="@+id/githubUserLogin"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@{githubUser.login}" />

        <TextView
            android:id="@+id/githubUserName"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@{githubUser.name}" />

        <ImageView
            android:id="@+id/githubUserAvatar"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            geno:imageURI="@{githubUser.avatar}" />
    </LinearLayout>
</layout>""")
        writer.close()
        messager.printMessage(Diagnostic.Kind.ERROR, "Writing XML in $pathXml")
//        OutputStreamWriter(Files.newOutputStream(outputPath), StandardCharsets.UTF_8).use { writer -> writeTo(writer) }
*/
    }
}

private fun FunSpec.Builder.addStatements(statements: MutableList<String>): FunSpec.Builder {
    statements.forEach { addStatement(it) }
    return this
}

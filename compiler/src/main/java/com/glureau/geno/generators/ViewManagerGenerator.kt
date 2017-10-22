package com.glureau.geno.generators


import com.glureau.geno.utils.AnnotationHelper.getAnnotationClassValue
import com.glureau.geno.annotation.CustomView
import com.glureau.geno.utils.AndroidClasses
import com.glureau.geno.utils.ImportInjection
import com.squareup.kotlinpoet.*
import java.io.File
import java.io.FileReader
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import javax.annotation.processing.Messager
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.tools.Diagnostic

/**
 * Created by Greg on 22/10/2017.
 */
class ViewManagerGenerator(val messager: Messager) {

    fun generateView(element: TypeElement) {
        val className = element.asClassName()
        val simpleClassName = className.simpleName() + "ViewManager"
        val packageName = className.packageName()
        val instanceName = className.simpleName().decapitalize()

        val R = getAnnotationClassValue(element, CustomView::class, "R")
        val viewName = element.getAnnotation(CustomView::class.java).viewName
        val kaeImport = "kotlinx.android.synthetic.main.$viewName.view.*"

        val fields = element.enclosedElements.filter { it.kind == ElementKind.FIELD }.map { it as VariableElement }
        var fillBuilder = FunSpec.builder("fill")
                .addParameter("view", AndroidClasses.ANDROID_VIEW)
        fields.forEach { field ->
            val fieldName = field.simpleName.toString()
            val viewKae = "geno_$fieldName"
            fillBuilder = fillBuilder.beginControlFlow("if (view.$viewKae != null)")
                    .addStatement("view.$viewKae.text = githubUser.$fieldName")
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
                                        .addParameter("activity", AndroidClasses.ANDROID_ACTIVITY)
                                        .addParameter("root", AndroidClasses.ANDROID_VIEW_GROUP.asNullable())
                                        .addStatement("return activity.layoutInflater.inflate($R.layout.$viewName, root, false)")
                                        .returns(AndroidClasses.ANDROID_VIEW)
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
    }
}
package com.glureau.geno

import com.glureau.geno.annotation.*
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

/**
 * Created by Greg on 21/10/2017.
 */
@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_6) // Generate Warnings, not sure it's very helpful :/
class GenoAnnotationProcessor : AbstractProcessor() {

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
        val elements = roundEnv.getElementsAnnotatedWith(CustomView::class.java)

        val greeterClass = ClassName("com.glureau.compiler.test", "Greeter")
        val file = FileSpec.builder("com.glureau.compiler.test", "HelloWorld")
                .addType(TypeSpec.classBuilder("Greeter")
                        .primaryConstructor(FunSpec.constructorBuilder()
                                .addParameter("name", String::class)
                                .build())
                        .addProperty(PropertySpec.builder("name", String::class)
                                .initializer("name")
                                .build())
                        .addFunction(FunSpec.builder("greet")
                                .addStatement("return %S", "Hello, \$name")
                                .returns(String::class)
                                .build())
                        .build())
                .addFunction(FunSpec.builder("main")
                        .addParameter("args", String::class, KModifier.VARARG)
                        .addStatement("%T(args[0]).greet()", greeterClass)
                        .build())
                .build()

        val path = File("./compiler-test/build/generated/source/kapt/debug")
        file.writeTo(path)

        processingEnv.messager.printMessage(Diagnostic.Kind.WARNING, "A little step for me...")
        return false
    }
}
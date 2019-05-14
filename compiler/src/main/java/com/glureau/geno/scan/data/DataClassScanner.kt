package com.glureau.geno.scan.data

import com.glureau.geno.annotation.data.ManyToMany
import com.glureau.geno.annotation.data.OneToMany
import com.glureau.geno.annotation.data.OneToOne
import com.glureau.geno.annotation.storage.ViewModel
import com.glureau.geno.utils.JavaToKotlinPrimitives
import com.glureau.geno.utils.KotlinNullable
import com.squareup.kotlinpoet.asClassName
import javax.annotation.processing.Messager
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.tools.Diagnostic

object DataClassScanner {
    fun scan(roundEnv: RoundEnvironment, messager: Messager): List<DataClass> {
        val viewModels = roundEnv.getElementsAnnotatedWith(ViewModel::class.java) as Set<TypeElement>?
        val manyToManies = roundEnv.getElementsAnnotatedWith(ManyToMany::class.java) as Set<TypeElement>?
        val oneToManies = roundEnv.getElementsAnnotatedWith(OneToMany::class.java) as Set<TypeElement>?
        val oneToOnes = roundEnv.getElementsAnnotatedWith(OneToOne::class.java) as Set<TypeElement>?

        val result = mutableListOf<DataClass>()

        viewModels?.forEach { viewModel ->
            val viewModelClassName = viewModel.asClassName()

            val enclosedElements = viewModel.enclosedElements.filter { it.kind == ElementKind.FIELD /*&& it.modifiers.contains(Modifier.PUBLIC)*/ }.map { it as VariableElement }
            val fields = mutableListOf<DataField>()
            enclosedElements.forEach { enclosedElement ->
                val typeName = KotlinNullable.typeName(enclosedElement)
                messager.printMessage(Diagnostic.Kind.WARNING, "Field ${enclosedElement.simpleName} =>  $typeName => ${JavaToKotlinPrimitives.transformIfPrimitive(typeName)}")

                fields.add(DataField(
                        name = enclosedElement.simpleName.toString(),
                        poetTypeName = JavaToKotlinPrimitives.transformIfPrimitive(typeName),
                        relationships = emptyList()// TODO: Implementation need reworking the relationship annotations
                ))
            }

            result.add(DataClass(
                    packageName = viewModelClassName.packageName(),
                    name = viewModelClassName.simpleName(),
                    fields = fields
            ))
        }
        return result
    }
}
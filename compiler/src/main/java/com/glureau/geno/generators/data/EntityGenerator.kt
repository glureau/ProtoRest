package com.glureau.geno.generators.data

import com.glureau.geno.GeneratedClassInfo
import com.glureau.geno.GeneratedClassesInfo
import com.glureau.geno.annotation.data.Identifier
import com.glureau.geno.annotation.data.InternalEntity
import com.glureau.geno.annotation.data.ManyToMany
import com.glureau.geno.utils.AndroidClasses
import com.glureau.geno.utils.AnnotationHelper
import com.glureau.geno.utils.JavaToKotlinPrimitives
import com.glureau.geno.utils.KotlinNullable
import com.squareup.kotlinpoet.*
import java.io.File
import java.util.*
import javax.annotation.processing.Messager
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.tools.Diagnostic

/**
 * Created by Greg on 01/11/2017.
 */
class EntityGenerator(private val messager: Messager) {

    companion object {
        val INTERNAL_ID = "_internal_id"
        val INTERNAL_UPDATE_DATE = "_internal_update_date"
    }


//    @Entity(tableName = "users")
//    data class UsersEntity(
//            @PrimaryKey
//            val _internal_id: Long,
//            var id: Long,
//            var total: Long
//    )


    fun generate(element: TypeElement, outputDir: String?, generatedClassesInfo: GeneratedClassesInfo) {
        val className = element.asClassName()

        val simpleClassName = className.simpleName()
        val packageName = className.packageName() + ".data"
        val entityClassName = simpleClassName + "Entity"

        val classBuilder = TypeSpec.classBuilder(entityClassName)
                .addModifiers(KModifier.DATA)
                .addSuperinterface(InternalEntity::class)
                .addAnnotation(AnnotationSpec.builder(AndroidClasses.ROOM_ENTITY)
                        .addMember("tableName", "\"$simpleClassName\"")
                        .build())


        val constructor = FunSpec.constructorBuilder()
        constructor.addParameter(ParameterSpec
                .builder(INTERNAL_ID, Long::class.asTypeName().asNullable(), KModifier.OVERRIDE)
                .defaultValue("null")
                .addAnnotation(AnnotationSpec.builder(AndroidClasses.ROOM_PRIMARY_KEY).addMember("autoGenerate", "true").build())
                .build())
        classBuilder.addProperty(PropertySpec
                .builder(INTERNAL_ID, Long::class.asTypeName().asNullable(), KModifier.OVERRIDE)
                .initializer(INTERNAL_ID)
                .addAnnotation(AndroidClasses.ROOM_PRIMARY_KEY)
                .build())

        constructor.addParameter(ParameterSpec
                .builder(INTERNAL_UPDATE_DATE, Date::class)
                .defaultValue("java.util.Date()")
                .addModifiers(KModifier.OVERRIDE)
                .build())
        classBuilder.addProperty(PropertySpec
                .builder(INTERNAL_UPDATE_DATE, Date::class)
                .addModifiers(KModifier.OVERRIDE)
                .initializer(INTERNAL_UPDATE_DATE)
                .build())

        addParameters(element, constructor, classBuilder)

        classBuilder.primaryConstructor(constructor.build())

        val file = FileSpec.builder(packageName, entityClassName)
                .indent("    ")
                .addType(classBuilder.build())
                .build()

        val path = File(outputDir)
        file.writeTo(path)

        messager.printMessage(Diagnostic.Kind.NOTE, "Generated $entityClassName")
        generatedClassesInfo.entity = GeneratedClassInfo(ClassName(packageName, entityClassName))
    }


    private fun addParameters(element: TypeElement, constructor: FunSpec.Builder, classBuilder: TypeSpec.Builder) {
        val fields = element.enclosedElements.filter { it.kind == ElementKind.FIELD /*&& it.modifiers.contains(Modifier.PUBLIC)*/ }.map { it as VariableElement }

        fields.forEach { field ->
            val typeName = KotlinNullable.typeName(field)
            messager.printMessage(Diagnostic.Kind.WARNING, "Field ${field.simpleName} =>  $typeName => ${JavaToKotlinPrimitives.transformIfPrimitive(typeName)}")

            val fieldName = field.simpleName.toString()
            val finalTypeName = JavaToKotlinPrimitives.transformIfPrimitive(typeName)
            constructor.addParameter(fieldName, finalTypeName)
            classBuilder.addProperty(
                    PropertySpec.builder(fieldName, finalTypeName)
                            .initializer(fieldName).build())
        }
    }
}
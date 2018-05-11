package com.glureau.geno.generators.data

import com.glureau.geno.GeneratedClassInfo
import com.glureau.geno.GeneratedClassesInfo
import com.glureau.geno.annotation.data.ManyToMany
import com.glureau.geno.utils.AndroidClasses
import com.glureau.geno.utils.AnnotationHelper
import com.squareup.kotlinpoet.*
import java.io.File
import javax.annotation.processing.Messager
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

class ManyToManyEntityGenerator(private val messager: Messager) {

    companion object {
        val INTERNAL_ID = "_internal_id"
        val INTERNAL_UPDATE_DATE = "_internal_update_date"
    }


//    @Entity(tableName = "a_to_b",
//            indices = arrayOf(Index(value = arrayOf("a", "b"), unique = true)),
//            foreignKeys = arrayOf(
//                    ForeignKey(entity = SimpleGithubUserEntity::class,
//                            parentColumns = arrayOf("_internal_id"),
//                            childColumns = arrayOf("a")),
//                    ForeignKey(entity = SimpleGithubOrganizationEntity::class,
//                            parentColumns = arrayOf("_internal_id"),
//                            childColumns = arrayOf("b"))
//            )
//    )
//    data class A_to_B(
//            @PrimaryKey(autoGenerate = true)
//            val _internal_id: Long,
//
//            val a_id: Long,
//            val b_id: Long
//    )


    fun generate(relationship: TypeElement, outputDir: String?, info: GeneratedClassesInfo) {
        val referenceClassA = AnnotationHelper.getAnnotationClassValue(relationship, ManyToMany::class, "referenceClassA").toString()
        val referenceClassB = AnnotationHelper.getAnnotationClassValue(relationship, ManyToMany::class, "referenceClassB").toString()
        messager.printMessage(Diagnostic.Kind.ERROR, "referenceClassA: $referenceClassA")
        messager.printMessage(Diagnostic.Kind.ERROR, "referenceClassB: $referenceClassB")
        val packageA = referenceClassA.substringBeforeLast(".")
        val classNameStrA = referenceClassA.substringAfterLast(".")
        val classNameA = ClassName(packageA, classNameStrA)
        val packageB = referenceClassB.substringBeforeLast(".")
        val classNameStrB = referenceClassB.substringAfterLast(".")
        val classNameB = ClassName(packageB, classNameStrB)

        val manyToManyClassNameStr = classNameStrA + "_to_" + classNameStrB
        val manyToManyPackage = packageA + ".data"
        val manyToManyClassName = ClassName(manyToManyPackage, manyToManyClassNameStr)

        val classBuilder = TypeSpec.classBuilder(manyToManyClassName)
                .addModifiers(KModifier.DATA)
                .addAnnotation(AnnotationSpec.builder(AndroidClasses.ROOM_ENTITY)
                        .addMember("tableName", "\"$manyToManyClassNameStr\"")
                        .addMember("indices", "arrayOf(\n" +
                                "        android.arch.persistence.room.Index(value = arrayOf(\"${classNameStrA}_id\", \"${classNameStrB}_id\"), unique = true),\n" +
                                "        android.arch.persistence.room.Index(value = \"${classNameStrA}_id\", unique = false),\n" +
                                "        android.arch.persistence.room.Index(value = \"${classNameStrB}_id\", unique = false)\n" +
                                ")")

                        .addMember("foreignKeys", "arrayOf(\n" +
                                "android.arch.persistence.room.ForeignKey(entity = $packageA.data.${classNameStrA}Entity::class,\n" +
                                "parentColumns = arrayOf(\"_internal_id\"),\n" +
                                "childColumns =  arrayOf(\"${classNameStrA}_id\")\n" +
                                "),\n" +
                                "android.arch.persistence.room.ForeignKey(entity = $packageB.data.${classNameStrB}Entity::class,\n" +
                                "parentColumns = arrayOf(\"_internal_id\"),\n" +
                                "childColumns =  arrayOf(\"${classNameStrB}_id\")\n" +
                                ")\n" +
                                ")")
                        .build())

        val constructor = FunSpec.constructorBuilder()

        constructor.addParameter(ParameterSpec
                .builder(EntityGenerator.INTERNAL_ID, Long::class.asTypeName().asNullable())
                .defaultValue("null")
                .addAnnotation(AnnotationSpec.builder(AndroidClasses.ROOM_PRIMARY_KEY).addMember("autoGenerate", "true").build())
                .build())
        classBuilder.addProperty(PropertySpec
                .builder(EntityGenerator.INTERNAL_ID, Long::class.asTypeName().asNullable())
                .initializer(EntityGenerator.INTERNAL_ID)
                .addAnnotation(AndroidClasses.ROOM_PRIMARY_KEY)
                .build())

        constructor.addParameter(ParameterSpec
                .builder("${classNameStrA}_id", Long::class)
                .build())
        classBuilder.addProperty(PropertySpec
                .builder("${classNameStrA}_id", Long::class)
                .initializer("${classNameStrA}_id")
                .build())

        constructor.addParameter(ParameterSpec
                .builder("${classNameStrB}_id", Long::class)
                .build())
        classBuilder.addProperty(PropertySpec
                .builder("${classNameStrB}_id", Long::class)
                .initializer("${classNameStrB}_id")
                .build())

        classBuilder.primaryConstructor(constructor.build())

        val file = FileSpec.builder(manyToManyPackage, manyToManyClassNameStr)
                .indent("    ")
                .addType(classBuilder.build())
                .build()

        val path = File(outputDir)
        file.writeTo(path)

        messager.printMessage(Diagnostic.Kind.NOTE, "Generated $manyToManyClassNameStr")
        info.entity = GeneratedClassInfo(manyToManyClassName)
    }
}
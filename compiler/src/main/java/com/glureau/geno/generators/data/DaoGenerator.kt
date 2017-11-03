package com.glureau.geno.generators.data

import com.glureau.geno.GeneratedClassInfo
import com.glureau.geno.GeneratedClassesInfo
import com.glureau.geno.utils.AndroidClasses.ROOM_DAO
import com.glureau.geno.utils.AndroidClasses.ROOM_DELETE
import com.glureau.geno.utils.AndroidClasses.ROOM_INSERT
import com.glureau.geno.utils.AndroidClasses.ROOM_ON_CONFLICT_STRATEGY_REPLACE
import com.glureau.geno.utils.AndroidClasses.ROOM_QUERY
import com.glureau.geno.utils.ReactivexClasses.MAYBE
import com.squareup.kotlinpoet.*
import java.io.File
import javax.annotation.processing.Messager
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

/**
 * Created by Greg on 01/11/2017.
 */
class DaoGenerator(private val messager: Messager) {


//    @Dao
//    interface UserDao {
//
//        @Query("SELECT * FROM users WHERE id = :id")
//        fun getUserById(id: Long): Maybe<UsersEntity>
//
//        @Insert(onConflict = OnConflictStrategy.REPLACE)
//        fun insertUser(user: UsersEntity)
//
//        @Query("DELETE FROM users WHERE id = :id")
//        fun deleteUserById(id: Long)
//    }


    fun generate(element: TypeElement, outputDir: String?, generatedClassesInfo: GeneratedClassesInfo) {
        val className = element.asClassName()

        val simpleClassName = className.simpleName()
        val packageName = className.packageName() + ".data"
        val entityName = simpleClassName + "Entity"
        val entityClassName = ClassName(packageName, entityName)
        val daoName = simpleClassName + "Dao"


        val classBuilder = TypeSpec.interfaceBuilder(daoName)
                .addAnnotation(ROOM_DAO)

        classBuilder.addFunction(
                FunSpec.builder("get${simpleClassName}ById")
                        .addModifiers(KModifier.ABSTRACT)
                        .addParameter("id", Long::class)
                        .returns(MAYBE(entityClassName))
                        .addAnnotation(AnnotationSpec.builder(ROOM_QUERY)
                                .addMember("value", "\"SELECT * FROM $simpleClassName WHERE id = :id\"")
                                .build())
                        .build()
        )

        classBuilder.addFunction(
                FunSpec.builder("insert$simpleClassName")
                        .addModifiers(KModifier.ABSTRACT)
                        .addParameter(entityName.decapitalize(), entityClassName)
                        .addAnnotation(AnnotationSpec.builder(ROOM_INSERT)
                                .addMember("onConflict", "%T", ROOM_ON_CONFLICT_STRATEGY_REPLACE)
                                .build())
                        .build()
        )

        classBuilder.addFunction(
                FunSpec.builder("delete$simpleClassName")
                        .addModifiers(KModifier.ABSTRACT)
                        .addParameter(entityName.decapitalize(), entityClassName)
                        .addAnnotation(ROOM_DELETE)
                        .build()
        )

        val file = FileSpec.builder(packageName, daoName)
                .indent("    ")
                .addType(classBuilder.build())
                .build()

        val path = File(outputDir)
        file.writeTo(path)

        generatedClassesInfo.dao = GeneratedClassInfo(ClassName(packageName, daoName))
        messager.printMessage(Diagnostic.Kind.NOTE, "Generated $daoName")
    }

}
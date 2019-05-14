package com.glureau.geno.generators.data

import com.glureau.geno.GeneratedClassInfo
import com.glureau.geno.GeneratedClassesInfo
import com.glureau.geno.utils.AndroidClasses.ROOM_DAO
import com.glureau.geno.utils.AndroidClasses.ROOM_DELETE
import com.glureau.geno.utils.AndroidClasses.ROOM_INSERT
import com.glureau.geno.utils.AndroidClasses.ROOM_ON_CONFLICT_STRATEGY_REPLACE
import com.glureau.geno.utils.AndroidClasses.ROOM_QUERY
import com.glureau.geno.utils.AnnotationHelper
import com.glureau.geno.utils.JavaToKotlinPrimitives
import com.glureau.geno.utils.KotlinClasses.LIST
import com.glureau.geno.utils.KotlinNullable
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


    fun generate(element: TypeElement, outputDir: String?, generatedClassesInfo: GeneratedClassesInfo, relationships: Set<TypeElement>) {
        val className = element.asClassName()

        val simpleClassName = className.simpleName()
        val packageName = className.packageName() + ".data"
        val entityName = simpleClassName + "Entity"
        val entityClassName = ClassName(packageName, entityName)
        val daoName = simpleClassName + "Dao"

        val identifier = AnnotationHelper.getIdentifier(element) ?: error("foo")

        val classBuilder = TypeSpec.interfaceBuilder(daoName)
                .addAnnotation(ROOM_DAO)

        classBuilder.addFunction(
                FunSpec.builder("get${simpleClassName}By" + identifier.simpleName.toString().capitalize())
                        .addModifiers(KModifier.ABSTRACT)
                        .addParameter(identifier.simpleName.toString(), JavaToKotlinPrimitives.transformIfPrimitive(KotlinNullable.typeName(identifier)))
                        .returns(MAYBE(entityClassName))
                        .addAnnotation(AnnotationSpec.builder(ROOM_QUERY)
                                .addMember("value", "\"SELECT * FROM $simpleClassName WHERE ${identifier.simpleName} = :${identifier.simpleName}\"")
                                .build())
                        .build()
        )

        //TODO: generify
        if (simpleClassName.contains("User")) {
            classBuilder.addFunction(
                    FunSpec.builder("get${simpleClassName}ByOrgs")
                            .addModifiers(KModifier.ABSTRACT)
                            .addParameter("orgs", String::class)
                            .returns(MAYBE(LIST(entityClassName)))
                            .addAnnotation(AnnotationSpec.builder(ROOM_QUERY)
                                    .addMember("value", "\"SELECT a.* FROM $simpleClassName a " +
                                            "INNER JOIN SimpleGithubOrganization_to_SimpleGithubUser b ON a._internal_id=b.${simpleClassName}_id " +
                                            "INNER JOIN SimpleGithubOrganization c ON c._internal_id=b.SimpleGithubOrganization_id " +
                                            "WHERE c.login = :orgs\"")
                                    .build())
                            .build()
            )
        }

//        @Query("SELECT * FROM SimpleGithubUser")
//        fun getSimpleGithubUsers(): Maybe<List<SimpleGithubUserEntity>>

        classBuilder.addFunction(
                FunSpec.builder("get${simpleClassName}s")
                        .addModifiers(KModifier.ABSTRACT)
                        .returns(MAYBE(LIST(entityClassName)))
                        .addAnnotation(AnnotationSpec.builder(ROOM_QUERY)
                                .addMember("value", "\"SELECT * FROM $simpleClassName\"")
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
        if (simpleClassName.contains("User")) {
            classBuilder.addFunction(
                    FunSpec.builder("insertSimpleGithubOrganization_to_SimpleGithubUser")
                            .addModifiers(KModifier.ABSTRACT)
                            .addParameter("manyToManyJoin", ClassName(packageName, "SimpleGithubOrganization_to_SimpleGithubUser"))
                            .addAnnotation(AnnotationSpec.builder(ROOM_INSERT)
                                    .addMember("onConflict", "%T", ROOM_ON_CONFLICT_STRATEGY_REPLACE)
                                    .build())
                            .build()
            )
        }

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
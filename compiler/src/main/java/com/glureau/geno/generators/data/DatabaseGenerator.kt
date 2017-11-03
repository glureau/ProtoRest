package com.glureau.geno.generators.data

import com.glureau.geno.GeneratedClassesInfo
import com.glureau.geno.utils.AndroidClasses
import com.glureau.geno.utils.KotlinClasses.ANNOTATION_VOLATILE
import com.glureau.geno.utils.LibClasses.TYPE_CONVERTERS
import com.squareup.kotlinpoet.*
import java.io.File
import javax.annotation.processing.Messager
import javax.tools.Diagnostic

/**
 * Created by Greg on 01/11/2017.
 */
class DatabaseGenerator(private val messager: Messager) {


//    @Database(entities = arrayOf(UsersEntity::class, ...), version = 1, exportSchema = false)
//    @TypeConverters({Converters.class})
//    abstract class GenoDatabase : RoomDatabase() {
//
//        abstract fun userDao(): UserDao
//        ...
//
//        companion object {
//            @Volatile private var INSTANCE: GenoDatabase? = null
//
//            fun getInstance(context: Context): GenoDatabase =
//                    INSTANCE ?: synchronized(this) {
//                        INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
//                    }
//
//            private fun buildDatabase(context: Context) = Room.databaseBuilder(context.applicationContext, GenoDatabase::class.java, "GenoDatabase.db").build()
//        }
//    }


    fun generate(generatedClasses: MutableMap<ClassName, GeneratedClassesInfo>, outputDir: String?) {

        val entities = generatedClasses.values.map { it.entity?.className ?: error("Entity should not be null here!") }
        val databaseName = "GenoDatabase"
        val packageName = "com.glureau.geno.db"
        val databaseClassName = ClassName(packageName, databaseName)
        val classBuilder = TypeSpec.classBuilder(databaseName)
                .superclass(AndroidClasses.ROOM_DATABASE)
                .addAnnotation(AnnotationSpec.builder(AndroidClasses.DATABASE)
                        .addMember("version", "1") //TODO : Should be an annotation param
                        .addMember("exportSchema", "false")
                        .addMember("entities", "arrayOf(" + entities.joinToString { "%T::class" } + ")", *entities.toTypedArray())
                        .build())
                .addAnnotation(AnnotationSpec.builder(AndroidClasses.ROOM_TYPE_CONVERTERS)
                        .addMember("value", "%T::class", TYPE_CONVERTERS)
                        .build())
                .addModifiers(KModifier.ABSTRACT)

        val daos = generatedClasses.values.map { it.dao?.className ?: error("Dao should not be null here!") }
        daos.forEach { dao ->
            classBuilder.addFunction(
                    FunSpec.builder(dao.simpleName().decapitalize())
                            .addModifiers(KModifier.ABSTRACT)
                            .returns(dao)
                            .build()
            )
        }

        classBuilder.companionObject(TypeSpec.companionObjectBuilder()
                .addProperty(PropertySpec.varBuilder("INSTANCE", databaseClassName.asNullable())
                        .addModifiers(KModifier.PRIVATE)
                        .addAnnotation(ANNOTATION_VOLATILE)
                        .initializer("null")
                        .build())
                .addFunction(FunSpec.builder("getInstance")
                        .addParameter("context", AndroidClasses.CONTEXT)
                        .returns(databaseClassName)
                        .addStatement("return INSTANCE ?: synchronized(this) { INSTANCE ?: buildDatabase(context).also { INSTANCE = it } }")
                        .build())
                .addFunction(FunSpec.builder("buildDatabase")
                        .addParameter("context", AndroidClasses.CONTEXT)
                        .addModifiers(KModifier.PRIVATE)
                        .returns(databaseClassName)
                        .addStatement("return %T.databaseBuilder(context.applicationContext, $databaseName::class.java, \"$databaseName.db\").build()", AndroidClasses.ROOM)
                        .build())
                .build())

        val file = FileSpec.builder(packageName, databaseName)
                .indent("    ")
                .addType(classBuilder.build())
                .build()

        val path = File(outputDir)
        file.writeTo(path)

        messager.printMessage(Diagnostic.Kind.NOTE, "Generated $databaseName")
    }

}
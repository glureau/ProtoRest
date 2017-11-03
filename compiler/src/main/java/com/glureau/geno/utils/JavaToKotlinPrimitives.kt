package com.glureau.geno.utils

import com.squareup.kotlinpoet.TypeName

/**
 * Created by Greg on 01/11/2017.
 */
object JavaToKotlinPrimitives {
    /**
     * @return the Kotlin primitive corresponding to the given java typeName, or the given parameter if it's not a primitive
     */
    fun transformIfPrimitive(typeName: TypeName): TypeName {
        return when (typeName.toString()) {
            "java.lang.String", "java.lang.String?" -> KotlinNullable.asTypeName(String::class, typeName.nullable)
            "java.lang.Double", "java.lang.Double?" -> KotlinNullable.asTypeName(Double::class, typeName.nullable)
            "java.lang.Float", "java.lang.Float?" -> KotlinNullable.asTypeName(Float::class, typeName.nullable)
            "java.lang.Long", "java.lang.Long?" -> KotlinNullable.asTypeName(Long::class, typeName.nullable)
            "java.lang.Integer", "java.lang.Integer?" -> KotlinNullable.asTypeName(Int::class, typeName.nullable)
            "java.lang.Short", "java.lang.Short?" -> KotlinNullable.asTypeName(Short::class, typeName.nullable)
            "java.lang.Byte", "java.lang.Byte?" -> KotlinNullable.asTypeName(Byte::class, typeName.nullable)
            else -> typeName
        }
    }
}

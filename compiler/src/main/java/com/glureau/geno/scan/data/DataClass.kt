package com.glureau.geno.scan.data

import com.squareup.kotlinpoet.TypeName

data class DataClass(
        val packageName: String,
        val name: String,
        val tableName: String = name,
        val fields: List<DataField>)

data class DataField(
        val name: String,
        val columnName: String = name,
        val poetTypeName: TypeName,
        val relationships: List<DataRelation>)

interface DataRelation {
    val from: DataField
    val to: DataField
}

data class OneToOneDataRelation(override val from: DataField, override val to: DataField) : DataRelation
data class ManyToManyDataRelation(override val from: DataField, override val to: DataField) : DataRelation
data class OneToManyDataRelation(override val from: DataField, override val to: DataField) : DataRelation
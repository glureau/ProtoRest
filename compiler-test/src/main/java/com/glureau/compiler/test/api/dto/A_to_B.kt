package com.glureau.compiler.test.api.dto

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import com.glureau.compiler.test.api.dto.data.SimpleGithubOrganizationEntity
import com.glureau.compiler.test.api.dto.data.SimpleGithubUserEntity
/*
@Entity(tableName = "a_to_b",
        indices = arrayOf(Index(value = arrayOf("a", "b"), unique = true)),
        foreignKeys = arrayOf(
                ForeignKey(entity = SimpleGithubUserEntity::class,
                        parentColumns = arrayOf("_internal_id"),
                        childColumns = arrayOf("a")),
                ForeignKey(entity = SimpleGithubOrganizationEntity::class,
                        parentColumns = arrayOf("_internal_id"),
                        childColumns = arrayOf("b"))
        )
)
data class A_to_B(
        @PrimaryKey(autoGenerate = true)
        val _internal_id: Long,

        val a: Long,
        val b: Long
)
*/
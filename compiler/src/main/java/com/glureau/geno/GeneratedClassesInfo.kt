package com.glureau.geno

import com.squareup.kotlinpoet.ClassName

/**
 * Created by Greg on 03/11/2017.
 */
data class GeneratedClassesInfo(
        var entity: GeneratedClassInfo? = null,
        var dao: GeneratedClassInfo? = null,
        var viewHolder: GeneratedClassInfo? = null,
        var recyclerView: GeneratedClassInfo? = null
)

data class GeneratedClassInfo(val className: ClassName)
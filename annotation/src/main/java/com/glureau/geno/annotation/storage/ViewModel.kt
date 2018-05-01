package com.glureau.geno.annotation.storage

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class ViewModel
// We could have different annotations depending of the desired generation, e.g. @Entity > @Dao > @Repository > @ViewModel
// and some of them encapsulating the others (@Dao implicitly requires @Entity)
// But at the end, it would give a complex annotation API, and it doesn't look great so far.
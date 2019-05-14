package com.glureau.geno.utils

import com.glureau.geno.annotation.data.Identifier
import javax.lang.model.element.*
import javax.lang.model.type.TypeMirror
import kotlin.reflect.KClass


/**
 * Created by Greg on 22/10/2017.
 */
object AnnotationHelper {
    private fun getAnnotationMirror(typeElement: TypeElement, clazz: Class<*>): AnnotationMirror? {
        val clazzName = clazz.name
        return typeElement.annotationMirrors.firstOrNull { it.annotationType.toString() == clazzName }
    }

    private fun getAnnotationValue(annotationMirror: AnnotationMirror, key: String): AnnotationValue? {
        for ((key1, value) in annotationMirror.elementValues) {
            if (key1.simpleName.toString() == key) {
                return value
            }
        }
        return null
    }


    fun getAnnotationClassValue(element: TypeElement, kclass: KClass<*>, name: String): TypeMirror? {
        val am = getAnnotationMirror(element, kclass.java) ?: return null
        val av = getAnnotationValue(am, name)
        return if (av == null) {
            null
        } else {
            av.value as TypeMirror
        }
    }

    fun getIdentifier(element: TypeElement) = element.enclosedElements
            .filter { it.kind == ElementKind.FIELD }
            .map { it as VariableElement }
            .firstOrNull { it.getAnnotation(Identifier::class.java) != null }

}
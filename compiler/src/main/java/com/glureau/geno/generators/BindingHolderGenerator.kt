package com.glureau.geno.generators

import com.glureau.geno.annotation.CustomView
import com.glureau.geno.utils.AndroidClasses
import com.glureau.geno.utils.AndroidClasses.RECYCLER_VIEW_HOLDER
import com.glureau.geno.utils.AnnotationHelper
import com.squareup.kotlinpoet.*
import java.io.File
import javax.annotation.processing.Messager
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Created by Greg on 22/10/2017.
 */
class BindingHolderGenerator(val messager: Messager) {


//    package com.glureau.geno.test
//    class GithubUserBindingHolder(val binding: GithubUserBinding) : RecyclerView.ViewHolder(binding.root) {
//        fun bind(githubUser: GithubUser) {
//            binding.githubUser = githubUser
//            binding.executePendingBindings()
//        }
//    }

//    class GithubUserBindingHolder(val githubUserBinding: GithubUser) {
//        fun bind(githubUser: GithubUser) {
//            githubUserBinding.githubUser = githubUser
//            githubUserBinding.executePendingBindings()
//        }
//    }

    fun generateView(element: TypeElement) {
        val className = element.asClassName()
        val simpleClassName = className.simpleName()
        val packageName = className.packageName()
        val instanceName = simpleClassName.decapitalize()
        val bindingHolderClassName = simpleClassName + "BindingHolder"

        val R = AnnotationHelper.getAnnotationClassValue(element, CustomView::class, "R")
        val viewName = element.getAnnotation(CustomView::class.java).viewName

        val xmlLayoutFile = File("compiler-test/src/main/res/layout/$viewName.xml")
        val xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlLayoutFile)
        if (xmlDoc.documentElement.nodeName != "layout") {
            messager.printMessage(Diagnostic.Kind.ERROR, "$className is not using Android Data Binding ($xmlLayoutFile should start with 'layout' but starts with '${xmlDoc.documentElement.nodeName}'")
            return // Not using Android Binding, nothing to do here.
        }

        val projectPackage = R.toString().substring(0, R.toString().length - 2)
        val bindingName = viewName.split("_").joinToString("") { it.capitalize() } + "Binding"

        val bindingClassName = ClassName(projectPackage + ".databinding", bindingName)
        val bindingInstanceName = bindingClassName.simpleName().decapitalize()

        val classBuilder = TypeSpec.classBuilder(bindingHolderClassName)
                .primaryConstructor(FunSpec.constructorBuilder()
                        .addParameter(bindingInstanceName, bindingClassName)
                        .addParameter("view", AndroidClasses.VIEW)
                        .build())

                .superclass(RECYCLER_VIEW_HOLDER)
                .addSuperclassConstructorParameter("view")

                .addProperty(PropertySpec.builder(bindingInstanceName, bindingClassName)
                        .initializer(bindingInstanceName)
                        .build())
                .addFunction(FunSpec.builder("bind")
                        .addParameter(instanceName, className)
                        .addStatement("$bindingInstanceName.$instanceName = $instanceName")
                        .addStatement("$bindingInstanceName.executePendingBindings()")
                        .build()
                )

        val file = FileSpec.builder(packageName, bindingHolderClassName)
                .indent("    ")
                .addType(classBuilder.build())
                .build()

        val path = File("./compiler-test/build/generated/source/kapt/debug")
        file.writeTo(path)

        messager.printMessage(Diagnostic.Kind.NOTE, "Generated $simpleClassName")
    }
}
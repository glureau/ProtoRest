package com.glureau.geno.generators.view

import com.glureau.geno.GeneratedClassInfo
import com.glureau.geno.GeneratedClassesInfo
import com.glureau.geno.annotation.CustomView
import com.glureau.geno.utils.AndroidClasses.RECYCLER_VIEW_HOLDER
import com.glureau.geno.utils.AnnotationHelper
import com.squareup.kotlinpoet.*
import org.w3c.dom.Document
import java.io.File
import javax.annotation.processing.Messager
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

/**
 * Created by Greg on 22/10/2017.
 */
class ViewHolderGenerator(private val messager: Messager) {

//    package com.glureau.geno.test
//    class GithubUserBindingHolder(val binding: GithubUserBinding) : RecyclerView.ViewHolder(binding.root) {
//        fun bind(githubUser: GithubUser) {
//            binding.user /* name from XML if same class */ = githubUser
//            binding.executePendingBindings()
//        }
//    }

    fun generate(element: TypeElement, xmlLayout: Document, outputDir: String?, generatedClassesInfo: GeneratedClassesInfo) {
        val className = element.asClassName()

        val simpleClassName = className.simpleName()
        val packageName = className.packageName() + ".view"
        val instanceName = simpleClassName.decapitalize()
        val holderClassName = simpleClassName + "BindingHolder"

        val R = AnnotationHelper.getAnnotationClassValue(element, CustomView::class, "R")
        val viewName = element.getAnnotation(CustomView::class.java).viewName

        val projectPackage = R.toString().substring(0, R.toString().length - 2)
        val bindingName = viewName.split("_").joinToString("") { it.capitalize() } + "Binding"

        val bindingClassName = ClassName(projectPackage + ".databinding", bindingName)
        val bindingInstanceName = bindingClassName.simpleName().decapitalize()

        var xmlBindingName = instanceName
        val nodeList = xmlLayout.getElementsByTagName("variable")
        for (i in 0 until nodeList.length) {
            val type = nodeList.item(i).attributes.getNamedItem("type").textContent
            if (type == className.canonicalName) { // Found a mapping with the same class
                xmlBindingName = nodeList.item(i).attributes.getNamedItem("name").textContent
                continue // Match with the first mapping ONLY (known limitation)
            }
        }

        val classBuilder = TypeSpec.classBuilder(holderClassName)
                .primaryConstructor(FunSpec.constructorBuilder()
                        .addParameter(bindingInstanceName, bindingClassName)
                        .build())

                .superclass(RECYCLER_VIEW_HOLDER)
                .addSuperclassConstructorParameter("$bindingInstanceName.root")

                .addProperty(PropertySpec.builder(bindingInstanceName, bindingClassName)
                        .initializer(bindingInstanceName)
                        .build())
                .addFunction(FunSpec.builder("bind")
                        .addParameter(instanceName, className)
                        .addStatement("$bindingInstanceName.$xmlBindingName = $instanceName")
                        .addStatement("$bindingInstanceName.executePendingBindings()")
                        .build()
                )

        val file = FileSpec.builder(packageName, holderClassName)
                .indent("    ")
                .addType(classBuilder.build())
                .build()

        val path = File(outputDir)
        file.writeTo(path)

        generatedClassesInfo.viewHolder = GeneratedClassInfo(ClassName(packageName, holderClassName))
        messager.printMessage(Diagnostic.Kind.NOTE, "Generated $holderClassName")
    }
}
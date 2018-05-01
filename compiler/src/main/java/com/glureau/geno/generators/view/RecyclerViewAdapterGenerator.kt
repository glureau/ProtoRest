package com.glureau.geno.generators.view

import com.glureau.geno.GeneratedClassInfo
import com.glureau.geno.GeneratedClassesInfo
import com.glureau.geno.annotation.view.CustomView
import com.glureau.geno.utils.AndroidClasses
import com.glureau.geno.utils.AnnotationHelper
import com.glureau.geno.utils.ImportInjection
import com.squareup.kotlinpoet.*
import java.io.File
import javax.annotation.processing.Messager
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

/**
 * Created by Greg on 22/10/2017.
 */
class RecyclerViewAdapterGenerator(private val messager: Messager) {

//    package com.glureau.geno.test
//    class GithubUserBindingRecyclerViewAdapter(val githubUsers: MutableList<GithubUser> = mutableListOf()) : RecyclerView.Adapter<TrackRecyclerViewAdapter.TrackViewHolder>() {
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
//            val binding = GithubUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//            return GithubUserHolder(binding)
//        }
//
//        override fun onBindViewHolder(holder: GithubUserHolder, position: Int) {
//            val githubUser = githubUsers[position]
//            holder.bind(githubUser)
//        }
//
//        override fun getItemCount() = githubUsers.size
//    }


    fun generate(element: TypeElement, outputDir: String?, generatedClassesInfo: GeneratedClassesInfo) {
        val className = element.asClassName()

        val simpleClassName = className.simpleName()
        val packageName = className.packageName() + ".view"
        val instanceName = simpleClassName.decapitalize()
        val holderClassName = simpleClassName + "BindingHolder"
        val adapterClassName = simpleClassName + "BindingRecyclerViewAdapter"

        val R = AnnotationHelper.getAnnotationClassValue(element, CustomView::class, "R")
        val viewName = element.getAnnotation(CustomView::class.java).viewName

        val projectPackage = R.toString().substring(0, R.toString().length - 2)
        val bindingName = viewName.split("_").joinToString("") { it.capitalize() } + "Binding"

        val bindingClassName = ClassName(projectPackage + ".databinding", bindingName)

        val dataCollection = instanceName + "s"
        val dataClass = ClassName.bestGuess("MutableList<$simpleClassName>")

        val classBuilder = TypeSpec.classBuilder(adapterClassName)
                .primaryConstructor(FunSpec.constructorBuilder()
                        .addParameter(
                                ParameterSpec.builder(dataCollection, dataClass)
                                        .defaultValue("mutableListOf()")
                                        .build())
                        .build())

                .superclass(AndroidClasses.RECYCLER_VIEW_ADAPTER(holderClassName))
                .addProperty(PropertySpec.builder(dataCollection, dataClass)
                        .initializer(dataCollection)
                        .build())

                .addFunction(FunSpec.builder("onCreateViewHolder")
                        .addModifiers(KModifier.OVERRIDE)
                        .addParameter("parent", AndroidClasses.VIEW_GROUP)
                        .addParameter("viewType", Int::class)
                        .addStatement("return $holderClassName($bindingClassName.inflate(%T.from(parent.context)))", AndroidClasses.LAYOUT_INFLATER)
                        .returns(ClassName(packageName, holderClassName))
                        .build())


                .addFunction(FunSpec.builder("onBindViewHolder")
                        .addModifiers(KModifier.OVERRIDE)
                        .addParameter("holder", ClassName(packageName, holderClassName))
                        .addParameter("position", Int::class)
                        .addStatement("holder.bind($dataCollection[position])")
                        .build())

                .addFunction(FunSpec.builder("getItemCount")
                        .addModifiers(KModifier.OVERRIDE)
                        .addStatement("return $dataCollection.size")
                        .build()
                )

        val file = FileSpec.builder(packageName, adapterClassName)
                .indent("    ")
                .addType(classBuilder.build())
                .build()

        val path = File(outputDir)
        file.writeTo(path)

        ImportInjection.injectImport(outputDir + "/" + packageName.replace(".", "/") + "/" + adapterClassName + ".kt", className.canonicalName)

        generatedClassesInfo.recyclerView = GeneratedClassInfo(ClassName(packageName, adapterClassName))
        messager.printMessage(Diagnostic.Kind.NOTE, "Generated $adapterClassName")
    }
}
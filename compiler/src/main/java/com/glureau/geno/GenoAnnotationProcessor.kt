package com.glureau.geno

import com.glureau.geno.annotation.*
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

/**
 * Created by Greg on 21/10/2017.
 */
@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_6) // Generate Warnings, not sure it's very helpful :/
class GenoAnnotationProcessor : AbstractProcessor() {

    private lateinit var messager: Messager
    private lateinit var filer: Filer

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        messager = processingEnv.messager
        filer = processingEnv.filer
    }

    companion object {
        private val SUPPORTED_ANNOTATIONS = arrayOf(
                CustomView::class.java,
                Endpoint::class.java,
                EndpointParam::class.java,
                Image::class.java,
                RestError::class.java)
                .map { it.canonicalName }.toSet()

    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        return SUPPORTED_ANNOTATIONS
    }

    override fun process(annotations: Set<TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        generateViews(roundEnv)

        processingEnv.messager.printMessage(Diagnostic.Kind.WARNING, "A little step for me...")
        return false
    }

    private fun generateViews(roundEnv: RoundEnvironment) {
        val elements = roundEnv.getElementsAnnotatedWith(CustomView::class.java)
        for (it in elements) {
            if (it.kind != ElementKind.CLASS) {
                messager.printMessage(Diagnostic.Kind.WARNING, "@CustomView should be use on class, skipping generation", it)
                continue
            }
            generateView(it as TypeElement)
        }
    }


    //    internal class GithubUserViewManager(val githubUser: GithubUser) {
//
//        companion object {
//            fun inflate(activity: Activity, root: ViewGroup): View {
//                return activity.layoutInflater.inflate(R.layout.activity_main, root, false)
//            }
//        }
//
//        fun fill(view: View) {
//            val loginTextView: TextView? = view.findViewById(R.id.geno_login) as TextView
//            if (loginTextView != null) {
//                loginTextView.text = githubUser.login
//            }
//
//            val nameTextView: TextView? = view.findViewById(R.id.geno_name) as TextView
//            if (nameTextView != null) {
//                nameTextView.text = githubUser.name
//            }
//
//            val avatarImageView: TextView? = view.findViewById(R.id.geno_avatar) as ImageView
//            if (avatarImageView != null) {
//                GlideApp.with(activity).asBitmap().load(githubUser.avatar).into(avatarImageView)
//            }
//        }
//    }
    private fun generateView(element: TypeElement) {
        val className = element.asClassName()
        val simpleClassName = className.simpleName() + "ViewManager"
        val packageName = className.packageName()
        val instanceName = className.simpleName().decapitalize()
        val file = FileSpec.builder(packageName, simpleClassName)
                .addType(TypeSpec.classBuilder(simpleClassName)
                        .primaryConstructor(FunSpec.constructorBuilder()
                                .addParameter(instanceName, className)
                                .build())
                        .addProperty(PropertySpec.builder(instanceName, className)
                                .initializer(instanceName)
                                .build())
                        .companionObject(TypeSpec.companionObjectBuilder(simpleClassName)
                                .addFunction(FunSpec.builder("inflate")
//                                        .addParameter("view"), "")
                                        .addStatement("return activity.layoutInflater.inflate(%S, root, false)", "R.layout.$instanceName")
                                        .returns(String::class)
                                        //.returns(android.view.View::class)
                                        .build())
                                .build())
                        .build())
                .build()

        val path = File("./compiler-test/build/generated/source/kapt/debug")
        file.writeTo(path)
    }
}

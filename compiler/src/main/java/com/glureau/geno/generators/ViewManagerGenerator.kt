package com.glureau.geno.generators


import com.glureau.geno.annotation.CustomView
import com.glureau.geno.annotation.Image
import com.glureau.geno.utils.AndroidClasses
import com.glureau.geno.utils.AnnotationHelper.getAnnotationClassValue
import com.glureau.geno.utils.ExternalClasses
import com.glureau.geno.utils.ImportInjection
import com.squareup.kotlinpoet.*
import java.io.File
import javax.annotation.processing.Messager
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.tools.Diagnostic

/**
 * Created by Greg on 22/10/2017.
 */
class ViewManagerGenerator(val messager: Messager) {

    fun generateView(element: TypeElement) {
        val className = element.asClassName()
        val simpleClassName = className.simpleName() + "ViewManager"
        val packageName = className.packageName()
        val instanceName = className.simpleName().decapitalize()

        val R = getAnnotationClassValue(element, CustomView::class, "R")
        val viewName = element.getAnnotation(CustomView::class.java).viewName
        val kaeImport = "kotlinx.android.synthetic.main.$viewName.view.*"

        val fields = element.enclosedElements.filter { it.kind == ElementKind.FIELD }.map { it as VariableElement }

        val classBuilder = TypeSpec.classBuilder(simpleClassName)
                .primaryConstructor(FunSpec.constructorBuilder()
                        .addParameter(instanceName, className)
                        .build())
                .addProperty(PropertySpec.builder(instanceName, className)
                        .initializer(instanceName)
                        .build())
                .addFunction(fillBuilder(AndroidClasses.ACTIVITY, instanceName, fields))
                .addFunction(fillBuilder(AndroidClasses.FRAGMENT, instanceName, fields))
                .companionObject(TypeSpec.companionObjectBuilder(simpleClassName)
                        .addFunction(FunSpec.builder("inflate")
                                .addParameter("activity", AndroidClasses.ACTIVITY)
                                .addParameter("root", AndroidClasses.VIEW_GROUP.asNullable())
                                .addStatement("return activity.layoutInflater.inflate($R.layout.$viewName, root, false)")
                                .returns(AndroidClasses.VIEW)
                                .build())
                        .build())

        val file = FileSpec.builder(packageName, simpleClassName)
                .indent("    ")
                .addType(classBuilder.build())
                .build()

        val path = File("./compiler-test/build/generated/source/kapt/debug")
        file.writeTo(path)

        ImportInjection.injectImport(path.absolutePath + "/" + file.packageName.replace(".", "/") + "/" + file.name + ".kt", kaeImport)

        messager.printMessage(Diagnostic.Kind.NOTE, "Generated $simpleClassName")
    }

    private fun fillBuilder(fragmentActivity: ClassName, instanceName: String, fields: List<VariableElement>): FunSpec {
        val faName = fragmentActivity.simpleName().decapitalize()
        val viewParam = "view"
        var fillBuilder = FunSpec.builder("fill")
                .addParameter(faName, fragmentActivity)
                .addParameter(viewParam, AndroidClasses.VIEW)
        fields.forEach { field ->
            val fieldName = field.simpleName.toString()
            val fieldView = "geno_$fieldName"
            val statement: String
            val args = mutableListOf<Any>()
            messager.printMessage(Diagnostic.Kind.NOTE, "-----------------------------------------------")
            messager.printMessage(Diagnostic.Kind.NOTE, "Debugging constantValue ${field.constantValue}")
            messager.printMessage(Diagnostic.Kind.NOTE, "Debugging kind ${field.kind}")
            messager.printMessage(Diagnostic.Kind.NOTE, "Debugging asType ${field.asType()}")
            messager.printMessage(Diagnostic.Kind.NOTE, "Debugging enclosedElements ${field.enclosedElements}")
            messager.printMessage(Diagnostic.Kind.NOTE, "Debugging enclosingElement ${field.enclosingElement}")
            messager.printMessage(Diagnostic.Kind.NOTE, "Debugging modifiers ${field.modifiers}")

            val imageAnnotation = field.getAnnotation(Image::class.java)
            if (imageAnnotation != null) {
                statement = "%T.with($faName).asBitmap().load($instanceName.$fieldName).into($viewParam.$fieldView as %T)"
                args.add(ExternalClasses.GLIDE_APP)
                args.add(AndroidClasses.IMAGE_VIEW)
            } else {
                statement = "$viewParam.$fieldView.text = $instanceName.$fieldName.toString()"
            }
            fillBuilder = fillBuilder.beginControlFlow("if ($viewParam.$fieldView != null)")
                    .addStatement(statement, *args.toTypedArray())
                    .endControlFlow()
        }
        return fillBuilder.build()
    }
}
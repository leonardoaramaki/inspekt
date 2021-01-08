package inspekt

import org.jetbrains.kotlin.codegen.FunctionCodegen
import org.jetbrains.kotlin.codegen.ImplementationBodyCodegen
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin
import org.jetbrains.org.objectweb.asm.Opcodes
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter
import java.io.PrintStream
import java.lang.StringBuilder

class InspektCodegenExtension : ExpressionCodegenExtension {

    // Check whether a given class is a subclass of android.view.View or not.
    private fun DeclarationDescriptor.isViewSubClass(): Boolean {
        val superFqName = DescriptorUtils.getFqName(this).asString()
        if (superFqName == "kotlin.Any") {
            return false
        }
        if (superFqName == "android.view.View") {
            return true
        }
        if (this !is ClassDescriptor) {
            return false
        }
        val supertypes = this.typeConstructor.supertypes
        for (type in supertypes) {
            val declarationDescriptor = type.constructor.declarationDescriptor ?: break
            return declarationDescriptor.isViewSubClass()
        }
        return false
    }

    override fun generateClassSyntheticParts(codegen: ImplementationBodyCodegen) {
        val classBuilder = codegen.v
        val targetClass = codegen.myClass as? KtClass ?: return

        val container = codegen.descriptor
        if (container.kind != ClassKind.CLASS) return

        val supertypes = container.typeConstructor.supertypes
        if (supertypes.isEmpty()) return

        var viewSubClass = false
        for (type in supertypes) {
            val declarationDescriptor = type.constructor.declarationDescriptor ?: return
            viewSubClass = declarationDescriptor.isViewSubClass()
            if (viewSubClass) {
                break
            }
        }
        if (!viewSubClass) return

        val methodVisitor = classBuilder.newMethod(JvmDeclarationOrigin.NO_ORIGIN,
                Opcodes.ACC_PUBLIC + Opcodes.ACC_SYNTHETIC, METHOD_NAME, METHOD_DESC, null, null)
        methodVisitor.visitCode()
        InstructionAdapter(methodVisitor).apply {
            val fqName = container.companionObjectDescriptor?.let {
                DescriptorUtils.getFqName(it).asString()
            } ?: container.name.asString()
            visitLdcInsn(targetClass.fqName?.asString() ?: fqName)
        }
        methodVisitor.visitInsn(Opcodes.ARETURN)
        FunctionCodegen.endVisit(methodVisitor, METHOD_NAME)
    }

    companion object {
        const val METHOD_NAME = "getAccessibilityClassName"
        const val METHOD_DESC = "()Ljava/lang/CharSequence;"
    }
}
package vg.h2o.asm

import com.ldhdev.asmhelper.*
import com.ldhdev.asmhelper.compiler.newArray
import org.objectweb.asm.*
import org.objectweb.asm.Opcodes.*
import java.io.File
import java.lang.instrument.ClassFileTransformer
import java.lang.instrument.Instrumentation
import java.security.ProtectionDomain

object PluginAgent {

    @JvmStatic
    fun premain(args: String?, inst: Instrumentation) {
        inst.addTransformer(PluginClassTransformer())
    }

}

private class PluginClassTransformer : ClassFileTransformer {
    override fun transform(
        loader: ClassLoader?,
        className: String?,
        classBeingRedefined: Class<*>?,
        protectionDomain: ProtectionDomain?,
        classfileBuffer: ByteArray?
    ): ByteArray {


        val listType = Type.getType("Ljava/util/List;")

        if (className == "net/minecraft/commands/arguments/selector/EntitySelector") {
            runCatching {
                val reader = ClassReader(classfileBuffer)
                val writer = ClassWriter(reader, ClassWriter.COMPUTE_MAXS or ClassWriter.COMPUTE_FRAMES)

                reader.accept(object : ClassVisitor(ASM9, writer) {

                    override fun visit(
                        version: Int,
                        access: Int,
                        name: String?,
                        signature: String?,
                        superName: String?,
                        interfaces: Array<out String>?
                    ) {
                        super.visit(version, access, name, signature, superName, interfaces)

                        visitField(ACC_PUBLIC + ACC_STATIC, "dimibug", "Ljava/lang/reflect/Method;", null, null)
                    }

                    override fun visitMethod(
                        access: Int,
                        name: String,
                        descriptor: String,
                        signature: String?,
                        exceptions: Array<out String>?
                    ): MethodVisitor {

                        val visitor = super.visitMethod(access, name, descriptor, signature, exceptions)

                        if (name == "d" && Type.getReturnType(descriptor) == listType) {
                            return object : MethodVisitor(ASM9, visitor) {
                                override fun visitCode() {
                                    super.visitCode()

                                    getStatic(
                                        "net/minecraft/commands/arguments/selector/EntitySelector",
                                        "dimibug",
                                        "Ljava/lang/reflect/Method;"
                                    )
                                    insn(ACONST_NULL)
                                    newArray<String>(1) {
                                        aadd {
                                            `this`()
                                            getField(
                                                "net/minecraft/commands/arguments/selector/EntitySelector",
                                                "m",
                                                "Ljava/lang/String;"

                                            )
                                        }
                                    }
                                    invokeVirtual(
                                        "java/lang/reflect/Method",
                                        "invoke",
                                        "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;",
                                    )
                                    astore(2)

                                    ifClause {
                                        condition(IFNULL) {
                                            aload(2)
                                        }

                                        code {
                                            aload(2)
                                            areturn()
                                        }
                                    }
                                }
                            }
                        }

                        return visitor
                    }
                }, ClassReader.EXPAND_FRAMES)

                return writer.toByteArray().also {
                    File("${className.replace("/", ".")}.class").writeBytes(it)
                }
            }.onFailure {
                it.printStackTrace()
            }
        }

        return super.transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer)
    }
}
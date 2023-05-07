package vg.h2o.asm

import com.google.auto.service.AutoService
import com.ldhdev.asmhelper.*
import com.ldhdev.asmhelper.compiler.newArray
import org.objectweb.asm.*

@AutoService(ITransformer::class)
class EntitySelectorTransformer : ITransformer {

    override val className = "net/minecraft/commands/arguments/selector/EntitySelector"


    private val listType = Type.getType("Ljava/util/List;")

    override fun transform(writer: ClassWriter): ClassVisitor {
        return object : ClassVisitor(Opcodes.ASM9, writer) {

            override fun visit(
                version: Int,
                access: Int,
                name: String?,
                signature: String?,
                superName: String?,
                interfaces: Array<out String>?
            ) {
                super.visit(version, access, name, signature, superName, interfaces)

                visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "dimibug", "Ljava/lang/reflect/Method;", null, null)
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
                    return object : MethodVisitor(Opcodes.ASM9, visitor) {
                        override fun visitCode() {
                            super.visitCode()

                            getStatic(
                                "net/minecraft/commands/arguments/selector/EntitySelector",
                                "dimibug",
                                "Ljava/lang/reflect/Method;"
                            )
                            insn(Opcodes.ACONST_NULL)
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
                                condition(Opcodes.IFNULL) {
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
        }
    }
}
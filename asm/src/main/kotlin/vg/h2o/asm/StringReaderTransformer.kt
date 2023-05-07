package vg.h2o.asm

import com.google.auto.service.AutoService
import com.ldhdev.asmhelper.*
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*

@AutoService(ITransformer::class)
class StringReaderTransformer : ITransformer {

    override val className = "com/mojang/brigadier/StringReader"

    override fun transform(writer: ClassWriter) = object : ClassVisitor(ASM9, writer) {
        override fun visitMethod(
            access: Int,
            name: String?,
            descriptor: String?,
            signature: String?,
            exceptions: Array<out String>?
        ): MethodVisitor {
            val visitor = super.visitMethod(access, name, descriptor, signature, exceptions)

            return if (name == "isAllowedInUnquotedString") {
                object : MethodVisitor(ASM9, visitor) {
                    override fun visitCode() {
                        super.visitCode()

                        fun makeIf(a: Char, b: Char) {
                            ifClause {
                                condition(IF_ICMPLT) {
                                    iload(0)
                                    int(a.code)
                                }

                                condition(IF_ICMPGT) {
                                    iload(0)
                                    int(b.code)
                                }

                                code {
                                    boolean(true)
                                    ireturn()
                                }
                            }
                        }

                        makeIf('가', '힣')
                        makeIf('ㄱ', 'ㅎ')
                        makeIf('ㅏ', 'ㅣ')
                    }
                }
            } else visitor
        }
    }
}
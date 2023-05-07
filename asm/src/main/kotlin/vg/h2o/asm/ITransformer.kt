package vg.h2o.asm

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

interface ITransformer {

    val className: String

    fun transform(writer: ClassWriter): ClassVisitor
}
package vg.h2o.asm

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.File
import java.lang.instrument.ClassFileTransformer
import java.lang.instrument.Instrumentation
import java.security.ProtectionDomain
import java.util.*

private val dir = File("bytecodes").apply {
    deleteRecursively()
    mkdirs()
}

object PluginAgent {

    @JvmStatic
    fun premain(args: String?, inst: Instrumentation) {
        inst.addTransformer(PluginClassTransformer())
    }

}

private class PluginClassTransformer : ClassFileTransformer {

    private fun writeBytecode(className: String, bytes: ByteArray) {
        File(dir, "${className.replace("/", ".")}.class").writeBytes(bytes)
    }

    private val transformers by lazy {
        ServiceLoader.load(ITransformer::class.java).associateBy { it.className }
    }

    override fun transform(
        loader: ClassLoader?,
        className: String,
        classBeingRedefined: Class<*>?,
        protectionDomain: ProtectionDomain?,
        classfileBuffer: ByteArray
    ): ByteArray {
        val transformer = transformers[className]

        if (transformer != null) {
            runCatching {
                val reader = ClassReader(classfileBuffer)
                val writer = ClassWriter(reader, ClassWriter.COMPUTE_MAXS or ClassWriter.COMPUTE_FRAMES)

                reader.accept(transformer.transform(writer), ClassReader.EXPAND_FRAMES)

                return writer.toByteArray().also {
                    writeBytecode(className, it)
                }
            }.onFailure {
                it.printStackTrace()
            }
        }

        return super.transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer)
    }
}
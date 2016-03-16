package ru.ensemplix.deobf;

import org.objectweb.asm.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class DeobfuscateImpl implements Deobfuscate {

    private final Map<String, String> mappings;
    private ClassWriter writer;

    public DeobfuscateImpl(Map<String, String> mappings) {
        this.mappings = mappings;
    }

    @Override
    public void read(InputStream in) throws IOException {
        ClassReader reader = new ClassReader(in);
        writer = new ClassWriter(reader, 0);
        reader.accept(new DeobfuscateClassVisitor(writer), 0);
    }

    @Override
    public void write(OutputStream out) throws IOException {
        out.write(writer.toByteArray());
    }

    public class DeobfuscateClassVisitor extends ClassVisitor {

        public DeobfuscateClassVisitor(ClassWriter writer) {
            super(Opcodes.ASM5, writer);
        }

        @Override
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            if(mappings.containsKey(name)) {
                name = mappings.get(name);
            }

            return super.visitField(access, name, desc, signature, value);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if(mappings.containsKey(name)) {
                name = mappings.get(name);
            }

            return new DeobfuscateMethodVisitor(super.visitMethod(access, name, desc, signature, exceptions));
        }

    }

    public class DeobfuscateMethodVisitor extends MethodVisitor {

        public DeobfuscateMethodVisitor(MethodVisitor origin) {
            super(Opcodes.ASM5, origin);
        }

        @Override
        public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
            if(mappings.containsKey(name)) {
                name = mappings.get(name);
            }

            super.visitLocalVariable(name, desc, signature, start, end, index);
        }

        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
            if(mappings.containsKey(name)) {
                name = mappings.get(name);
            }

            super.visitFieldInsn(opcode, owner, name, desc);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            if(mappings.containsKey(name)) {
                name = mappings.get(name);
            }

            super.visitMethodInsn(opcode, owner, name, desc, itf);
        }

    }

}

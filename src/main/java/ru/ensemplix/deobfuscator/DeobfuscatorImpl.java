package ru.ensemplix.deobfuscator;

import org.objectweb.asm.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import static ru.ensemplix.deobfuscator.Deobfuscator.Type.FIELD;
import static ru.ensemplix.deobfuscator.Deobfuscator.Type.METHOD;
import static ru.ensemplix.deobfuscator.Deobfuscator.Type.PARAM;

public class DeobfuscatorImpl implements Deobfuscator {

    private final Map<Type, Result> results = new HashMap<>();
    private final Map<String, Type> patterns = new HashMap<>();
    private final Map<String, String> mappings;
    private ClassWriter writer;

    public DeobfuscatorImpl(Map<String, String> mappings) {
        this.mappings = mappings;

        patterns.put("field", FIELD);
        patterns.put("func", METHOD);
        patterns.put("p", PARAM);

        for(Type type : Type.values()) {
            results.put(type, new Result());
        }
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

    @Override
    public Map<Type, Result> getResults() {
        return results;
    }

    private String mapping(String name) {
        int index = name.indexOf("_");
        Type type = null;

        if(index > 0) {
            type = patterns.get(name.substring(0, index));
        }

        if(type != null) {
            Result result = results.get(type);
            result.total++;

            if (mappings.containsKey(name)) {
                result.count++;
                return mappings.get(name);
            }
        }

        return name;
    }

    public class DeobfuscateClassVisitor extends ClassVisitor {

        public DeobfuscateClassVisitor(ClassWriter writer) {
            super(Opcodes.ASM5, writer);
        }

        @Override
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            return super.visitField(access, mapping(name), desc, signature, value);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] ex) {
            return new DeobfuscateMethodVisitor(super.visitMethod(access, mapping(name), desc, sig, ex));
        }

    }

    public class DeobfuscateMethodVisitor extends MethodVisitor {

        public DeobfuscateMethodVisitor(MethodVisitor origin) {
            super(Opcodes.ASM5, origin);
        }

        @Override
        public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
            super.visitLocalVariable(mapping(name), desc, signature, start, end, index);
        }

        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
            super.visitFieldInsn(opcode, owner, mapping(name), desc);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            super.visitMethodInsn(opcode, owner, mapping(name), desc, itf);
        }

    }

}

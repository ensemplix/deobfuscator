package ru.ensemplix.deobf;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class DeobfuscateImplTest {

    @Test
    @SuppressWarnings("unchecked")
    public void testDeobfuscate() throws IOException {
        Map<String, String> mappings = new HashMap<>();
        mappings.put("a", "integer");
        mappings.put("b", "bool");
        mappings.put("c", "string");
        mappings.put("a2", "integer2");

        Deobfuscate deobfuscate = new DeobfuscateImpl(mappings);
        deobfuscate.read(getClass().getResourceAsStream("/ru/ensemplix/Dummy.class"));
        byte[] bytes;

        try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            deobfuscate.write(out);
            bytes = out.toByteArray();
        }

        ClassReader reader = new ClassReader(bytes);
        ClassNode classNode = new ClassNode();
        reader.accept(classNode, 0);

        List<FieldNode> fields = classNode.fields;

        assertEquals("integer", fields.get(0).name);
        assertEquals("bool", fields.get(1).name);
        assertEquals("string", fields.get(2).name);

        MethodNode method = (MethodNode) classNode.methods.get(1);
        MethodNode method2 = (MethodNode) classNode.methods.get(2);

        assertEquals("integer", method.name);
        assertEquals("bool", method2.name);
        assertEquals("integer2", ((LocalVariableNode) method.localVariables.get(1)).name);
        assertEquals("bool", ((LocalVariableNode) method2.localVariables.get(1)).name);

        InsnList instructions = method.instructions;
        InsnList instructions2 = method2.instructions;

        assertEquals("integer", ((FieldInsnNode) instructions.get(4)).name);
        assertEquals("bool", ((MethodInsnNode) instructions.get(9)).name);
        assertEquals("bool", ((FieldInsnNode) instructions2.get(4)).name);
    }

}

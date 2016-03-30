package ru.ensemplix;

import com.google.common.collect.MapDifference.ValueDifference;
import com.google.common.collect.Maps;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.bundling.Jar;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import static com.google.common.base.Preconditions.checkArgument;

public class PatcherAction implements Action<Task> {

    @Override
    public void execute(Task task) {
        Project project = task.getProject();

        Jar jar = (Jar) project.getTasks().getByName("jar");
        PatcherExtension extension = project.getExtensions().findByType(PatcherExtension.class);
        checkArgument(extension != null, "No configuration provided");
        String archive = jar.getArchiveName();

        Path base = Paths.get(extension.getBase());
        Path compiled = Paths.get(jar.getArchiveName());
        Path deobf = Paths.get(extension.getDeobf());

        Map<String, Long> deobfClasses = getJarClasses(deobf);
        Map<String, Long> compiledClasses = getJarClasses(compiled);
        Map<String, ValueDifference<Long>> diff = Maps.difference(deobfClasses, compiledClasses).entriesDiffering();
        Path patched = Paths.get(archive.substring(0, archive.lastIndexOf(".")) + "-patched.jar");

        try {
            if(Files.notExists(patched)) {
                Files.createFile(patched);
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }

        patch(base, compiled, diff.keySet(), false);
        patch(deobf, compiled, diff.keySet(), true);
    }

    private Map<String, Long> getJarClasses(Path path) {
        Map<String, Long> info = new HashMap<>();

        try(JarFile jar = new JarFile(path.toFile())) {
            Enumeration<? extends JarEntry> entries = jar.entries();

            while(entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();

                if(entry.isDirectory() || !entry.getName().endsWith(".class")) {
                    continue;
                }

                info.put(entry.getName(), entry.getSize());
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }

        return info;
    }

    private void patch(Path path, Path dest, Set<String> classes, boolean contains) {
        try(ZipOutputStream out = new ZipOutputStream(new FileOutputStream(dest.toFile()))) {
            try(ZipFile zip = new ZipFile(path.toFile())) {
                Enumeration<? extends ZipEntry> entries = zip.entries();

                while(entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    String name = entry.getName();

                    if(contains && !classes.contains(name)) {
                        continue;
                    }

                    if(!contains && classes.contains(name)) {
                        continue;
                    }

                    out.putNextEntry(new ZipEntry(name));

                    try(InputStream in = zip.getInputStream(entry)) {
                        byte[] bytes = new byte[1024];
                        int count;

                        while ((count = in.read(bytes)) > 0) {
                            out.write(bytes, 0, count);
                        }
                    }
                }
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

}

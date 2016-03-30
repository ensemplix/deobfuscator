package ru.ensemplix;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

public class PatcherPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getExtensions().create("patcher", PatcherExtension.class);
        Task task = project.getTasks().getByName("jar");
        task.doLast(new PatcherAction());
    }

}

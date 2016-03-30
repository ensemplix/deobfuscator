package ru.ensemplix;

import lombok.Data;

@Data
public class PatcherExtension {

    /**
     * Мод до декомпиляции.
     */
    private String base;

    /**
     * Мод после декомпиляии и исправления ошибок компиляции.
     */
    private String deobf;

}

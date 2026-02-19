package com.project.demo.Entity.enums;

public enum ChatEventType {
    THOUGHT, // ex though for 2s
    MESSAGE, // Standard conversation text
    FILE_EDIT, // Code generation <file>
    TOOL_LOG // "Reading file..." <tool>
}

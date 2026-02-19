package com.project.demo.dto.project;

import java.time.Instant;

public record FileNode(
        String path
) {

    @Override
    public String toString()
    {
//        return "FileNode{"+ "path='" +path +'\'' +'}'; // instead of this just pass path so as to not confuse the llm

        return path;
    }

}

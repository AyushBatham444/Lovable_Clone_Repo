package com.project.demo.llm.tools;

import com.project.demo.Service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class CodeGenerationTools {

    private final ProjectFileService projectFileService;
    private final Long projectId;


    // We create this tool so that llm could read the file content
    @Tool(name = "read_files",
   description = "Read the content of files. Only input the file names present inside the FILE_TREE. DO NOT input any path which is not present under the FILE_TREE.")
    public List<String> readFiles(
            @ToolParam(description = "List of relative paths (e.g., ['src/App.tsx'])")
            List<String> paths
    ){
        List<String> result=new ArrayList<>();
        for(String path:paths)
        {
            String cleanPath=path.startsWith("/")?path.substring(1):path; // to remove the / if it starts with one

            log.info("Requested file:{}",cleanPath);
            String content=projectFileService.getFileContent(projectId,cleanPath).content();

            result.add(String.format(
                    "--- START OF FILE: %s ---\n%s\n--- END OF FILE ---",
                    cleanPath,content
            ));
        }
        return result;
    }
}

package com.project.demo.Service;

import com.project.demo.dto.project.FileContentResponse;
import com.project.demo.dto.project.FileNode;
import com.project.demo.dto.project.FileTreeResponse;

import java.util.List;

public interface ProjectFileService {
     FileTreeResponse getFileTree(Long projectId);

     FileContentResponse getFileContent(Long projectId, String path);

    void saveFile(Long projectId, String filePath, String fileContent);
}

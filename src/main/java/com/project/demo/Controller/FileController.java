package com.project.demo.Controller;

import com.project.demo.Service.ProjectFileService;
import com.project.demo.dto.project.FileContentResponse;
import com.project.demo.dto.project.FileNode;
import com.project.demo.dto.project.FileTreeResponse;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor // generate objects using constructor now you dont need to write them
@RequestMapping("/api/projects/{projectId}/files")
public class FileController {

    private final ProjectFileService fileServiceObj;

    @GetMapping()
    public ResponseEntity<FileTreeResponse>  getFileTree(@PathVariable Long projectId){

        return ResponseEntity.ok(fileServiceObj.getFileTree(projectId));
    }

    @GetMapping("/content") // * so that I can also get /src/hooks/AppHook.jsx //this get mapping will only work if there is something before path
    public ResponseEntity<FileContentResponse> getFile(
            @PathVariable Long projectId,
            @RequestParam String path
    )
    {

        return ResponseEntity.ok(fileServiceObj.getFileContent(projectId,path));
    }

}

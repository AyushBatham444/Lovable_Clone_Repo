package com.project.demo.Service.Impl;

import com.project.demo.Entity.Project;
import com.project.demo.Entity.ProjectFile;
import com.project.demo.Service.ProjectFileService;
import com.project.demo.dto.project.FileContentResponse;
import com.project.demo.dto.project.FileNode;
import com.project.demo.dto.project.FileTreeResponse;
import com.project.demo.errors.ResourceNotFoundException;
import com.project.demo.mapper.ProjectFileMapper;
import com.project.demo.repository.ProjectFileRepository;
import com.project.demo.repository.ProjectRepository;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectFileServiceImpl implements ProjectFileService {

    private final ProjectFileRepository projectFileRepository;
    private final ProjectRepository projectRepository;
    private final MinioClient minioClient;
    private final ProjectFileMapper projectFileMapper;
    private static final String BUCKET_NAME= "projects";

    @Value("${minio.project-bucket}")
    private String projectBucket;


    @Override
    public FileTreeResponse getFileTree(Long projectId) {
        List<ProjectFile> projectFileList=projectFileRepository.findByProjectId(projectId);
        List<FileNode> projectFileNodes = projectFileMapper.toListOfFileNode(projectFileList);
        return new FileTreeResponse(projectFileNodes);
    }

    @Override
    public FileContentResponse getFileContent(Long projectId, String path) {
        String objectName = projectId + "/" + path;
        try (
                InputStream is = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(BUCKET_NAME)
                                .object(objectName)
                                .build())) {

            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return new FileContentResponse(path, content);
        } catch (Exception e) {
            log.error("Failed to read file: {}/{}", projectId, path, e);
            throw new RuntimeException("Failed to read file content", e);
        }
    }

    @Override
    public void saveFile(Long projectId, String path, String content) {

        Project project=projectRepository.findById(projectId).orElseThrow(
                ()-> new ResourceNotFoundException("Project",projectId.toString())
        );

        //Now get the path of the file remove starting / if it was put by llm first
        String cleanPath= path.startsWith("/")?path.substring(1):path;
        String objectKey=projectId+"/"+cleanPath; // ex 2/src/App.jsx etc

        try { // got from minio doc
            byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8); // converting content to byte stream so that u can even process images
            InputStream inputStream = new ByteArrayInputStream(contentBytes); // then convert to input stream
            // saving the file content in minio
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(projectBucket)
                            .object(objectKey)
                            .stream(inputStream, contentBytes.length, -1)
                            .contentType(determineContentType(path))
                            .build());

            // Saving the metaData
            ProjectFile file = projectFileRepository.findByProjectIdAndPath(projectId, cleanPath)
                    .orElseGet(() -> ProjectFile.builder() // if it exist we get it else run .builder() and generate the new file
                            .project(project)
                            .path(cleanPath)
                            .minioObjectKey(objectKey) // Use the key we generated
                            .createdAt(Instant.now())
                            .build());

            file.setUpdatedAt(Instant.now()); // if file already exist then just update the updated file
            projectFileRepository.save(file);

            log.info("Saved file: {}", objectKey);
        } catch (Exception e) {
            log.error("Failed to save file {}/{}", projectId, cleanPath, e);
            throw new RuntimeException("File save failed", e);
        }



    }
    private String determineContentType(String path) {
        String type = URLConnection.guessContentTypeFromName(path);
        if (type != null) return type;
        if (path.endsWith(".jsx") || path.endsWith(".ts") || path.endsWith(".tsx")) return "text/javascript";
        if (path.endsWith(".json")) return "application/json";
        if (path.endsWith(".css")) return "text/css";

        return "text/plain";
    }
}

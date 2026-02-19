package com.project.demo.Service.Impl;

import com.project.demo.Entity.Project;
import com.project.demo.Entity.ProjectFile;
import com.project.demo.Service.ProjectTemplateService;
import com.project.demo.errors.ResourceNotFoundException;
import com.project.demo.repository.ProjectFileRepository;
import com.project.demo.repository.ProjectRepository;
import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProjectTemplateServiceImp implements ProjectTemplateService {

    private final MinioClient minioClient;
    private final ProjectFileRepository projectFileRepository;
    private final ProjectRepository projectRepository;

    private static final String TEMPLATE_BUCKET="starter-projects"; // name of bucket containing template in minio
    private static final String TARGET_BUCKET="projects"; // name of bucket where my real ai generated project is
    private static final String TEMPLATE_NAME="react-vite-tailwind-daisyui-starter"; // name of folder in which template code is

    @Override
    public void initializeProjectFromTemplate(Long projectId) {

        // here we will basically puting the template inside the project buckets for every new project created
        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new ResourceNotFoundException("Project", projectId.toString()));

        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(TEMPLATE_BUCKET)
                            .prefix(TEMPLATE_NAME + "/")
                            .recursive(true) // this means it will take all files inside every folder and subfolder not just depth 1 but all depth of file
                            .build()
            );

            List<ProjectFile> filesToSave = new ArrayList<>(); // for metadata in postgres db

            for (Result<Item> result : results) {
                Item item = result.get();
                String sourceKey = item.objectName();

                String cleanPath = sourceKey.replaceFirst(TEMPLATE_NAME + "/", "");
                String destKey = projectId + "/" + cleanPath;

                minioClient.copyObject( // copying happen here
                        CopyObjectArgs.builder()
                                .bucket(TARGET_BUCKET) // the destination of copy
                                .object(destKey)
                                .source( // the source of copying
                                        CopySource.builder()
                                                .bucket(TEMPLATE_BUCKET)
                                                .object(sourceKey)
                                                .build()
                                )
                                .build()
                );

                ProjectFile pf = ProjectFile.builder()
                        .project(project)
                        .path(cleanPath)
                        .minioObjectKey(destKey)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();

                filesToSave.add(pf);
            }

            projectFileRepository.saveAll(filesToSave);

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize project from template", e);
        }

    }
}

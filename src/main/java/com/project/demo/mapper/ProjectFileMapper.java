package com.project.demo.mapper;

import com.project.demo.Entity.ProjectFile;
import com.project.demo.dto.project.FileNode;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectFileMapper {

    List<FileNode> toListOfFileNode(List<ProjectFile> projectFileList);
}

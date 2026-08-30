package com.eduflow.backend.dto;

public class CourseDto {
    private Long id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private int moduleCount;
    private int progressPercentage;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    public int getModuleCount() { return moduleCount; }
    public void setModuleCount(int moduleCount) { this.moduleCount = moduleCount; }
    public int getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(int progressPercentage) { this.progressPercentage = progressPercentage; }
}

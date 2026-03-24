package com.uniintern.model;

public class CourseInfo {
    private String id;
    private String title;
    private String youtubeId;
    private String description;
    private String icon;
    private String duration;

    public CourseInfo(String id, String title, String youtubeId, String description, String icon, String duration) {
        this.id = id;
        this.title = title;
        this.youtubeId = youtubeId;
        this.description = description;
        this.icon = icon;
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYoutubeId() {
        return youtubeId;
    }

    public void setYoutubeId(String youtubeId) {
        this.youtubeId = youtubeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}

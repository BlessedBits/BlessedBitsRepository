package com.blessedbits.SchoolHub.misc;

import lombok.Getter;

@Getter
public enum CloudFolder {
    MISC_FILES("miscFiles"),
    PROFILE_IMAGES("profileImages"),
    SCHOOL_IMAGES("schoolImages"),
    SCHOOL_GALLERIES("schoolGalleries"),
    NEWS_IMAGES("newsImages"),
    TASK_SUBMISSIONS("taskSubmissions");

    private final String folderName;

    CloudFolder(String folderName) {
        this.folderName = folderName;
    }


    @Override
    public String toString() {
        return getFolderName();
    }
}


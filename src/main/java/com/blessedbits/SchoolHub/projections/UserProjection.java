package com.blessedbits.SchoolHub.projections;

import java.util.List;

public interface UserProjection {
    String getUsername();
    ClassProjection getUserClass();

    interface ClassProjection {
        String getName();
        List<CourseProjection> getCourses();
    }

    interface CourseProjection {
        String getName();
    }
}

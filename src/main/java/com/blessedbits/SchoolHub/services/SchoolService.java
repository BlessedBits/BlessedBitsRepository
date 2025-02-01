package com.blessedbits.SchoolHub.services;

import com.blessedbits.SchoolHub.dto.SchoolContactsDto;
import com.blessedbits.SchoolHub.dto.SchoolInfoDto;
import com.blessedbits.SchoolHub.dto.TeacherInfoDto;
import com.blessedbits.SchoolHub.misc.CloudFolder;
import com.blessedbits.SchoolHub.dto.AchievementDto;
import com.blessedbits.SchoolHub.models.Achievement;
import com.blessedbits.SchoolHub.models.School;
import com.blessedbits.SchoolHub.models.SchoolContacts;
import com.blessedbits.SchoolHub.models.UserEntity;
import com.blessedbits.SchoolHub.repositories.SchoolContactsRepository;
import com.blessedbits.SchoolHub.repositories.SchoolRepository;
import com.blessedbits.SchoolHub.repositories.UserRepository;
import com.blessedbits.SchoolHub.repositories.AchievementRepository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SchoolService {

    @Autowired
    private SchoolContactsRepository schoolContactsRepository;
    @Autowired
    private SchoolRepository schoolRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AchievementRepository achievementRepository;
    @Autowired
    private StorageService storageService;

    public SchoolContactsDto getSchoolContacts(Integer schoolId) {
        SchoolContacts schoolContacts = schoolContactsRepository.findBySchoolId(schoolId)
                .orElseThrow(() -> new RuntimeException("School contacts not found"));

        SchoolContactsDto dto = new SchoolContactsDto();
        dto.setPhoneNumber(schoolContacts.getPhoneNumber());
        dto.setEmail(schoolContacts.getEmail());
        dto.setYoutubeLink(schoolContacts.getYoutubeLink());
        dto.setFacebookLink(schoolContacts.getFacebookLink());
        dto.setInstagramLink(schoolContacts.getInstagramLink());
        dto.setTiktokLink(schoolContacts.getTiktokLink());

        return dto;
    }

    public void updateSchoolContacts(Integer schoolId, SchoolContactsDto schoolContactsDto) {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new RuntimeException("School not found"));

        SchoolContacts contacts = school.getContacts();
        if (contacts == null) {
            contacts = new SchoolContacts();
            school.setContacts(contacts);  
        }
        contacts.setPhoneNumber(schoolContactsDto.getPhoneNumber());
        contacts.setEmail(schoolContactsDto.getEmail());
        contacts.setYoutubeLink(schoolContactsDto.getYoutubeLink());
        contacts.setFacebookLink(schoolContactsDto.getFacebookLink());
        contacts.setInstagramLink(schoolContactsDto.getInstagramLink());
        contacts.setTiktokLink(schoolContactsDto.getTiktokLink());
        contacts.setSchool(school);
        schoolContactsRepository.save(contacts);
        
        schoolRepository.save(school);
    }

    public SchoolInfoDto getSchoolInfo(Integer schoolId) 
    {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new RuntimeException("School not found"));

        long studentCount = userRepository.countBySchoolIdAndRole(schoolId, "STUDENT");
        long teacherCount = userRepository.countBySchoolIdAndRole(schoolId, "TEACHER");

        SchoolInfoDto dto = new SchoolInfoDto();
        dto.setName(school.getName());
        dto.setAddress(school.getAddress());
        dto.setLogo(school.getLogo());
        dto.setStudentCount(studentCount);
        dto.setTeacherCount(teacherCount);
        
        return dto;
    }

    private TeacherInfoDto convertToTeacherInfoDto(UserEntity teacher) {
        TeacherInfoDto dto = new TeacherInfoDto();
        dto.setFirstName(teacher.getFirstName());
        dto.setLastName(teacher.getLastName());
        dto.setProfileImage(teacher.getProfileImage());
        dto.setDuty(teacher.getDuty());
        dto.setRole(teacher.getRoles().get(0).getName());

        return dto;
    }

    public List<TeacherInfoDto> getTeachersBySchool(Integer schoolId) {
        List<UserEntity> teachers = userRepository.findTeachersBySchoolId(schoolId);
        return teachers.stream().map(this::convertToTeacherInfoDto).collect(Collectors.toList());
    }

    public Achievement createAchievement(Integer schoolId, MultipartFile image, AchievementDto achievementDto)
    {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new RuntimeException("School not found"));
        Achievement achievement = new Achievement();
        achievement.setTitle(achievementDto.getTitle());
        achievement.setDescription(achievementDto.getDescription());
        achievement.setSchool(school);
        try {
            String url = storageService.uploadFile(image, CloudFolder.ACHIEVEMENT_IMAGES);
            achievement.setImage(url);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image: " + e.getMessage(), e);
        }
        return achievementRepository.save(achievement);
    }

    public List<Achievement> getAchievementsBySchool(int schoolId) {
        return achievementRepository.findBySchoolId(schoolId);
    }

}

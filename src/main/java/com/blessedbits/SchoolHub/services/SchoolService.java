package com.blessedbits.SchoolHub.services;

import com.blessedbits.SchoolHub.dto.SchoolContactsDto;
import com.blessedbits.SchoolHub.dto.SchoolInfoDto;
import com.blessedbits.SchoolHub.models.School;
import com.blessedbits.SchoolHub.models.SchoolContacts;
import com.blessedbits.SchoolHub.repositories.SchoolContactsRepository;
import com.blessedbits.SchoolHub.repositories.SchoolRepository;
import com.blessedbits.SchoolHub.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SchoolService {

    @Autowired
    private SchoolContactsRepository schoolContactsRepository;
    @Autowired
    private SchoolRepository schoolRepository;
    @Autowired
    private UserRepository userRepository;

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

}

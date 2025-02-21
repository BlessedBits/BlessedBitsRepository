package com.blessedbits.SchoolHub.services;

import com.blessedbits.SchoolHub.misc.EntityManagerUtils;
import com.blessedbits.SchoolHub.misc.RoleType;
import com.blessedbits.SchoolHub.models.*;
import com.blessedbits.SchoolHub.projections.dto.SchoolDto;
import com.blessedbits.SchoolHub.projections.mappers.SchoolMapper;
import com.blessedbits.SchoolHub.repositories.SchoolRepository;
import com.blessedbits.SchoolHub.repositories.SchoolContactsRepository;
import com.blessedbits.SchoolHub.repositories.SchoolGalleryRepository;
import com.blessedbits.SchoolHub.repositories.AchievementRepository;
import com.blessedbits.SchoolHub.repositories.UserRepository;
import com.blessedbits.SchoolHub.dto.SchoolContactsDto;
import com.blessedbits.SchoolHub.dto.SchoolInfoDto;
import com.blessedbits.SchoolHub.dto.TeacherInfoDto;
import com.blessedbits.SchoolHub.dto.UpdateSchoolInfoDto;
import com.blessedbits.SchoolHub.misc.CloudFolder;
import com.blessedbits.SchoolHub.dto.AchievementDto;
import com.blessedbits.SchoolHub.dto.CreateSchoolDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Service
public class SchoolService {
    private final SchoolRepository schoolRepository;
  
    @Autowired
    private SchoolContactsRepository schoolContactsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AchievementRepository achievementRepository;
    @Autowired
    private StorageService storageService;
    @Autowired
    private SchoolGalleryRepository schoolGalleryRepository;
    @Autowired
    private UserService userService;

    @PersistenceContext
    private EntityManager entityManager;

    public SchoolService(SchoolRepository schoolRepository) {
        this.schoolRepository = schoolRepository;
    }

    public School getById(Integer id) {
        return schoolRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "School with given id not found")
        );
    }

    public School getLoadedById(Integer id, List<String> include) {
        String jpql = "SELECT s FROM School s WHERE s.id = :id";
        TypedQuery<School> query = EntityManagerUtils
                .createTypedQueryWithGraph(School.class, entityManager, jpql, include);
        query.setParameter("id", id);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "School with given id not found");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return getById(id);
        }
    }

    public School getByIdOrUser(Integer id, UserEntity user) {
        if (id != 0) {
            return getById(id);
        }
        return user.getSchool();
    }

    public List<School> getAllLoaded(List<String> include) {
        String jpql = "select s from School s";
        TypedQuery<School> query = EntityManagerUtils
                .createTypedQueryWithGraph(School.class, entityManager, jpql, include);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No results found for the query");
        } catch (Exception e) {
            System.out.println("Couldn't execute query with entity graph");
            return schoolRepository.findAll();
        }
    }

    public List<UserEntity> getSchoolUsersLoaded(Integer id, List<String> include) {
        String jpql = "select s.users from School s where s.id = :id";
        TypedQuery<UserEntity> query = EntityManagerUtils
                .createTypedQueryWithGraph(UserEntity.class, entityManager, jpql, include);
        query.setParameter("id", id);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No results found for the query");
        } catch (Exception e) {
            System.out.println("Couldn't execute query with entity graph");
            return getById(id).getUsers().stream().toList();
        }
    }

    public List<ClassEntity> getSchoolClassesLoaded(Integer id, List<String> include) {
        String jpql = "select s.classes from School s where s.id = :id";
        TypedQuery<ClassEntity> query = EntityManagerUtils
                .createTypedQueryWithGraph(ClassEntity.class, entityManager, jpql, include);
        query.setParameter("id", id);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No results found for the query");
        } catch (Exception e) {
            System.out.println("Couldn't execute query with entity graph");
            return getById(id).getClasses().stream().toList();
        }
    }

    public List<Course> getSchoolCoursesLoaded(Integer id, List<String> include) {
        String jpql = "select s.courses from School s where s.id = :id";
        TypedQuery<Course> query = EntityManagerUtils
                .createTypedQueryWithGraph(Course.class, entityManager, jpql, include);
        query.setParameter("id", id);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No results found for the query");
        } catch (Exception e) {
            System.out.println("Couldn't execute query with entity graph");
            return getById(id).getCourses().stream().toList();
        }
    }

    public List<News> getSchoolNewsLoaded(Integer id, List<String> include) {
        String jpql = "select s.news from School s where s.id = :id";
        TypedQuery<News> query = EntityManagerUtils
                .createTypedQueryWithGraph(News.class, entityManager, jpql, include);
        query.setParameter("id", id);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No results found for the query");
        } catch (Exception e) {
            System.out.println("Couldn't execute query with entity graph");
            return getById(id).getNews().stream().toList();
        }
    }

    public List<SchoolDto> getAllAsDto(List<String> include) {
        return getAllLoaded(include).stream()
                .map(school -> SchoolMapper.INSTANCE.toSchoolDto(school, include))
                .toList();
    }

    public List<SchoolDto> mapAllToDto(Set<School> entities, List<String> include) {
        return entities.stream()
                .map(entity -> SchoolMapper.INSTANCE.toSchoolDto(entity, include))
                .toList();
    }

    public List<SchoolDto> mapAllToDto(List<School> entities, List<String> include) {
        return entities.stream()
                .map(entity -> SchoolMapper.INSTANCE.toSchoolDto(entity, include))
                .toList();
    }

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

        long studentCount = userRepository.countBySchoolIdAndRole(schoolId, RoleType.STUDENT);
        long teacherCount = userRepository.countBySchoolIdAndRole(schoolId, RoleType.TEACHER);

        SchoolInfoDto dto = new SchoolInfoDto();
        dto.setName(school.getName());
        dto.setAddress(school.getAddress());
        dto.setLogo(school.getLogo());
        dto.setPhrase(school.getPhrase());
        dto.setYear(school.getYear());
        dto.setStudentCount(studentCount);
        dto.setTeacherCount(teacherCount);
        
        return dto;
    }

    public void updateSchoolInfo(School school, UpdateSchoolInfoDto schoolDto) 
    {
        if (schoolDto.getName() != null && !schoolDto.getName().isEmpty()) {
            school.setName(schoolDto.getName());
        }
        if (schoolDto.getAddress() != null && !schoolDto.getAddress().isEmpty()) {
            school.setAddress(schoolDto.getAddress());
        }
        if (schoolDto.getPhrase() != null && !schoolDto.getPhrase().isEmpty()) {
            school.setPhrase(schoolDto.getPhrase());
        }
        if (schoolDto.getYear() != null) {
            school.setYear(schoolDto.getYear()); 
        }
        schoolRepository.save(school);
    }

    public School createSchool(CreateSchoolDto schoolDto) {
        School school = new School();
        school.setName(schoolDto.getName());
        school.setAddress(schoolDto.getAddress());
        school.setYear(schoolDto.getYear());

        SchoolContacts contacts = new SchoolContacts();
        contacts.setSchool(school);
        school.setContacts(contacts);

        return schoolRepository.save(school);
    }


    private TeacherInfoDto convertToTeacherInfoDto(UserEntity teacher) {
        TeacherInfoDto dto = new TeacherInfoDto();
        dto.setId(teacher.getId());
        dto.setFirstName(teacher.getFirstName());
        dto.setLastName(teacher.getLastName());
        dto.setProfileImage(teacher.getProfileImage());
        dto.setDuty(teacher.getDuty());
        dto.setRole(String.valueOf(teacher.getRole()));

        return dto;
    }

    public List<UserEntity> getTeachersBySchool(Integer schoolId) {
        return userRepository.findBySchoolIdAndRole(schoolId, RoleType.TEACHER);
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

    public Achievement updateAchievement(Integer schoolId, Integer achievementId, MultipartFile image, AchievementDto achievementDto) {
        Achievement achievement = achievementRepository.findById(achievementId)
                .orElseThrow(() -> new RuntimeException("Achievement not found"));

        if (!achievement.getSchool().getId().equals(schoolId)) {
            throw new RuntimeException("Achievement does not belong to the specified school");
        }
    
        achievement.setTitle(achievementDto.getTitle());
        achievement.setDescription(achievementDto.getDescription());
        
        try {
            if (image != null && !image.isEmpty()) {
                if (achievement.getImage() != null && !achievement.getImage().isEmpty()) {
                    storageService.deleteFile(achievement.getImage());
                }
                String url = storageService.uploadFile(image, CloudFolder.ACHIEVEMENT_IMAGES);
                achievement.setImage(url);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image: " + e.getMessage(), e);
        }
        
        return achievementRepository.save(achievement);
    }

    public void deleteAchievement(Integer schoolId, Integer achievementId) {
    
        Achievement achievement = achievementRepository.findById(achievementId)
                .orElseThrow(() -> new RuntimeException("Achievement not found"));

        if (!achievement.getSchool().getId().equals(schoolId)) {
            throw new RuntimeException("Achievement does not belong to the specified school");
        }
    
        achievementRepository.delete(achievement);
    }
    

    public String addImageToSchoolGallery(String authorizationHeader, MultipartFile file) throws Exception
    {
        School school = userService.getUserFromHeader(authorizationHeader).getSchool();

        String url = storageService.uploadFile(file, CloudFolder.SCHOOL_GALLERIES);

        SchoolGallery schoolGallery = new SchoolGallery();
        schoolGallery.setGalleryImage(url);
        schoolGallery.setSchool(school);

        schoolGalleryRepository.save(schoolGallery);
        return url;
    }

    public List<SchoolGallery> getAllGalleryImages(String authorizationHeader) {
        School school = userService.getUserFromHeader(authorizationHeader).getSchool();
        return schoolGalleryRepository.findBySchool(school);
    }

    public void deleteGalleryImage(String authorizationHeader, String image) throws Exception {
        School school = userService.getUserFromHeader(authorizationHeader).getSchool();

        SchoolGallery schoolGallery = schoolGalleryRepository.findBySchoolAndGalleryImage(school, image)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        storageService.deleteFile(image);

        schoolGalleryRepository.delete(schoolGallery);
    }
  
}

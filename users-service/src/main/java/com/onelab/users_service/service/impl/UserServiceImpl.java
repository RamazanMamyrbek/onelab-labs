package com.onelab.users_service.service.impl;

import com.onelab.users_service.entity.PendingUser;
import com.onelab.users_service.entity.Users;
import com.onelab.users_service.entity.elastic.UsersIndex;
import com.onelab.users_service.mapper.UserMapper;
import com.onelab.users_service.producer.UserServiceProducer;
import com.onelab.users_service.repository.elastic.UsersSearchRepository;
import com.onelab.users_service.repository.jpa.PendingUserRepository;
import com.onelab.users_service.repository.jpa.UserRepository;
import com.onelab.users_service.service.EmailService;
import com.onelab.users_service.service.ExchangeRateService;
import com.onelab.users_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.onelab.common.dto.request.*;
import org.onelab.common.dto.response.CourseResponseDto;
import org.onelab.common.dto.response.NotificationResponseDto;
import org.onelab.common.dto.response.PendingUserResponseDto;
import org.onelab.common.dto.response.UsersResponseDto;
import org.onelab.common.enums.Currency;
import org.onelab.common.enums.Role;
import org.onelab.common.exception.BadRequestException;
import org.onelab.common.exception.ResourceNotFoundException;
import org.onelab.common.feign.CourseFeignClient;
import org.onelab.common.feign.NotificationFeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PendingUserRepository pendingUserRepository;
    private final UserMapper userMapper;
    private final UserServiceProducer userServiceProducer;
    private final PasswordEncoder passwordEncoder;
    private final NotificationFeignClient notificationFeignClient;
    private final CourseFeignClient courseFeignClient;
    private final UsersSearchRepository usersSearchRepository;
    private final EmailService emailService;
    private static final Random RANDOM = new Random();
    private final ExchangeRateService exchangeRateService;


    @Override
    @Transactional
    public void assignCourseToStudent(AssignCourseDto assignCourseDto, String email, String token) {
        Users student = getStudentById(assignCourseDto.userId());
        CourseResponseDto courseResponseDto = courseFeignClient.getCourseById(assignCourseDto.courseId(), token).getBody();
        Users teacher = getUserByEmail(email);
        if(!teacher.getRole().equals(Role.ROLE_TEACHER) || !courseResponseDto.teacherId().equals(teacher.getId())) {
            throw BadRequestException.invalidTeacherException(teacher.getId(), courseResponseDto.id());
        }
        if(student.getCourseIds().contains(assignCourseDto.courseId())) {
            throw BadRequestException.userAlreadyHasCourse(student.getId(), courseResponseDto.id());
        }
        student.getCourseIds().add(assignCourseDto.courseId());
        student = userRepository.save(student);
        userServiceProducer.sendNotification(new NotificationDto(
                assignCourseDto.userId(),
                "You have been enrolled to the course: %s".formatted(courseResponseDto.name())
        ));
    }

    @Override
    @Transactional
    public CourseResponseDto buyCourse(Long courseId, String email, String token) {
        Users user = getUserByEmail(email);
        CourseResponseDto courseResponseDto = courseFeignClient.getCourseById(courseId, token).getBody();
        canBuyCourse(user, courseResponseDto);
        user.setBalance(user.getBalance().subtract(new BigDecimal(courseResponseDto.price())));
        user.getCourseIds().add(courseId);
        user = userRepository.save(user);
        userServiceProducer.sendNotification(new NotificationDto(
                user.getId(),
                "You have been enrolled to the course: ".formatted(courseResponseDto.name())
        ));
        return courseResponseDto;
    }

    private void canBuyCourse(Users user, CourseResponseDto courseResponseDto) {
        if(courseResponseDto.teacherId().equals(user.getId())) {
            throw BadRequestException.cannotBuyOwnCourse();
        }
        if(user.getCourseIds().contains(courseResponseDto.id())) {
            throw BadRequestException.userAlreadyHasCourse(user.getId(), courseResponseDto.id());
        }
        if(user.getBalance().compareTo(new BigDecimal(courseResponseDto.price())) < 0) {
            throw BadRequestException.balanceNotEnough(user.getId(), courseResponseDto.id());
        }
    }


    @Override
    public List<CourseResponseDto> getStudentCourses(Long studentId, String token) {
        Users student = getStudentById(studentId);
        List<CourseResponseDto> courseResponseDtoList = courseFeignClient.getCoursesByIds(student.getCourseIds(), token).getBody();
        return courseResponseDtoList;
    }

    @Override
    public List<UsersResponseDto> getAllUsers(Role role) {
        return userRepository.findAll()
                .stream()
                .filter(user -> role == null || user.getRole().equals(role))
                .map(userMapper::mapToUserResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public PendingUserResponseDto registerUser(UserRegisterRequestDto requestDto) {
        if(userRepository.existsByEmail(requestDto.email())) {
            throw BadRequestException.userAlreadyExistsException(requestDto.email());
        }
        String code = generateConfirmationCode();
        PendingUser pendingUser;
        if(pendingUserRepository.existsByEmail(requestDto.email())){
            pendingUser = pendingUserRepository.findByEmail(requestDto.email()).orElseThrow(
                    () -> ResourceNotFoundException.pendingUserNotFound(requestDto.email())
            );
            pendingUser.setCode(code);
        } else {
            pendingUser = PendingUser
                    .builder()
                    .email(requestDto.email())
                    .name(requestDto.name())
                    .country(requestDto.country())
                    .age(requestDto.age())
                    .passwordHash(passwordEncoder.encode(requestDto.password()))
                    .role(requestDto.role())
                    .code(code)
                    .build();
        }
        pendingUser = pendingUserRepository.save(pendingUser);
        emailService.sendConfirmationCode(pendingUser.getEmail(), code);
        return new PendingUserResponseDto(pendingUser.getEmail(), "Confirmation code was sent to email");
    }

    @Override
    public Users getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User with email %s was not found".formatted(email))
        );
    }

    @Override
    public UsersResponseDto getUserInfoById(Long userId) {
        return userMapper.mapToUserResponseDTO(getUserById(userId));
    }

    @Override
    public UsersResponseDto getUserProfileByEmail(String name) {
        return userMapper.mapToUserResponseDTO(getUserByEmail(name));
    }

    @Override
    @Transactional
    public UsersResponseDto editProfile(UserEditRequestDto requestDto, String email) {
        Users user = getUserByEmail(email);
        user.setRole(requestDto.role());
        user.setName(requestDto.name());
        user.setCountry(requestDto.country());
        user.setAge(requestDto.age());
        user = userRepository.save(user);
        reindexUsers();
        userServiceProducer.sendNotification(new NotificationDto(user.getId(), "Your account was edited"));
        return userMapper.mapToUserResponseDTO(user);
    }

    @Override
    @Transactional
    public void deleteUserByEmail(String email) {
        Users user = getUserByEmail(email);
        userRepository.delete(user);
        reindexUsers();
        userServiceProducer.sendNotification(new NotificationDto(user.getId(), "User with email %s was deleted".formatted(email)));
    }

    @Override
    public List<NotificationResponseDto> getNotifications(String email, String token) {
        Users user = getUserByEmail(email);
        List<NotificationResponseDto> responseDtoList = notificationFeignClient.getNotificationsByUserId(token, user.getId());
        return responseDtoList;
    }

    @Override
    public List<UsersResponseDto> searchUsers(String nameQuery, Long minAge, Long maxAge, String country, Role role, int page, int size) {
        Pageable pageRequest = PageRequest.of(page, size);
        if (nameQuery == null || nameQuery.isBlank()) {
            return getAllUsers(role, pageRequest)
                    .parallelStream()
                    .filter(usersResponseDto -> country == null || usersResponseDto.country().equals(country))
                    .filter(usersResponseDto ->
                            usersResponseDto.age() >= (minAge != null ? minAge : 7) &&
                                    (maxAge == null || usersResponseDto.age() <= maxAge))
                    .filter(usersResponseDto -> role == null || usersResponseDto.role().equals(role.name()))
                    .collect(Collectors.toList());
        }
        Page<UsersIndex> usersIndexPage = usersSearchRepository.findAllByNameContainingIgnoreCase(nameQuery, pageRequest);
        Set<Long> idsSet = usersIndexPage.stream()
                .map(UsersIndex::getId)
                .collect(Collectors.toSet());

        return userRepository.findAllById(idsSet)
                .parallelStream()
                .map(userMapper::mapToUserResponseDTO)
                .filter(usersResponseDto -> country == null || usersResponseDto.country().equals(country))
                .filter(usersResponseDto ->
                        usersResponseDto.age() >= (minAge != null ? minAge : 7) &&
                                (maxAge == null || usersResponseDto.age() <= maxAge))
                .filter(usersResponseDto -> role == null || usersResponseDto.role().equals(role.name()))
                .toList();
    }


    @Override
    public List<UsersResponseDto> getStudentsForCourse(Long courseId) {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getCourseIds().contains(courseId))
                .filter(user -> user.getRole().equals(Role.ROLE_STUDENT))
                .map(userMapper::mapToUserResponseDTO)
                .collect(Collectors.toList());
    }


    private Users getTeacherById(Long id) {
        Users user = getUserById(id);
        if(!user.getRole().equals(Role.ROLE_TEACHER)) {
            throw ResourceNotFoundException.teacherNotFound(id);
        }
        return user;
    }

    private Users getStudentById(Long id) {
        Users user = getUserById(id);
        if(!user.getRole().equals(Role.ROLE_STUDENT)) {
            throw ResourceNotFoundException.studentNotFound(id);
        }
        return user;
    }

    @Override
    public Users getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> ResourceNotFoundException.userNotFound(userId)
        );
    }

    @Override
    @Transactional
    public void removeCourseFromStudents(Long courseId) {
        userRepository.removeCourseFromStudents(courseId);
    }

    @Override
    @Transactional
    public void expelStudentFromCourse(ExpelFromCourseDto expelFromCourseDto, String email, String token) {
        Users student = getStudentById(expelFromCourseDto.studentId());
        CourseResponseDto courseResponseDto = courseFeignClient.getCourseById(expelFromCourseDto.courseId(), token).getBody();
        Users teacher = getUserByEmail(email);
        if(!teacher.getRole().equals(Role.ROLE_TEACHER) || !courseResponseDto.teacherId().equals(teacher.getId())) {
            throw BadRequestException.invalidTeacherException(teacher.getId(), courseResponseDto.id());
        }
        student.getCourseIds().remove(courseResponseDto.id());
        userRepository.save(student);
        userServiceProducer.sendNotification(new NotificationDto(
                expelFromCourseDto.studentId(),
                "You have been removed from the course: %s".formatted(courseResponseDto.name())
        ));
    }

    @Override
    public boolean studentHasCourse(Long courseId, String email) {
        Users student = getUserByEmail(email);
        return student.getCourseIds().contains(courseId);
    }

    @Override
    @Transactional
    public UsersResponseDto confirmEmail(ConfirmEmailRequestDto confirmEmailRequestDto) {
        PendingUser pendingUser = pendingUserRepository.findByEmail(confirmEmailRequestDto.email()).orElseThrow(
                () -> ResourceNotFoundException.pendingUserNotFound(confirmEmailRequestDto.email())
        );
        if(!pendingUser.getCode().equals(confirmEmailRequestDto.code())) {
            throw BadRequestException.invalidConfirmationCode();
        }
        Users user = userMapper.pendingUserToUser(pendingUser);
        user = userRepository.save(user);
        pendingUserRepository.deleteById(pendingUser.getId());
        userServiceProducer.sendNotification(new NotificationDto(
                user.getId(),
                "User with email %s was registered".formatted(user.getEmail())
        ));
        reindexUsers();
        return userMapper.mapToUserResponseDTO(user);
    }

    @Override
    @Transactional
    public void resendCode(String email) {
        PendingUser pendingUser = pendingUserRepository.findByEmail(email).orElseThrow(
                () -> ResourceNotFoundException.pendingUserNotFound(email)
        );
        String code = generateConfirmationCode();
        emailService.sendConfirmationCode(email, code);
        pendingUser.setCode(code);
        pendingUserRepository.save(pendingUser);
    }

    @Override
    @Transactional
    public UsersResponseDto replenishBalance(String email, Long balance, Currency currency) {
        Users user = getUserByEmail(email);
        BigDecimal newBalance = exchangeRateService.toUsd(new BigDecimal(balance), currency.name());
        user.setBalance(user.getBalance().add(newBalance));
        userServiceProducer.sendNotification(new NotificationDto(
                user.getId(),
                "Balance was replenished with %s USD".formatted(newBalance)
        ));
        return userMapper.mapToUserResponseDTO(userRepository.save(user));
    }

    @Override
    @Transactional
    public UsersResponseDto clearBalance(String email) {
        Users user = getUserByEmail(email);
        user.setBalance(new BigDecimal(0));
        userServiceProducer.sendNotification(new NotificationDto(
                user.getId(),
                "Balance was cleared"
        ));
        return userMapper.mapToUserResponseDTO(userRepository.save(user));
    }

    private void reindexUsers() {
        usersSearchRepository.saveAll(
                userRepository.findAll().stream().map(
                                user -> new UsersIndex(user.getId(), user.getName())
                        )
                        .collect(Collectors.toList())
        );
    }

    private List<UsersResponseDto> getAllUsers(Role role, Pageable pageRequest) {
        return userRepository.findAll(pageRequest)
                .stream()
                .filter(user -> role == null || user.getRole().equals(role))
                .map(userMapper::mapToUserResponseDTO)
                .toList();
    }

    private String generateConfirmationCode() {
        return String.format("%04d", RANDOM.nextInt(10000));
    }
}


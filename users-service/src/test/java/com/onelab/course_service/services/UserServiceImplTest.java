package com.onelab.course_service.services;


import com.onelab.users_service.entity.enums.Role;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private UserMapper userMapper;
//
//    @Mock
//    private UserServiceProducer userServiceProducer;
//
//    @Mock
//    private KafkaClient kafkaClient;
//
//    @InjectMocks
//    private UserServiceImpl userService;
//
//    private Users student;
//    private Users teacher;
//
//    @BeforeEach
//    void setUp() {
//        student = Users.builder().id(1L).name("Student").role(Role.STUDENT).build();
//        teacher = Users.builder().id(2L).name("Teacher").role(Role.TEACHER).build();
//    }
//
//    @Test
//    void getAllStudents_shouldReturnStudentList() {
//        when(userRepository.findAllByRole(Role.STUDENT)).thenReturn(List.of(student));
//        when(userMapper.mapToUserResponseDTO(any(Users.class))).thenReturn(new UsersResponseDto(student.getId(), student.getName(), Role.STUDENT.name()));
//
//        List<UsersResponseDto> students = userService.getAllStudents();
//
//        assertThat(students).hasSize(1);
//        assertThat(students.get(0).id()).isEqualTo(student.getId());
//        verify(userRepository).findAllByRole(Role.STUDENT);
//    }
//
//    @Test
//    void getAllTeachers_shouldReturnTeacherList() {
//        when(userRepository.findAllByRole(Role.TEACHER)).thenReturn(List.of(teacher));
//        when(userMapper.mapToUserResponseDTO(any(Users.class))).thenReturn(new UsersResponseDto(teacher.getId(), teacher.getName(), Role.TEACHER.name()));
//
//        List<UsersResponseDto> teachers = userService.getAllTeachers();
//
//        assertThat(teachers).hasSize(1);
//        assertThat(teachers.get(0).id()).isEqualTo(teacher.getId());
//        verify(userRepository).findAllByRole(Role.TEACHER);
//    }
//
//    @Test
//    void createStudent_shouldSaveStudentAndReturnResponse() {
//        UserRequestDto studentDto = new UserRequestDto("Student");
//        UsersResponseDto responseDto = new UsersResponseDto(1L, "Student",  Role.STUDENT.name());
//
//        when(userRepository.save(any(Users.class))).thenReturn(student);
//        when(userMapper.mapToUserResponseDTO(any(Users.class))).thenReturn(responseDto);
//
//        UsersResponseDto createdStudent = userService.createStudent(studentDto);
//
//        assertThat(createdStudent.id()).isEqualTo(1L);
//        assertThat(createdStudent.name()).isEqualTo("Student");
//        verify(userRepository).save(any(Users.class));
//        verify(userServiceProducer).sendNotification(any());
//    }
//
//    @Test
//    void createTeacher_shouldSaveTeacherAndReturnResponse() {
//        UserRequestDto teacherDto = new UserRequestDto("Teacher");
//        UsersResponseDto responseDto = new UsersResponseDto(2L, "Teacher", Role.TEACHER.name());
//
//        when(userRepository.save(any(Users.class))).thenReturn(teacher);
//        when(userMapper.mapToUserResponseDTO(any(Users.class))).thenReturn(responseDto);
//
//        UsersResponseDto createdTeacher = userService.createTeacher(teacherDto);
//
//        assertThat(createdTeacher.id()).isEqualTo(2L);
//        assertThat(createdTeacher.name()).isEqualTo("Teacher");
//        verify(userRepository).save(any(Users.class));
//        verify(userServiceProducer).sendNotification(any());
//    }
//
//    @Test
//    void deleteTeacher_shouldRemoveTeacher() {
//        DeleteUserDto deleteUserDto = new DeleteUserDto(2L);
//        when(userRepository.findById(2L)).thenReturn(Optional.of(teacher));
//
//        userService.deleteTeacher(deleteUserDto);
//
//        verify(userRepository).delete(teacher);
//        verify(userServiceProducer).sendNotification(any());
//    }
//
//    @Test
//    void deleteStudent_shouldRemoveStudent() {
//        DeleteUserDto deleteUserDto = new DeleteUserDto(1L);
//        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
//
//        userService.deleteStudent(deleteUserDto);
//
//        verify(userRepository).delete(student);
//        verify(userServiceProducer).sendNotification(any());
//    }
//
//    @Test
//    void deleteTeacher_shouldThrowExceptionIfNotFound() {
//        DeleteUserDto deleteUserDto = new DeleteUserDto(99L);
//        when(userRepository.findById(99L)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> userService.deleteTeacher(deleteUserDto))
//                .isInstanceOf(ResourceNotFoundException.class);
//    }
//
//    @Test
//    void deleteStudent_shouldThrowExceptionIfNotFound() {
//        DeleteUserDto deleteUserDto = new DeleteUserDto(99L);
//        when(userRepository.findById(99L)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> userService.deleteStudent(deleteUserDto))
//                .isInstanceOf(ResourceNotFoundException.class);
//    }
}

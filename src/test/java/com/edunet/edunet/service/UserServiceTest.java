package com.edunet.edunet.service;

import com.edunet.edunet.dto.GetUserRequest;
import com.edunet.edunet.dto.PostUserRequest;
import com.edunet.edunet.dto.UpdatePasswordRequest;
import com.edunet.edunet.exception.NotAllowedException;
import com.edunet.edunet.exception.ResourceNotFoundException;
import com.edunet.edunet.model.Branch;
import com.edunet.edunet.model.Topic;
import com.edunet.edunet.model.User;
import com.edunet.edunet.repository.BranchRepository;
import com.edunet.edunet.repository.RoleRepository;
import com.edunet.edunet.repository.TopicRepository;
import com.edunet.edunet.repository.UserRepository;
import com.edunet.edunet.security.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.verification.VerificationMode;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationService authService;

    @Mock
    private Clock clock;

    @InjectMocks
    private UserService userService;

    AutoCloseable autoCloseable;

    @BeforeEach
    public void setup() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testCreateUser_ShouldSucceed() {
        Branch branch = ServiceTestUtil.BRANCH;
        PostUserRequest userData = ServiceTestUtil.POST_USER_DTO;
        User user = ServiceTestUtil.USER;
        GetUserRequest expected = ServiceTestUtil.GET_USER_DTO;

        when(passwordEncoder.encode(userData.password())).thenReturn(userData.password());
        when(userRepository.existsByHandle(userData.handle())).thenReturn(false);
        when(branchRepository.findBranchByName(userData.branch())).thenReturn(Optional.of(branch));
        when(clock.instant()).thenReturn(Instant.parse(ServiceTestUtil.INSTANT_STRING));
        when(clock.getZone()).thenReturn(ZoneId.of("UCT"));
        when(userRepository.save(any(User.class))).thenReturn(user);

        GetUserRequest actual = userService.save(userData);

        verify(topicRepository, times(2)).save(any(Topic.class));
        assertEquals(expected, actual);
    }

    @Test
    public void testGetUserById_ShouldSucceed() {
        User user = ServiceTestUtil.USER;
        when(userRepository.findUserById(1L)).thenReturn(Optional.of(user));

        GetUserRequest expected = ServiceTestUtil.GET_USER_DTO;
        GetUserRequest actual = userService.getUserById(1L);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetUserById_ShouldResourceNotFoundException() {
        when(userRepository.findUserById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(2L));
    }

    @Test
    public void testDeleteUser_ShouldSucceed() {
        when(authService.getAuthenticatedUserId()).thenReturn(555L);
        userService.deleteUser(555L);
        verify(userRepository, times(1)).deleteById(555L);
    }

    @Test
    public void testDeleteUser_ShouldThrowNotAllowedException() {
        when(authService.getAuthenticatedUserId()).thenReturn(555L);
        assertThrows(NotAllowedException.class, () -> userService.deleteUser(444L));
    }

    @Test
    public void testUpdatePassword_ShouldSucceed() {
        when(authService.getAuthenticatedUserId()).thenReturn(555L);
        when(userRepository.findPasswordById(555L)).thenReturn(Optional.of(("Tea>Coffee")));
        when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        when(passwordEncoder.matches(anyString(), anyString()))
                .thenAnswer(invocation -> (invocation.getArguments()[0].equals(invocation.getArguments()[1])));
        UpdatePasswordRequest data = new UpdatePasswordRequest("Tea>Coffee", "Tea>>>Coffee", "Tea>>>Coffee");
        userService.updatePassword(555L, data);
        verify(userRepository, times(1)).updatePassword(555L, "Tea>>>Coffee");
    }

    @Test
    public void testUpdatePassword_ShouldThrowNotAllowedException() {
        when(authService.getAuthenticatedUserId()).thenReturn(555L);
        assertThrows(NotAllowedException.class, () -> userService.updatePassword(444L, new UpdatePasswordRequest("", "", "")));
    }
}
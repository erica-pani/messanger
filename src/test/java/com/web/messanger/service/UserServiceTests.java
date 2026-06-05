package com.web.messanger.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.web.messanger.model.User;
import com.web.messanger.repos.UserRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

  @Mock private UserRepository userRepository;

  @Mock private BCryptPasswordEncoder encoder;

  @InjectMocks private UserService target;

  User user;
  Long validId = 15L;
  String validFirstname = "Max";
  String validLastname = "Mustermann";
  String validUsername = "mm";
  String validPassword = "password";

  @BeforeEach
  public void setUpTestCase() {
    user = new User();
    user.setId(validId);
    user.setFirstname(validFirstname);
    user.setLastname(validLastname);
    user.setUsername(validUsername);
    user.setHashed_password(validPassword);
    user.setBirthDate(LocalDate.now());
  }

  @Test
  public void saveUserTest() {

    when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    String hashedPassword = encoder.encode(validPassword);

    when(encoder.encode(validPassword)).thenReturn(hashedPassword);
    when(encoder.matches(validPassword, hashedPassword)).thenReturn(true);

    target.saveUser(user);

    ArgumentCaptor<User> savedUser = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(savedUser.capture());
    User result = savedUser.getValue();

    assertEquals(validId, result.getId());
    assertEquals(validFirstname, result.getFirstname());
    assertEquals(validLastname, result.getLastname());
    assertEquals(validUsername, result.getUsername());
    assertTrue(encoder.matches(validPassword, result.getHashed_password()));
    assertEquals(LocalDate.now(), result.getBirthDate());
    assertNotNull(result.getGroups());
    assertNotNull(result.getMessages());
  }
}

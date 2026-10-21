package com.web.messanger.model.dtos;

import java.time.LocalDate;

public record UserDTO(
    String firstname, String lastname, String username, String password, LocalDate birthDate) {}

package com.web.messanger.model.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record UserDTO(
    @NotBlank String firstname,
    @NotBlank String lastname,
    @Size(min = 4) @NotBlank String username,
    @Size(min = 6) @NotBlank @Pattern(regexp = "^\\S+$") String password,
    @NotNull LocalDate birthDate) {}

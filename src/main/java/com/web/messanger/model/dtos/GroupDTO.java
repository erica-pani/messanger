package com.web.messanger.model.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record GroupDTO(@Size(min = 4) @NotBlank String name, @NotNull Set<Long> userIds) {}

package com.web.messanger.model.dtos;

import java.util.Set;

public record GroupDTO(String name, Set<Long> userIds) {}

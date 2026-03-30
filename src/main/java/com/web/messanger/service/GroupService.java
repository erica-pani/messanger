package com.web.messanger.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.messanger.model.Group;
import com.web.messanger.repos.GroupRepository;

@Service
public class GroupService {
    
    @Autowired
    private GroupRepository groupRepository;

    public void saveGroup(Group group) {

        groupRepository.save(group);

    }
}

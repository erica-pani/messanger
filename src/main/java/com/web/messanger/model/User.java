package com.web.messanger.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "Messanger_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstname;
    private String lastname;
    private String username;
    private String hashed_password;
    private LocalDate birthDate;

    @JsonIgnore
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    private List<ChatMessage> messages;

    @JsonIgnore
    @ManyToMany(mappedBy = "users")
    private Set<Group> groups;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
        name = "user_friends",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    Set<User> friends;

    @ManyToMany(mappedBy = "friends")
    @JsonIgnore
    private Set<User> friendOf;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public void setHashed_password(String hashed_password) {
        this.hashed_password = hashed_password;
    }

    public String getHashed_password() {
        return hashed_password;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public void addGroup(Group group) {
        groups.add(group);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    public Set<User> getFriends() {
        return friends;
    }

    public void setFriends(Set<User> friends) {
        this.friends = friends;
    }

    public void addFriend(User user) {
        friends.add(user);
        user.getFriends().add(user);
    }

    public void removeFriend(User user) {
        friends.remove(user);
        user.getFriends().remove(this);
    }

    public Set<User> getFriendOf() {
        return friendOf;
    }

    public void setFriendOf(Set<User> friendOf) {
        this.friendOf = friendOf;
    }

}

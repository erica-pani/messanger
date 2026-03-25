package com.web.messanger.service;

import java.io.IOException;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.web.messanger.model.Datamanager;
import com.web.messanger.model.User;
import com.web.messanger.util.Encryptor;


@Service
public class AuthService {

    private Datamanager dm;

    public AuthService() {
        try {
            this.dm = Datamanager.getInstance();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    public User login(String username, String password) throws Exception {
        if (dm.getUserMap().containsKey(username)) {

            User user = dm.getUserMap().get(username);

            if (Encryptor.verifyPassword(password, user.getHashed_password())) {
                System.out.println("eingeloggt");
                dm.scheduledSave();
                return dm.getUserMap().get(username);
            } else {
                System.out.println("Passwort falsch");
                return null;
            }
        } else {
            System.out.println("User existiert nicht");
            return null;
        }
    }

    public void register(
            String firstname, String lastname, String username, String password, LocalDate birthDate) {

        try {
            if (dm.getUserMap().containsKey(username)) {
                System.out.println("username existiert bereits");
            } else {
                dm.addNewUser(firstname, lastname, username, Encryptor.hashPassword(password), birthDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

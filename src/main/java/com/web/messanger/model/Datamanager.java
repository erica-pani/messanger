package com.web.messanger.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Datamanager {

    private static volatile Datamanager instance = null;

    private static final String USER_PATH = "userInfo.ser";
    private static Map<String, User> userMap = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService executor =
            Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture<?> pendingSave;
    private static final int SAVE_DELAY_SECONDS = 500;

    private Datamanager() throws ClassNotFoundException, IOException {
        loadAll();
    }

    public static Datamanager getInstance() throws ClassNotFoundException, IOException {

        if (instance == null) {

            synchronized (Datamanager.class) {
                if (instance == null) {
                    instance = new Datamanager();
                }
            }
        }
        return instance;
    }

    public synchronized void scheduledSave() {
        if (pendingSave != null && !pendingSave.isDone()) {
            pendingSave.cancel(false);
        }

        pendingSave =
                executor.schedule(
                        () -> {
                            try {
                                save();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        },
                        SAVE_DELAY_SECONDS,
                        TimeUnit.MILLISECONDS);
    }

    private synchronized void save() throws IOException {
        FileOutputStream fileOut = new FileOutputStream(USER_PATH);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(userMap);
        out.close();
        fileOut.close();
    }

    @SuppressWarnings("unchecked")
    private void loadUser() throws IOException, ClassNotFoundException {

        FileInputStream fileIn = new FileInputStream(USER_PATH);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        userMap = (Map<String, User>) in.readObject();
        in.close();
        fileIn.close();
    }

    private void loadAll() throws ClassNotFoundException, IOException {
        File file = new File(USER_PATH);

        if (!file.exists()) {
            System.out.println(
                    "Datei kann nicht gefunden werden!\nErwartetes Verhalten beim ersten Systemstart");
        } else {
            loadUser();
        }
    }

    public void addNewUser(
            String firstname,
            String lastname,
            String username,
            String hashed_Password,
            LocalDate birthDate)
            throws IOException, ClassNotFoundException {
        scheduledSave();
    }

    public void deleteUser(String username) throws IOException {
        if (userMap.containsKey(username)) {
            userMap.remove(username);
        }
        scheduledSave();
    }

    public Map<String, User> getUserMap() throws ClassNotFoundException, IOException {
        return Collections.unmodifiableMap(userMap);
    }

    public void shutdown() {
        try {
            if (pendingSave != null && !pendingSave.isDone()) {
                pendingSave.cancel(false);
                save();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        executor.shutdown();
    }
}

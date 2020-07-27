package com.amansiol.messenger.models;

public class Allusers_models {
    String UID;
    String name;
    String status;
    String image;
    String search;
    String onlineStatus;
    String typing;
    String email;

    public Allusers_models() {
    }

    public Allusers_models(String UID, String name, String status, String image, String search, String onlineStatus, String typing, String email) {
        this.UID = UID;
        this.name = name;
        this.status = status;
        this.image = image;
        this.search = search;
        this.onlineStatus = onlineStatus;
        this.typing = typing;
        this.email = email;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getTyping() {
        return typing;
    }

    public void setTyping(String typing) {
        this.typing = typing;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

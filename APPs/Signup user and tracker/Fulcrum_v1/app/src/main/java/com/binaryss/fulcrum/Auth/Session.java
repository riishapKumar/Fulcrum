package com.binaryss.fulcrum.Auth;

public class Session {
public String id;
public String email,name,type;

    public Session(String id, String email, String name, String type) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

}

package com.edunet.edunet.repository.projections;

import com.edunet.edunet.model.Role;

public interface Credentials {

    int getId();

    Role getRole();

    String getPassword();
}

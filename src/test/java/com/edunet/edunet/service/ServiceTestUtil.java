package com.edunet.edunet.service;

import com.edunet.edunet.dto.GetUserRequest;
import com.edunet.edunet.dto.PostUserRequest;
import com.edunet.edunet.model.Branch;
import com.edunet.edunet.model.User;

import java.time.LocalDate;

import static com.edunet.edunet.model.User.Gender.MALE;

public class ServiceTestUtil {

    public static final User USER;

    public static final GetUserRequest GET_USER_DTO;

    public static final PostUserRequest POST_USER_DTO;

    public static final Branch BRANCH;

    public static final String INSTANT_STRING = "1793-05-01T12:15:00Z";


    static {

        BRANCH = new Branch(1, "Mathematics", "hell on earth");

        POST_USER_DTO = new PostUserRequest(
                "Friedrich", "Gauss", "princeofmath", MALE.val(), "Mathematics",
                "Mathematician", "friedrich.gauss19@gmail.com", "Johanna@Osthoff", "Germany"
        );

        GET_USER_DTO = new GetUserRequest(
                POST_USER_DTO.firstName(), POST_USER_DTO.lastName(), POST_USER_DTO.handle(), POST_USER_DTO.email(),
                POST_USER_DTO.country(), MALE.name(), BRANCH.getName(), POST_USER_DTO.title(), LocalDate.of(1793, 5, 1)
        );

        USER = new User();
        USER.setFirstName(POST_USER_DTO.firstName());
        USER.setLastName(POST_USER_DTO.lastName());
        USER.setCountry(POST_USER_DTO.country());
        USER.setHandle(POST_USER_DTO.handle());
        USER.setBranch(BRANCH);
        USER.setTitle(POST_USER_DTO.title());
        USER.setGender(User.Gender.fromInt(POST_USER_DTO.gender()));
        USER.setEmail(POST_USER_DTO.email());
        USER.setCreatedOn(LocalDate.of(1793, 5, 1));

    }

}

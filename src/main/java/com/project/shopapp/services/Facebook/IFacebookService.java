package com.project.shopapp.services.Facebook;

import com.project.shopapp.dtos.FacebookDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Facebook;

import java.util.List;

public interface IFacebookService {
    Facebook createUser(FacebookDTO facebookDTO);
    Facebook getUserById(long id) throws DataNotFoundException;
    List<Facebook> getAllUsers();
    void deleteAccount(long id);
    Facebook getFacebookByEmail(String email);
}
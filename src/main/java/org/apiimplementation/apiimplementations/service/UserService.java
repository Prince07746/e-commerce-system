package org.apiimplementation.apiimplementations.service;


import org.apiimplementation.apiimplementations.repository.UserRepository;
import org.apiimplementation.apiimplementations.model.User;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository dataLoaderRepo;
    private final MongoTemplate mongoTemplate;

    public UserService(UserRepository dataLoaderUserRepository, MongoTemplate mongoTemplate) {
        this.dataLoaderRepo = dataLoaderUserRepository;
        this.mongoTemplate = mongoTemplate;
    }


    // create
    public void addUser(User user) {
        if (user != null) {
            dataLoaderRepo.save(user);
        }
    }
    // ------


    // read
    public List<User> getUserList() {
        return dataLoaderRepo.findAll();
    }

    public Optional<User> getUser(String id) {
        return dataLoaderRepo.findById(id);
    }
    // ------


    // update
    private Update getUserUpdate(User user) { // this work has a helper logic to update user
        return new Update()
                .set("name", user.getName())
                .set("lastName", user.getLastName())
                .set("email", user.getEmail())
                .set("dateOfBirth", user.getDateOfBirth());
    }

    public void updateUser(User user) {
        if (user.getId() != null) {
            Query query = new Query(Criteria.where("_id").is(user.getId()));
            mongoTemplate.updateFirst(query, getUserUpdate(user), User.class);
        }
    }

    public void updateUsers(List<User> userList) {
        if (userList == null || userList.isEmpty()) return;
        for (User user : userList) {
            if (user.getId() != null) {
                Query query = new Query(Criteria.where("_id").is(user.getId()));
                mongoTemplate.updateFirst(query, getUserUpdate(user), User.class);
            }
        }
    }
    // -------


    // delete
    public void deleteUser(String id) {
        if (dataLoaderRepo.existsById(id)) {
            dataLoaderRepo.deleteById(id);
        }
    }

    public void deleteUsers(List<String> userIds) {
        if (userIds == null || userIds.isEmpty()) return;
        dataLoaderRepo.deleteAllById(userIds);
    }

    public void deleteAllUsers(){
        dataLoaderRepo.deleteAll();
    }
    // --------


}

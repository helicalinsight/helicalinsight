/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.admin.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * This is persistent class for profile which maps it's properties with profile
 * table and persist them to a database, this class's object or instance in
 * stored in profile table as per rule this class should have the default
 * constructor, as well as getter and setter method's for it's properties
 * Annotation Entity mark this class as Entity Bean and annotation Table allows
 * you to specify the details of the table that will be used to persist the
 * entity in the database.
 *
 * @author Muqtar Ahmed
 */

@Entity
@Table(name = "profile")
public class Profile implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * each entity bean have the primary key and annotate with Id generated
     * values generate the automatically determined the most appropriate primary
     * key with strategy
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * details of the column profile name to which field's or properties is
     * mapped
     */

    private String profile_name;

    /**
     * details of the column profile value to which field's or properties is
     * mapped
     */

    private String profile_value;

    /**
     * details of the column user id to which field's or properties is mapped
     */

    private Integer user_id;

    /**
     * ManyToOne mapping between profile and user table, joins the profile table
     * with user table and map's the user id user object is loaded together with
     * profile field's as fetch type is eager
     */

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = true, insertable = false, updatable = false)
    /**
     * user object  for join profile table with user table
     */ private User user;

    /**
     * default Constructor
     */

    public Profile() {

    }

    /**
     * over loaded Constructor with three arguments id, profile name, profile
     * value, user id
     *
     * @param id            profile Id
     * @param profile_name  profile name
     * @param profile_value profile value
     * @param user_id       user Id
     */

    public Profile(int id, String profile_name, String profile_value, Integer user_id) {
        super();
        this.id = id;
        this.profile_name = profile_name;
        this.profile_value = profile_value;
        this.user_id = user_id;
    }

    /**
     * this is getter method for id which is the primary key for the profile
     * table and return the generated primary key
     *
     * @return generated id
     */

    public int getId() {
        return id;
    }

    /**
     * this is setter method for profile id
     *
     * @param profile id
     */

    public void setId(int id) {
        this.id = id;
    }

    /**
     * this is getter method for profile name
     *
     * @return profile name
     */

    public String getProfile_name() {
        return profile_name;
    }

    /**
     * this is setter method for profile name
     *
     * @param profile_name profile name
     */

    public void setProfile_name(String profile_name) {
        this.profile_name = profile_name;
    }

    /**
     * this is getter method for profile value
     *
     * @return profile value
     */

    public String getProfile_value() {
        return profile_value;
    }

    /**
     * this setter method for profile value
     *
     * @param profile_value profile value
     */

    public void setProfile_value(String profile_value) {
        this.profile_value = profile_value;
    }

    /**
     * this is getter method for user object
     *
     * @return user object
     */

    public User getUser() {
        return user;
    }

    /**
     * this is setter method for user object
     *
     * @param user object
     */

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * this is the getter method for user id from profile table
     *
     * @return user id of profile table
     */

    public Integer getUser_id() {
        return user_id;
    }

    /**
     * this is setter method for user id
     *
     * @param user_id user id
     */

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

}

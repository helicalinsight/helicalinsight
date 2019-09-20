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

package com.helicalinsight.resourcesecurity.jaxb;


import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Created by author on 25-05-2015.
 *
 * @author Rajasekhar
 */
@SuppressWarnings("ALL")
@Component
@Scope("prototype")
@XmlRootElement(name = "security")
@XmlAccessorType(XmlAccessType.FIELD)
public class Security {

    @XmlElement
    private String createdBy;


    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Security security = (Security) o;

        if (createdBy != null ? !createdBy.equals(security.createdBy) : security.createdBy != null) return false;

        return true;
    }

    @Override
    public String toString() {
        return "Security{" +
                "createdBy='" + createdBy + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return createdBy != null ? createdBy.hashCode() : 0;
    }

    @Component
    @Scope("prototype")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Share {


        @XmlAttribute
        private String mandatory = "true";

        @XmlElement(name = "roles")
        private Roles roles;

        @XmlElement(name = "users")
        private Users users;

        @Override
        public String toString() {
            return "Share{" +
                    "mandatory='" + mandatory + '\'' +
                    ", roles=" + roles +
                    ", users=" + users +
                    '}';
        }

        public Roles getRoles() {
            return roles;
        }

        public void setRoles(Roles roles) {
            this.roles = roles;
        }

        public Users getUsers() {
            return users;
        }

        public void setUsers(Users users) {
            this.users = users;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Share share = (Share) o;

            if (!mandatory.equals(share.mandatory)) return false;
            if (roles != null ? !roles.equals(share.roles) : share.roles != null) return false;
            if (users != null ? !users.equals(share.users) : share.users != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = mandatory.hashCode();
            result = 31 * result + (roles != null ? roles.hashCode() : 0);
            result = 31 * result + (users != null ? users.hashCode() : 0);
            return result;
        }
    }

    @Component
    @Scope("prototype")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Roles {


        @XmlAttribute
        private String mandatory = "true";

        @XmlElement(name = "role")
        private List<Role> roles;

        public List<Role> getRoles() {
            return roles;
        }

        public void setRoles(List<Role> roles) {
            this.roles = roles;
        }

        @Override
        public String toString() {
            return "Roles{" +
                    "roles=" + roles +
                    '}';
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;

            Roles roles1 = (Roles) other;

            if (mandatory != null ? !mandatory.equals(roles1.mandatory) : roles1.mandatory != null) return false;
            if (roles != null ? !roles.equals(roles1.roles) : roles1.roles != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = mandatory != null ? mandatory.hashCode() : 0;
            result = 31 * result + (roles != null ? roles.hashCode() : 0);
            return result;
        }
    }

    @Component
    @Scope("prototype")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Users {

        @XmlAttribute
        private String mandatory = "true";

        @XmlElement(name = "user")
        private List<User> users;

        public List<User> getUsers() {
            return users;
        }

        public void setUsers(List<User> users) {
            this.users = users;
        }

        @Override
        public String toString() {
            return "Users{" +
                    "users=" + users +
                    '}';
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;

            Users users1 = (Users) other;

            if (mandatory != null ? !mandatory.equals(users1.mandatory) : users1.mandatory != null) return false;
            if (users != null ? !users.equals(users1.users) : users1.users != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = mandatory != null ? mandatory.hashCode() : 0;
            result = 31 * result + (users != null ? users.hashCode() : 0);
            return result;
        }
    }


    @Component
    @Scope("prototype")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Role {

        @XmlAttribute
        private String name;

        @XmlAttribute
        private String id;

        @XmlValue
        private String permission;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPermission() {
            return permission;
        }

        public void setPermission(String permission) {
            this.permission = permission;
        }

        @Override
        public String toString() {
            return "Role{" +
                    "name='" + name + '\'' +
                    ", id='" + id + '\'' +
                    ", permission='" + permission + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;

            Role role = (Role) other;

            if (id != null ? !id.equals(role.id) : role.id != null) return false;
            if (name != null ? !name.equals(role.name) : role.name != null) return false;
            if (permission != null ? !permission.equals(role.permission) : role.permission != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (id != null ? id.hashCode() : 0);
            result = 31 * result + (permission != null ? permission.hashCode() : 0);
            return result;
        }
    }

    @Component
    @Scope("prototype")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class User {

        @XmlAttribute
        private String name;

        @XmlAttribute
        private String id;

        @XmlValue
        private String permission;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPermission() {
            return permission;
        }

        public void setPermission(String permission) {
            this.permission = permission;
        }

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    ", id='" + id + '\'' +
                    ", permission='" + permission + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;

            User user = (User) other;

            if (id != null ? !id.equals(user.id) : user.id != null) return false;
            if (name != null ? !name.equals(user.name) : user.name != null) return false;
            if (permission != null ? !permission.equals(user.permission) : user.permission != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (id != null ? id.hashCode() : 0);
            result = 31 * result + (permission != null ? permission.hashCode() : 0);
            return result;
        }
    }

}
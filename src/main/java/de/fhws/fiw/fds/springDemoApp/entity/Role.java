package de.fhws.fiw.fds.springDemoApp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.fhws.fiw.fds.springDemoApp.caching.EtagGenerator;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
public class Role extends AbstractEntity {
    @Column(name = "role_name", unique = true, nullable = false)
    private String roleName;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users = new HashSet<>();

    public Role() {
    }

    public Role(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String getEtag(EtagGenerator etagGenerator) {
        try {
            Role clone = (Role) this.clone();
            clone.setUsers(null);
            return etagGenerator.generateEtag(clone);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", roleName='" + roleName + '\'' +
                '}';
    }
}

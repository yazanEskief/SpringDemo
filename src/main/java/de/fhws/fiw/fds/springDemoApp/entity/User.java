package de.fhws.fiw.fds.springDemoApp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRootName;
import de.fhws.fiw.fds.springDemoApp.caching.EtagGenerator;
import jakarta.persistence.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@JsonRootName("user")
public class User extends AbstractEntity {
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "active")
    private boolean active;

    @Column(name = "password", nullable = false)
    private String password;

    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST,
            CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public User() {
    }

    public User(String username, String password, boolean active) {
        this.username = username;
        this.active = active;
        setPassword(password);
    }

    @Override
    public String getEtag(EtagGenerator etagGenerator) {
        try {
            User clone = (User) this.clone();
            clone.setRoles(null);
            return etagGenerator.generateEtag(clone);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.password = passwordEncoder.encode(password);
    }

    public void addAllRoles(Collection<Role> roles) {
        this.roles.addAll(roles);
    }

    public void removeRole(String role) {
        roles.removeIf(r -> r.getRoleName().equals(role));
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", active=" + active +
                '}';
    }
}

package de.fhws.fiw.fds.springDemoApp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.fhws.fiw.fds.springDemoApp.caching.EtagGenerator;
import de.fhws.fiw.fds.springDemoApp.sortingAndPagination.Sortable;
import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class AbstractEntity implements Sortable, Serializable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    protected long id;

    @JsonIgnore
    @Column(name = "created_at")
    protected LocalDateTime createdAt = LocalDateTime.now();

    @JsonIgnore
    @Column(name = "updated_AT")
    @UpdateTimestamp
    protected LocalDateTime updatedAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public abstract String getEtag(EtagGenerator etagGenerator);
}

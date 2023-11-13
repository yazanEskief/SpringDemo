package de.fhws.fiw.fds.springDemoApp.caching;

import de.fhws.fiw.fds.springDemoApp.entity.AbstractEntity;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
public class CacheController {

    private final EtagGenerator etagGenerator;

    @Autowired
    public CacheController(EtagGenerator etagGenerator) {
        this.etagGenerator = etagGenerator;
    }

    public CacheControl publicCache10Seconds() {
        return CacheControl.maxAge(10, TimeUnit.SECONDS)
                .noTransform()
                .cachePublic();
    }

    public CacheControl privateCache10Seconds() {
        return CacheControl.maxAge(10, TimeUnit.SECONDS)
                .noTransform()
                .cachePrivate();
    }

    public CacheControl publicCache1minute() {
        return CacheControl.maxAge(1, TimeUnit.MINUTES)
                .noTransform()
                .cachePublic();
    }

    public CacheControl publicCache30Seconds() {
        return CacheControl.maxAge(30, TimeUnit.SECONDS)
                .cachePublic()
                .noTransform();
    }

    public CacheControl publicCache30SecondsMustRevalidate() {
        return CacheControl.maxAge(30, TimeUnit.SECONDS)
                .cachePublic()
                .noTransform()
                .mustRevalidate();
    }

    public <T extends EntityModel<? extends AbstractEntity>> ResponseEntity<T> configureCachingForGETSingleRequests (
            HttpServletRequest request, T entity
    ) {
        Optional<String> etagFromRequest = Optional.ofNullable(request.getHeader(HttpHeaders.IF_NONE_MATCH));
        final String etag = entity.getContent().getEtag(etagGenerator);

        if (etagFromRequest.isPresent()) {
            if (etag.equals(etagFromRequest.get())) {
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
            }
        }

        return ResponseEntity.ok()
                .cacheControl(publicCache30Seconds())
                .varyBy(HttpHeaders.ACCEPT)
                .eTag(etag)
                .body(entity);
    }

    public <T extends EntityModel<E>, E extends AbstractEntity> ResponseEntity<T> configureCachingForPUTRequests (
            HttpServletRequest request, Supplier<T> entityModelSupplier, E entityFromDB) {
        Optional<String> optionalEtag = Optional.ofNullable(request.getHeader(HttpHeaders.IF_MATCH));
        final String eTag = entityFromDB.getEtag(etagGenerator);

        if (optionalEtag.isPresent()) {
            if (!optionalEtag.get().equals(eTag)) {
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                        .build();
            }
        }

        T updatedEntity = entityModelSupplier.get();
        final String newEtag = updatedEntity.getContent().getEtag(etagGenerator);
        return ResponseEntity.accepted()
                .eTag(newEtag)
                .body(updatedEntity);
    }
}

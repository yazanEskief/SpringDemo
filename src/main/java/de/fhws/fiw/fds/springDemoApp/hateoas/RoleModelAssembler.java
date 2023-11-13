package de.fhws.fiw.fds.springDemoApp.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import de.fhws.fiw.fds.springDemoApp.controller.UserController;
import de.fhws.fiw.fds.springDemoApp.entity.Role;
import de.fhws.fiw.fds.springDemoApp.sortingAndPagination.PageMetaDataImpl;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class RoleModelAssembler {

    public PagedModel<Role> toPagedModelOnUser(final Page<Role> page, final long userId) {
        var pageMetaData = new PageMetaDataImpl(page.getSize(), page.getNumber(), page.getTotalElements(),
                page.getContent().size());

        PagedModel<Role> pagedModel = PagedModel.of(page.getContent(), pageMetaData);

        pagedModel.add(linkTo(methodOn(UserController.class).getRolesOfUser(userId, page.getNumber(), page.getSize()))
                .withSelfRel()
                .withType(MediaType.APPLICATION_JSON_VALUE));

        pagedModel.add(linkTo(methodOn(UserController.class).getUserById(userId))
                .withRel("user")
                .withType(MediaType.APPLICATION_JSON_VALUE));

        if(page.hasNext()) {
            pagedModel.add(linkTo(methodOn(UserController.class).getRolesOfUser(
                    userId, page.getNumber() + 1, page.getSize()))
                    .withRel("next")
                    .withType(MediaType.APPLICATION_JSON_VALUE));
        }

        if(page.hasPrevious()) {
            pagedModel.add(linkTo(methodOn(UserController.class).getRolesOfUser(
                    userId, page.getNumber() - 1, page.getSize()))
                    .withRel("previous")
                    .withType(MediaType.APPLICATION_JSON_VALUE));
        }

        return pagedModel;
    }
}

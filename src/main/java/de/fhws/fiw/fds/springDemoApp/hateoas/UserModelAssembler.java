package de.fhws.fiw.fds.springDemoApp.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import de.fhws.fiw.fds.springDemoApp.controller.UserController;
import de.fhws.fiw.fds.springDemoApp.entity.User;
import de.fhws.fiw.fds.springDemoApp.sortingAndPagination.PageMetaDataImpl;
import de.fhws.fiw.fds.springDemoApp.sortingAndPagination.PagingAndSortingContext;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class UserModelAssembler implements RepresentationModelAssembler<User, EntityModel<User>> {

    @Override
    public EntityModel<User> toModel(User entity) {
        EntityModel<User> userModel = EntityModel.of(entity);

        userModel.add(linkTo(methodOn(UserController.class).getUserById(entity.getId()))
                .withSelfRel()
                .withType(MediaType.APPLICATION_JSON_VALUE));
        userModel.add(linkTo(methodOn(UserController.class).updateUser(entity.getId(), null))
                .withRel("updateUser")
                .withType(MediaType.APPLICATION_JSON_VALUE));
        userModel.add(linkTo(methodOn(UserController.class).deleteUser(entity.getId()))
                .withRel("deleteUser")
                .withType(MediaType.APPLICATION_JSON_VALUE));
        userModel.add(linkTo(methodOn(UserController.class).getAllUsers("", 0, 20, "id"))
                .withRel("users")
                .withType(MediaType.APPLICATION_JSON_VALUE));

        return userModel;
    }

    public PagedModel<EntityModel<User>> toPagedModel(Page<User> page,
                                                      PagingAndSortingContext pagingAndSortingContext) {
        CollectionModel<EntityModel<User>> collectionModel = toCollectionModel(page.getContent());
        var pageMetaData = new PageMetaDataImpl(page.getSize(), page.getNumber(), page.getTotalElements(),
                page.getContent().size());
        PagedModel<EntityModel<User>> pagedModel = PagedModel.of(collectionModel.getContent(), pageMetaData);

        pagedModel.add(linkTo(methodOn(UserController.class).getAllUsers("", page.getNumber(),
                page.getSize(), pagingAndSortingContext.getSortAsQueryParam()))
                .withSelfRel()
                .withType(MediaType.APPLICATION_JSON_VALUE));

        pagedModel.add(linkTo(methodOn(UserController.class).getAllUsers("", page.getNumber(),
                page.getSize(), pagingAndSortingContext.getSortAsQueryParamReversed()))
                .withRel("reversedOrder")
                .withType(MediaType.APPLICATION_JSON_VALUE));

        if(page.hasNext()) {
            pagedModel.add(linkTo(methodOn(UserController.class).getAllUsers("", page.getNumber() + 1,
                    page.getSize(), pagingAndSortingContext.getSortAsQueryParam()))
                    .withRel("next")
                    .withType(MediaType.APPLICATION_JSON_VALUE));
        }

        if(page.hasPrevious()) {
            pagedModel.add(linkTo(methodOn(UserController.class).getAllUsers("", page.getNumber() - 1,
                    page.getSize(), pagingAndSortingContext.getSortAsQueryParam()))
                    .withRel("previous")
                    .withType(MediaType.APPLICATION_JSON_VALUE));
        }

        return pagedModel;
    }
}

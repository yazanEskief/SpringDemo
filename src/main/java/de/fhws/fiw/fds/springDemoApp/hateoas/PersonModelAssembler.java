package de.fhws.fiw.fds.springDemoApp.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import de.fhws.fiw.fds.springDemoApp.controller.Dispatcher;
import de.fhws.fiw.fds.springDemoApp.controller.PersonController;
import de.fhws.fiw.fds.springDemoApp.entity.Person;
import de.fhws.fiw.fds.springDemoApp.sortingAndPagination.PageMetaDataImpl;
import de.fhws.fiw.fds.springDemoApp.sortingAndPagination.PagingAndSortingConfig;
import de.fhws.fiw.fds.springDemoApp.sortingAndPagination.PagingAndSortingContext;
import de.fhws.fiw.fds.springDemoApp.util.Operation;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class PersonModelAssembler implements RepresentationModelAssembler<Person, EntityModel<Person>> {
    @Override
    public EntityModel<Person> toModel(Person entity) {
        EntityModel<Person> personModel = EntityModel.of(entity);

        personModel.add(linkTo(methodOn(PersonController.class).getPersonById(entity.getId())).withSelfRel()
                .withType(MediaType.APPLICATION_JSON_VALUE));
        personModel.add(linkTo(methodOn(PersonController.class)
                .getAllLocationsOfPerson(entity.getId(), false,
                        PagingAndSortingConfig.PAGE, PagingAndSortingConfig.SIZE,
                        PagingAndSortingConfig.SORT)).withRel("locations")
                .withType(MediaType.APPLICATION_JSON_VALUE));
        personModel.add(linkTo(methodOn(PersonController.class).deletePerson(entity.getId())).withRel("deletePerson")
                .withType(MediaType.APPLICATION_JSON_VALUE));
        personModel.add(linkTo(methodOn(PersonController.class).updatePerson(entity.getId(), entity)).withRel("updatePerson")
                .withType(MediaType.APPLICATION_JSON_VALUE));

        return personModel;
    }

    @Override
    public CollectionModel<EntityModel<Person>> toCollectionModel(Iterable<? extends Person> entities) {
        CollectionModel<EntityModel<Person>> personModels =
                RepresentationModelAssembler.super.toCollectionModel(entities);

        personModels.add(linkTo(Dispatcher.class).withRel("dispatcher").withType(MediaType.APPLICATION_JSON_VALUE));
        personModels.add(linkTo(methodOn(PersonController.class).savePerson(null))
                .withRel("createPerson").withType(MediaType.APPLICATION_JSON_VALUE));
        return personModels;
    }

    public PagedModel<EntityModel<Person>> toPagedModel(final Page<Person> page, final String firstName,
                                                        final String lastName,
                                                        final Operation operation,
                                                        final PagingAndSortingContext pagingContext) {
        var collectionModel = toCollectionModel(page.getContent());

        var pageMetaData = new PageMetaDataImpl(page.getSize(), page.getNumber(),
                page.getTotalElements(), page.getTotalPages(), page.getContent().size());
        PagedModel<EntityModel<Person>> pagedModel = PagedModel.of(collectionModel.getContent(), pageMetaData);

        pagedModel.add(collectionModel.getLinks());

        pagedModel.add(
          linkTo(methodOn(PersonController.class)
                  .getAllPeople(firstName, lastName, operation, page.getNumber(), page.getSize(),
                          pagingContext.getSortAsQueryParam()))
                  .withSelfRel()
                  .withType(MediaType.APPLICATION_JSON_VALUE)
        );

        pagedModel.add(linkTo(methodOn(PersonController.class)
                .getAllPeople(firstName, lastName, operation,
                        page.getNumber(), page.getSize(),
                        pagingContext.getSortAsQueryParamReversed()))
                .withRel("reverseOrder").withType(MediaType.APPLICATION_JSON_VALUE));

        if (page.hasNext()) {
            pagedModel.add(linkTo(methodOn(PersonController.class)
                    .getAllPeople(firstName, lastName, operation,
                            page.getNumber() + 1, page.getSize(),
                            pagingContext.getSortAsQueryParam()))
                    .withRel("next").withType(MediaType.APPLICATION_JSON_VALUE));
        }

        if (page.hasPrevious()) {
            pagedModel.add(linkTo(methodOn(PersonController.class)
                    .getAllPeople(firstName, lastName, operation,
                            page.getNumber() - 1, page.getSize(),
                            pagingContext.getSortAsQueryParam()))
                    .withRel("previous").withType(MediaType.APPLICATION_JSON_VALUE));
        }

        return pagedModel;
    }
}

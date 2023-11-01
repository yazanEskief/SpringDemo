package de.fhws.fiw.fds.springDemoApp.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import de.fhws.fiw.fds.springDemoApp.controller.Dispatcher;
import de.fhws.fiw.fds.springDemoApp.controller.PersonController;
import de.fhws.fiw.fds.springDemoApp.entity.Person;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
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
        personModel.add(linkTo(methodOn(PersonController.class).getAllLocationsOfPerson(entity.getId(), false)).withRel("locations")
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

        personModels.add(linkTo(PersonController.class).withSelfRel().withType(MediaType.APPLICATION_JSON_VALUE));
        personModels.add(linkTo(Dispatcher.class).withRel("dispatcher").withType(MediaType.APPLICATION_JSON_VALUE));
        personModels.add(linkTo(methodOn(PersonController.class).savePerson(null))
                .withRel("createPerson").withType(MediaType.APPLICATION_JSON_VALUE));
        return personModels;
    }
}

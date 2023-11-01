package de.fhws.fiw.fds.springDemoApp.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import de.fhws.fiw.fds.springDemoApp.controller.Dispatcher;
import de.fhws.fiw.fds.springDemoApp.controller.LocationController;
import de.fhws.fiw.fds.springDemoApp.controller.PersonController;
import de.fhws.fiw.fds.springDemoApp.entity.Location;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Component
public class LocationModelAssembler implements RepresentationModelAssembler<Location, EntityModel<Location>> {
    @Override
    public EntityModel<Location> toModel(Location entity) {
        EntityModel<Location> model = EntityModel.of(entity);

        model.add(linkTo(methodOn(LocationController.class).getLocationById(entity.getId()))
                .withSelfRel()
                .withType(MediaType.APPLICATION_JSON_VALUE));
        model.add(linkTo(methodOn(LocationController.class).updateLocation(entity.getId(), null))
                .withRel("updateLocation")
                .withType(MediaType.APPLICATION_JSON_VALUE));
        model.add(linkTo(methodOn(LocationController.class).deleteLocation(entity.getId()))
                .withRel("deleteLocation")
                .withType(MediaType.APPLICATION_JSON_VALUE));

        if (entity.getPerson() != null) {
            model.add(linkTo(methodOn(PersonController.class)
                    .getSingleLocationOfPerson(entity.getPerson().getId(), entity.getId()))
                    .withRel("locationOfPerson")
                    .withType(MediaType.APPLICATION_JSON_VALUE));
            model.add(linkTo(methodOn(PersonController.class)
                    .unLinkLocationFromPerson(entity.getPerson().getId(), entity.getId()))
                    .withRel("unlinkLocationFromPerson")
                    .withType(MediaType.APPLICATION_JSON_VALUE));
            model.add(linkTo(methodOn(PersonController.class)
                    .getPersonById(entity.getPerson().getId()))
                    .withRel("person")
                    .withType(MediaType.APPLICATION_JSON_VALUE));
        }

        return model;
    }

    public EntityModel<Location> toModel(Location entity, long personId) {
        EntityModel<Location> model = toModel(entity);

        if (entity.getPerson() == null) {
            model.add(linkTo(methodOn(PersonController.class).
                    linkLocationToPerson(personId, model.getContent().getId())).withRel("linkLocationToPerson")
                    .withType(MediaType.APPLICATION_JSON_VALUE));
        }

        return model;
    }

    @Override
    public CollectionModel<EntityModel<Location>> toCollectionModel(Iterable<? extends Location> entities) {
        var locationModels = RepresentationModelAssembler.super.toCollectionModel(entities);

        locationModels.add(linkTo(LocationController.class).withSelfRel()
                .withType(MediaType.APPLICATION_JSON_VALUE));
        locationModels.add(linkTo(methodOn(LocationController.class).saveLocation(null)).withRel("createLocation")
                .withType(MediaType.APPLICATION_JSON_VALUE));
        locationModels.add(linkTo(Dispatcher.class).withRel("dispatcher")
                .withType(MediaType.APPLICATION_JSON_VALUE));

        return locationModels;
    }

    public CollectionModel<EntityModel<Location>> toCollectionModelOnPerson(Iterable<Location> entities,
                                                                            boolean showAll,
                                                                            long personId) {

        var locationModels = StreamSupport.stream(entities.spliterator(), false)
                .map(entity -> toModel(entity, personId))
                .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));

        locationModels.add(linkTo(methodOn(PersonController.class).getPersonById(personId))
                .withRel("person").withType(MediaType.APPLICATION_JSON_VALUE));

        locationModels.add(linkTo(methodOn(PersonController.class).getAllLocationsOfPerson(personId, showAll))
                .withSelfRel().withType(MediaType.APPLICATION_JSON_VALUE));

        if (showAll) {
            locationModels.add(linkTo(methodOn(PersonController.class).getAllLocationsOfPerson(personId, false))
                    .withRel("locationOfPersonOnly").withType(MediaType.APPLICATION_JSON_VALUE));
        } else {
            locationModels.add(linkTo(methodOn(PersonController.class).getAllLocationsOfPerson(personId, true))
                    .withRel("allLocations").withType(MediaType.APPLICATION_JSON_VALUE));
        }

        return locationModels;
    }
}

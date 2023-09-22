package ru.practicum.location.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.location.dto.LocationDtoCoordinates;
import ru.practicum.location.model.Location;

@UtilityClass
public class LocationMapper {
    public Location toLocation(LocationDtoCoordinates locationDtoCoordinates) {
        return Location.builder()
                .lat(locationDtoCoordinates.getLat())
                .lon(locationDtoCoordinates.getLon())
                .build();
    }

    public LocationDtoCoordinates toLocationDtoCoordinates(Location location) {
        return LocationDtoCoordinates.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }
}
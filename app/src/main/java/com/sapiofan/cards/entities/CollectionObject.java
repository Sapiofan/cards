package com.sapiofan.cards.entities;

import java.util.UUID;

public abstract class CollectionObject {
    private UUID id;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}

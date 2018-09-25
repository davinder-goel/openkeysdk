package com.openkey.sdk.api.model;

import com.google.gson.reflect.TypeToken;

final class GenericBody<T> {

    final T body;
    final TypeToken<T> typeToken;

    GenericBody(final T body, final TypeToken<T> typeToken) {
        this.body = body;
        this.typeToken = typeToken;
    }

}
/*
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.ditto.policies.model.signals.commands.exceptions;

import java.net.URI;
import java.text.MessageFormat;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

import org.eclipse.ditto.base.model.common.HttpStatus;
import org.eclipse.ditto.base.model.exceptions.DittoRuntimeException;
import org.eclipse.ditto.base.model.exceptions.DittoRuntimeExceptionBuilder;
import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.base.model.json.JsonParsableException;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.policies.model.PolicyException;


/**
 * Thrown when validating a precondition header on a Policy or one of its sub-entities leads to status
 * {@link org.eclipse.ditto.base.model.common.HttpStatus#NOT_MODIFIED}.
 */
@Immutable
@JsonParsableException(errorCode = PolicyPreconditionNotModifiedException.ERROR_CODE)
public final class PolicyPreconditionNotModifiedException extends DittoRuntimeException implements PolicyException {

    /**
     * Error code of this exception.
     */
    public static final String ERROR_CODE = ERROR_CODE_PREFIX + "precondition.notmodified";

    private static final String MESSAGE_TEMPLATE =
            "The comparison of precondition header ''if-none-match'' for the requested Policy resource evaluated to " +
                    "false. Expected: ''{0}'' not to match actual: ''{1}''.";

    private static final String DEFAULT_DESCRIPTION =
            "The comparison of the provided precondition header ''if-none-match'' with the current ETag value of the " +
                    "requested Policy resource evaluated to false. Check the value of your conditional header value.";

    private PolicyPreconditionNotModifiedException(final DittoHeaders dittoHeaders,
            @Nullable final String message,
            @Nullable final String description,
            @Nullable final Throwable cause,
            @Nullable final URI href) {
        super(ERROR_CODE, HttpStatus.NOT_MODIFIED, dittoHeaders, message, description, cause, href);
    }

    /**
     * A mutable builder for a {@link PolicyPreconditionNotModifiedException}.
     *
     * @return the builder.
     * @since 3.3.0
     */
    public static Builder newBuilder() {
        return new Builder();
    }
    
    /**
     * A mutable builder for a {@link PolicyPreconditionNotModifiedException}.
     *
     * @param expectedNotToMatch the value which was expected not to match {@code matched} value.
     * @param matched the matched value.
     * @return the builder.
     */
    public static Builder newBuilder(final String expectedNotToMatch, final String matched) {
        return new Builder(expectedNotToMatch, matched);
    }

    /**
     * Constructs a new {@code PolicyPreconditionNotModifiedException} object with given message.
     *
     * @param message detail message. This message can be later retrieved by the {@link #getMessage()} method.
     * @param dittoHeaders the headers of the command which resulted in this exception.
     * @return the new PolicyPreconditionNotModifiedException.
     * @throws NullPointerException if {@code dittoHeaders} is {@code null}.
     */
    public static PolicyPreconditionNotModifiedException fromMessage(@Nullable final String message,
            final DittoHeaders dittoHeaders) {
        return DittoRuntimeException.fromMessage(message, dittoHeaders, new Builder());
    }

    /**
     * Constructs a new {@link PolicyPreconditionNotModifiedException} object with the exception message extracted from
     * the given JSON object.
     *
     * @param jsonObject the JSON to read the
     * {@link org.eclipse.ditto.base.model.exceptions.DittoRuntimeException.JsonFields#MESSAGE} field from.
     * @param dittoHeaders the headers of the command which resulted in this exception.
     * @return the new ConditionalHeadersNotModifiedException.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws org.eclipse.ditto.json.JsonMissingFieldException if this JsonObject did not contain an error message.
     * @throws org.eclipse.ditto.json.JsonParseException if the passed in {@code jsonObject} was not in the expected
     * format.
     */
    public static PolicyPreconditionNotModifiedException fromJson(final JsonObject jsonObject,
            final DittoHeaders dittoHeaders) {
        return DittoRuntimeException.fromJson(jsonObject, dittoHeaders, new Builder());
    }

    @Override
    public DittoRuntimeException setDittoHeaders(final DittoHeaders dittoHeaders) {
        return new Builder()
                .message(getMessage())
                .description(getDescription().orElse(null))
                .cause(getCause())
                .href(getHref().orElse(null))
                .dittoHeaders(dittoHeaders)
                .build();
    }

    /**
     * A mutable builder with a fluent API for a {@link PolicyPreconditionNotModifiedException}.
     */
    @NotThreadSafe
    public static final class Builder extends DittoRuntimeExceptionBuilder<PolicyPreconditionNotModifiedException> {

        private Builder() {
            description(DEFAULT_DESCRIPTION);
        }

        private Builder(final String expectedNotToMatch, final String matched) {
            this();
            message(MessageFormat.format(MESSAGE_TEMPLATE, expectedNotToMatch, matched));
        }

        @Override
        protected PolicyPreconditionNotModifiedException doBuild(final DittoHeaders dittoHeaders,
                @Nullable final String message,
                @Nullable final String description,
                @Nullable final Throwable cause,
                @Nullable final URI href) {
            return new PolicyPreconditionNotModifiedException(dittoHeaders, message, description, cause, href);
        }

    }
}

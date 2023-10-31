/*
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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
package org.eclipse.ditto.placeholders;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import javax.annotation.concurrent.Immutable;

/**
 * Provides the {@code fn:url-encode()} function implementation.
 */
@Immutable
final class PipelineFunctionUrlEncode implements PipelineFunction {

    private static final String FUNCTION_NAME = "url-encode";

    @Override
    public String getName() {
        return FUNCTION_NAME;
    }

    @Override
    public UrlEncodeSignature getSignature() {
        return UrlEncodeSignature.INSTANCE;
    }

    @Override
    public PipelineElement apply(final PipelineElement value, final String paramsIncludingParentheses,
            final ExpressionResolver expressionResolver) {

        // check if signature matches (empty params!)
        validateOrThrow(paramsIncludingParentheses);
        return value.map(v -> {
            try {
                return URLEncoder.encode(v, StandardCharsets.UTF_8.name());
            } catch (final UnsupportedEncodingException e) {
                return URLEncoder.encode(v);
            }
        });
    }

    private void validateOrThrow(final String paramsIncludingParentheses) {
        if (!PipelineFunctionParameterResolverFactory.forEmptyParameters().test(paramsIncludingParentheses)) {
            throw PlaceholderFunctionSignatureInvalidException.newBuilder(paramsIncludingParentheses, this)
                    .build();
        }
    }

    /**
     * Describes the signature of the {@code url-encode()} function.
     */
    private static final class UrlEncodeSignature implements Signature {

        private static final UrlEncodeSignature INSTANCE = new UrlEncodeSignature();

        private UrlEncodeSignature() {
        }

        @Override
        public List<ParameterDefinition<?>> getParameterDefinitions() {
            return Collections.emptyList();
        }

        @Override
        public String toString() {
            return renderSignature();
        }
    }

}

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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.concurrent.Immutable;

/**
 * Provides the {@code fn:join('delimiter')} function implementation.
 */
@Immutable
final class PipelineFunctionJoin implements PipelineFunction {

    private static final String FUNCTION_NAME = "join";

    private final PipelineFunctionParameterResolverFactory.SingleParameterResolver parameterResolver =
            PipelineFunctionParameterResolverFactory.forStringParameter();

    @Override
    public String getName() {
        return FUNCTION_NAME;
    }

    @Override
    public Signature getSignature() {
        return JoinFunctionSignature.INSTANCE;
    }

    @Override
    public PipelineElement apply(final PipelineElement value, final String paramsIncludingParentheses,
            final ExpressionResolver expressionResolver) {

        final String joinDelimiter = parseAndResolve(paramsIncludingParentheses, expressionResolver);
        if (value.toStream().count() == 0) {
            return PipelineElement.unresolved();
        } else {
            return PipelineElement.resolved(
                    value.toStream().collect(Collectors.joining(joinDelimiter))
            );
        }
    }

    private String parseAndResolve(final String paramsIncludingParentheses,
            final ExpressionResolver expressionResolver) {

        return parameterResolver.apply(paramsIncludingParentheses, expressionResolver, this)
                .findFirst()
                .orElseThrow(
                        () -> PlaceholderFunctionSignatureInvalidException.newBuilder(paramsIncludingParentheses, this)
                                .build());
    }

    /**
     * Describes the signature of the {@code join('delimiter')} function.
     */
    private static final class JoinFunctionSignature implements Signature {

        private static final JoinFunctionSignature INSTANCE = new JoinFunctionSignature();

        private final ParameterDefinition<String> givenStringDescription;

        private JoinFunctionSignature() {
            givenStringDescription = new GivenStringParam();
        }

        @Override
        public List<ParameterDefinition<?>> getParameterDefinitions() {
            return Collections.singletonList(givenStringDescription);
        }

        @Override
        public String toString() {
            return renderSignature();
        }
    }

    /**
     * Describes the only param of the {@code join('delimiter')} function.
     */
    private static final class GivenStringParam implements ParameterDefinition<String> {

        private GivenStringParam() {
        }

        @Override
        public String getName() {
            return "delimiter";
        }

        @Override
        public Class<String> getType() {
            return String.class;
        }

        @Override
        public String getDescription() {
            return "Specifies the string to use as delimiter when joining elements of an array to a string";
        }
    }

}

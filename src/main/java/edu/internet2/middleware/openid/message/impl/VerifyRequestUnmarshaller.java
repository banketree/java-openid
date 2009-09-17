/*
 * Copyright [2009] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.internet2.middleware.openid.message.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import edu.internet2.middleware.openid.message.ParameterMap;
import edu.internet2.middleware.openid.message.VerifyRequest;
import edu.internet2.middleware.openid.message.Message.Parameter;

/**
 * Unmarshaller for {@link VerifyRequest} messages.
 */
public class VerifyRequestUnmarshaller extends AbstractMessageUnmarshaller<VerifyRequest> {

    /** {@inheritDoc} */
    public void unmarshallParameters(VerifyRequest request, ParameterMap parameters) {
        request.setAssociationHandle(parameters.get(Parameter.assoc_handle));
        request.setClaimedId(parameters.get(Parameter.claimed_id));
        request.setEndpoint(parameters.get(Parameter.op_endpoint));
        request.setIdentity(parameters.get(Parameter.identity));
        request.setInvalidateHandle(parameters.get(Parameter.invalidate_handle));
        request.setResponseNonce(parameters.get(Parameter.response_nonce));
        try {
            String returnTo = parameters.get(Parameter.return_to);
            request.setReturnTo(new URL(returnTo));
        } catch (MalformedURLException e) {
            // TODO
        }
        request.setSignature(parameters.get(Parameter.sig));
        request.getSignedFields().addAll(Arrays.asList(Parameter.signed.toString().split(",")));
    }

}
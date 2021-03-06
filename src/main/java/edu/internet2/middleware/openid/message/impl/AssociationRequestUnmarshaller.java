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

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;

import org.apache.commons.codec.binary.Base64;

import edu.internet2.middleware.openid.common.OpenIDConstants;
import edu.internet2.middleware.openid.common.ParameterMap;
import edu.internet2.middleware.openid.common.OpenIDConstants.AssociationType;
import edu.internet2.middleware.openid.common.OpenIDConstants.Parameter;
import edu.internet2.middleware.openid.common.OpenIDConstants.SessionType;
import edu.internet2.middleware.openid.message.AssociationRequest;
import edu.internet2.middleware.openid.message.encoding.EncodingUtils;
import edu.internet2.middleware.openid.message.io.UnmarshallingException;
import edu.internet2.middleware.openid.util.DatatypeHelper;

/**
 * Unmarshaller for {@link AssociationRequest} message.
 */
public class AssociationRequestUnmarshaller extends AbstractMessageUnmarshaller<AssociationRequest> {

    /** {@inheritDoc} */
    public void unmarshallParameters(AssociationRequest request, ParameterMap parameters) throws UnmarshallingException {

        SessionType sessionType = SessionType.getType(parameters.get(Parameter.session_type.QNAME));
        request.setAssociationType(AssociationType.getType(parameters.get(Parameter.assoc_type.QNAME)));

        if (sessionType != null) {
            request.setSessionType(sessionType);

            if (sessionType.equals(SessionType.DH_SHA1) || sessionType.equals(SessionType.DH_SHA256)) {

                String encodedGen = parameters.get(Parameter.dh_gen.QNAME);
                String encodedModulus = parameters.get(Parameter.dh_modulus.QNAME);

                BigInteger gen;
                if (!DatatypeHelper.isEmpty(encodedGen)) {
                    gen = new BigInteger(Base64.decodeBase64(encodedGen.getBytes()));
                } else {
                    gen = OpenIDConstants.DEFAULT_DH_GEN;
                }

                BigInteger modulus;
                if (!DatatypeHelper.isEmpty(encodedModulus)) {
                    modulus = new BigInteger(Base64.decodeBase64(encodedModulus.getBytes()));
                } else {
                    modulus = OpenIDConstants.DEFAULT_DH_MODULUS;
                }

                DHParameterSpec dhParameters = new DHParameterSpec(modulus, gen);
                request.setDHParameters(dhParameters);

                String encodedKey = parameters.get(Parameter.dh_consumer_public.QNAME);
                if (!DatatypeHelper.isEmpty(encodedKey)) {
                    try {
                        DHPublicKey publicKey = EncodingUtils.decodePublicKey(encodedKey, dhParameters);
                        request.setDHConsumerPublic(publicKey);
                    } catch (NoSuchAlgorithmException e) {
                        throw new UnmarshallingException(e);
                    } catch (InvalidKeySpecException e) {
                        throw new UnmarshallingException(e);
                    }
                }

            }
        }

    }
}
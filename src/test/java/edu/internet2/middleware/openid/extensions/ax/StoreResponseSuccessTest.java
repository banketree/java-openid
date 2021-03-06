/*
 * Copyright 2009 University Corporation for Advanced Internet Development, Inc.
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

package edu.internet2.middleware.openid.extensions.ax;

import java.net.MalformedURLException;

import edu.internet2.middleware.openid.extensions.BaseMessageExtensionTestCase;

/**
 * Test case for creating, marshalling, and unmarshalling {@link FetchRequest}.
 */
public class StoreResponseSuccessTest extends BaseMessageExtensionTestCase {

    /** Expected mode. */
    private String expectedMode;

    /** Expected success. */
    private boolean expectedSuccess;

    /**
     * Constructor.
     * 
     * @throws MalformedURLException if URL is malformed
     */
    public StoreResponseSuccessTest() throws MalformedURLException {
        messageFile = "/data/edu/internet2/middleware/openid/extensions/ax/StoreResponseSuccess.txt";

        expectedMode = StoreResponse.MODE_SUCCESS;
        expectedSuccess = true;
    }

    /** {@inheritDoc} */
    public void testMessageMarshall() {
        StoreResponse response = (StoreResponse) buildMessageExtension(StoreResponse.class);
        response.setSuccess(expectedSuccess);
        assertEquals(expectedParameters, response);
    }

    /** {@inheritDoc} */
    public void testMessageUnmarshall() {
        StoreResponse response = (StoreResponse) unmarshallMessageExtension(messageFile);

        String mode = response.getMode();
        assertEquals("StoreResponse mode was " + mode + ", expected " + expectedMode, expectedMode, mode);

        boolean success = response.getSuccess();
        assertEquals("StoreResponse success was " + success + ", expected " + expectedSuccess, expectedSuccess, success);
    }

}
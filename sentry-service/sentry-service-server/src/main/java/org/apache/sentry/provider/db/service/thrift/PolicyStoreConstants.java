/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sentry.provider.db.service.thrift;

public final class PolicyStoreConstants {
  public static final String SENTRY_GENERIC_POLICY_NOTIFICATION = "sentry.generic.policy.notification";
  public static final String SENTRY_GENERIC_POLICY_STORE = "sentry.generic.policy.store";
  public static final String SENTRY_GENERIC_POLICY_STORE_DEFAULT =
      "org.apache.sentry.provider.db.generic.service.persistent.DelegateSentryStore";
  public static class PolicyStoreServerConfig {
    public static final String NOTIFICATION_HANDLERS = "sentry.policy.store.notification.handlers";
  }
  
  private PolicyStoreConstants() {
    // Make constructor private to avoid instantiation
  }
}

/*
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

package org.apache.sentry.shell;

import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.apache.sentry.core.common.exception.SentryUserException;
import org.apache.sentry.provider.db.service.thrift.SentryPolicyServiceClient;
import org.apache.sentry.provider.db.service.thrift.TSentryGroup;
import org.apache.sentry.provider.db.service.thrift.TSentryRole;

import java.util.*;

/**
 * ShellUtil implements actual commands
 */
class ShellUtil {

    List<String> listRoles() {
        Set<TSentryRole> roles = null;
        try {
            roles = sentryClient.listRoles(authUser);
        } catch (SentryUserException e) {
            System.out.println("Error listing roles: " + e.toString());
        }
        List<String> result = new ArrayList<>();
        if (roles == null || roles.isEmpty()) {
            return result;
        }

        for(TSentryRole role: roles) {
            result.add(role.getRoleName());
        }

        Collections.sort(result);
        return result;
    }

    List<String> listRoles(String group) {
        Set<TSentryRole> roles = null;
        try {
            roles = sentryClient.listRolesByGroupName(authUser, group);
        } catch (SentryUserException e) {
            System.out.println("Error listing roles: " + e.toString());
        }
        List<String> result = new ArrayList<>();
        if (roles == null || roles.isEmpty()) {
            return result;
        }

        for(TSentryRole role: roles) {
            result.add(role.getRoleName());
        }

        Collections.sort(result);
        return result;
    }

    void createRoles(String ...roles) {
        for (String role: roles) {
            try {
                sentryClient.createRole(authUser, role);
            } catch (SentryUserException e) {
                System.out.printf("failed to create role %s: %s\n",
                        role, e.toString());
            }
        }
    }

    void removeRoles(String ...roles) {
        for (String role: roles) {
            try {
                sentryClient.dropRole(authUser, role);
            } catch (SentryUserException e) {
                System.out.printf("failed to remove role %s: %s\n",
                        role, e.toString());
            }
        }
    }

    List<String> listGroups() {
        Set<TSentryRole> roles = null;

        try {
            roles = sentryClient.listRoles(authUser);
        } catch (SentryUserException e) {
            System.out.println("Error reading roles: " + e.toString());
        }

        if (roles == null || roles.isEmpty()) {
            return new ArrayList<>();
        }

        // Set of all group names
        Set<String> groupNames = new HashSet<>();

        // Get all group names
        for (TSentryRole role: roles) {
            for (TSentryGroup group: role.getGroups()) {
                groupNames.add(group.getGroupName());
            }
        }

        List<String> result = new ArrayList<>(groupNames);

        Collections.sort(result);
        return result;
    }

    List<String> listGroupRoles() {
        Set<TSentryRole> roles = null;

        try {
            roles = sentryClient.listRoles(authUser);
        } catch (SentryUserException e) {
            System.out.println("Error reading roles: " + e.toString());
        }

        if (roles == null || roles.isEmpty()) {
            return new ArrayList<>();
        }

        // Set of all group names
        Set<String> groupNames = new HashSet<>();

        // Map group to set of roles
        Map<String, Set<String>> groupInfo = new HashMap<>();

        // Get all group names
        for (TSentryRole role: roles) {
            for (TSentryGroup group: role.getGroups()) {
                String groupName = group.getGroupName();
                groupNames.add(groupName);
                Set<String> groupRoles = groupInfo.get(groupName);
                if (groupRoles != null) {
                    // Add a new or existing role
                    groupRoles.add(role.getRoleName());
                    continue;
                }
                // Never seen this group before
                groupRoles = new HashSet<>();
                groupRoles.add(role.getRoleName());
                groupInfo.put(groupName, groupRoles);
            }
        }

        List<String> groups = new ArrayList<>(groupNames);
        Collections.sort(groups);

        // Produce printable result as
        // group1 = role1, role2, ...
        // group2 = ...
        List<String> result = new LinkedList<>();
        for(String groupName: groups) {
            result.add(groupName + " = " +
                    StringUtils.join(groupInfo.get(groupName), ", "));
        }
        return result;
    }

    void grantGroupsToRole(String roleName, String ...groups) {
        try {
            sentryClient.grantRoleToGroups(authUser, roleName, Sets.newHashSet(groups));
        } catch (SentryUserException e) {
            System.out.printf("Failed to gran role %s to groups: %s\n",
                    roleName, e.toString());
        }
    }

    void revokeGroupsFromRole(String roleName, String ...groups) {
        try {
            sentryClient.revokeRoleFromGroups(authUser, roleName, Sets.newHashSet(groups));
        } catch (SentryUserException e) {
            System.out.printf("Failed to revoke role %s to groups: %s\n",
                    roleName, e.toString());
        }
    }



    ShellUtil(SentryPolicyServiceClient sentryClient, String authUser) {
        this.sentryClient = sentryClient;
        this.authUser = authUser;
    }

    private final SentryPolicyServiceClient sentryClient;
    private final String authUser;

}

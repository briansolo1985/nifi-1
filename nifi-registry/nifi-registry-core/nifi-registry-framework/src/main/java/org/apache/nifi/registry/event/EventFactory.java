/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.registry.event;

import org.apache.nifi.registry.authorization.User;
import org.apache.nifi.registry.authorization.UserGroup;
import org.apache.nifi.registry.bucket.Bucket;
import org.apache.nifi.registry.extension.bundle.Bundle;
import org.apache.nifi.registry.extension.bundle.BundleVersion;
import org.apache.nifi.registry.flow.VersionedFlow;
import org.apache.nifi.registry.flow.VersionedFlowSnapshot;
import org.apache.nifi.registry.hook.Event;
import org.apache.nifi.registry.hook.EventFieldName;
import org.apache.nifi.registry.hook.EventType;
import org.apache.nifi.registry.security.authorization.user.NiFiUserUtils;

import java.util.Objects;

/**
 * Factory to create Events from domain objects.
 */
public class EventFactory {

    public static Event bucketCreated(final Bucket bucket) {
        return bucketEvent(bucket, EventType.CREATE_BUCKET);
    }

    public static Event bucketUpdated(final Bucket bucket) {
        return bucketEvent(bucket, EventType.UPDATE_BUCKET);
    }

    public static Event bucketDeleted(final Bucket bucket) {
            return bucketEvent(bucket, EventType.DELETE_BUCKET);
    }

    private static Event bucketEvent(final Bucket bucket, EventType eventType) {
        return new StandardEvent.Builder()
                .eventType(eventType)
                .addField(EventFieldName.BUCKET_ID, bucket.getIdentifier())
                .addField(EventFieldName.BUCKET_NAME, bucket.getName())
                .addField(EventFieldName.BUCKET_DESCRIPTION, Objects.requireNonNullElse(bucket.getDescription(), "")) //Empty string if Null
                .addField(EventFieldName.CREATED_TIMESTAMP, String.valueOf(bucket.getCreatedTimestamp()))
                .addField(EventFieldName.ALLOW_PUBLIC_READ, (bucket.isAllowPublicRead() == null) ? "" : String.valueOf(bucket.isAllowPublicRead())) //Empty string if Null
                .addField(EventFieldName.USER, NiFiUserUtils.getNiFiUserIdentity())
                .build();
    }

    public static Event flowCreated(final VersionedFlow versionedFlow) {
        return flowEvent(versionedFlow, EventType.CREATE_FLOW);
    }

    public static Event flowUpdated(final VersionedFlow versionedFlow) {
        return flowEvent(versionedFlow, EventType.UPDATE_FLOW);
    }

    public static Event flowDeleted(final VersionedFlow versionedFlow) {
        return flowEvent(versionedFlow, EventType.DELETE_FLOW);
    }

    private static Event flowEvent(final VersionedFlow versionedFlow, EventType eventType) {
        return new StandardEvent.Builder()
                .eventType(eventType)
                .addField(EventFieldName.BUCKET_ID, versionedFlow.getBucketIdentifier())
                .addField(EventFieldName.BUCKET_NAME, Objects.requireNonNullElse(versionedFlow.getBucketName(), "")) //Empty string if Null
                .addField(EventFieldName.FLOW_ID, versionedFlow.getIdentifier())
                .addField(EventFieldName.FLOW_NAME, Objects.requireNonNullElse(versionedFlow.getName(), ""))
                .addField(EventFieldName.FLOW_DESCRIPTION, Objects.requireNonNullElse(versionedFlow.getDescription(), ""))
                .addField(EventFieldName.CREATED_TIMESTAMP, String.valueOf(versionedFlow.getCreatedTimestamp()))
                .addField(EventFieldName.MODIFIED_TIMESTAMP, String.valueOf(versionedFlow.getModifiedTimestamp()))
                .addField(EventFieldName.USER, NiFiUserUtils.getNiFiUserIdentity())
                .build();
    }

    public static Event flowVersionCreated(final VersionedFlowSnapshot versionedFlowSnapshot) {
        final String versionComments = versionedFlowSnapshot.getSnapshotMetadata().getComments() == null
                ? "" : versionedFlowSnapshot.getSnapshotMetadata().getComments();

        return new StandardEvent.Builder()
                .eventType(EventType.CREATE_FLOW_VERSION)
                .addField(EventFieldName.BUCKET_ID, versionedFlowSnapshot.getSnapshotMetadata().getBucketIdentifier())
                .addField(EventFieldName.BUCKET_NAME, (versionedFlowSnapshot.getBucket() == null) ? "" : Objects.requireNonNullElse(versionedFlowSnapshot.getBucket().getName(), ""))
                .addField(EventFieldName.FLOW_ID, versionedFlowSnapshot.getSnapshotMetadata().getFlowIdentifier())
                .addField(EventFieldName.FLOW_NAME, (versionedFlowSnapshot.getBucket() == null) ? "" : Objects.requireNonNullElse(versionedFlowSnapshot.getFlow().getName(), ""))
                .addField(EventFieldName.VERSION, String.valueOf(versionedFlowSnapshot.getSnapshotMetadata().getVersion()))
                .addField(EventFieldName.MODIFIED_TIMESTAMP, String.valueOf(versionedFlowSnapshot.getSnapshotMetadata().getTimestamp()))
                .addField(EventFieldName.USER, versionedFlowSnapshot.getSnapshotMetadata().getAuthor())
                .addField(EventFieldName.COMMENT, versionComments)
                .build();
    }

    public static Event extensionBundleCreated(final Bundle bundle) {
        return new StandardEvent.Builder()
                .eventType(EventType.CREATE_EXTENSION_BUNDLE)
                .addField(EventFieldName.BUCKET_ID, bundle.getBucketIdentifier())
                .addField(EventFieldName.EXTENSION_BUNDLE_ID, bundle.getIdentifier())
                .addField(EventFieldName.USER, NiFiUserUtils.getNiFiUserIdentity())
                .build();
    }

    public static Event extensionBundleDeleted(final Bundle bundle) {
        return new StandardEvent.Builder()
                .eventType(EventType.DELETE_EXTENSION_BUNDLE)
                .addField(EventFieldName.BUCKET_ID, bundle.getBucketIdentifier())
                .addField(EventFieldName.EXTENSION_BUNDLE_ID, bundle.getIdentifier())
                .addField(EventFieldName.USER, NiFiUserUtils.getNiFiUserIdentity())
                .build();
    }

    public static Event extensionBundleVersionCreated(final BundleVersion bundleVersion) {
        return new StandardEvent.Builder()
                .eventType(EventType.CREATE_EXTENSION_BUNDLE_VERSION)
                .addField(EventFieldName.BUCKET_ID, bundleVersion.getVersionMetadata().getBucketId())
                .addField(EventFieldName.EXTENSION_BUNDLE_ID, bundleVersion.getVersionMetadata().getBundleId())
                .addField(EventFieldName.VERSION, String.valueOf(bundleVersion.getVersionMetadata().getVersion()))
                .addField(EventFieldName.USER, NiFiUserUtils.getNiFiUserIdentity())
                .build();
    }

    public static Event extensionBundleVersionDeleted(final BundleVersion bundleVersion) {
        return new StandardEvent.Builder()
                .eventType(EventType.DELETE_EXTENSION_BUNDLE_VERSION)
                .addField(EventFieldName.BUCKET_ID, bundleVersion.getVersionMetadata().getBucketId())
                .addField(EventFieldName.EXTENSION_BUNDLE_ID, bundleVersion.getVersionMetadata().getBundleId())
                .addField(EventFieldName.VERSION, String.valueOf(bundleVersion.getVersionMetadata().getVersion()))
                .addField(EventFieldName.USER, NiFiUserUtils.getNiFiUserIdentity())
                .build();
    }

    public static Event userCreated(final User user) {
        return new StandardEvent.Builder()
                .eventType(EventType.CREATE_USER)
                .addField(EventFieldName.USER_ID, user.getIdentifier())
                .addField(EventFieldName.USER_IDENTITY, user.getIdentity())
                .addField(EventFieldName.USER, NiFiUserUtils.getNiFiUserIdentity())
                .build();
    }

    public static Event userUpdated(final User user) {
        return new StandardEvent.Builder()
                .eventType(EventType.UPDATE_USER)
                .addField(EventFieldName.USER_ID, user.getIdentifier())
                .addField(EventFieldName.USER_IDENTITY, user.getIdentity())
                .addField(EventFieldName.USER, NiFiUserUtils.getNiFiUserIdentity())
                .build();
    }

    public static Event userDeleted(final User user) {
        return new StandardEvent.Builder()
                .eventType(EventType.DELETE_USER)
                .addField(EventFieldName.USER_ID, user.getIdentifier())
                .addField(EventFieldName.USER_IDENTITY, user.getIdentity())
                .addField(EventFieldName.USER, NiFiUserUtils.getNiFiUserIdentity())
                .build();
    }

    public static Event userGroupCreated(final UserGroup userGroup) {
        return new StandardEvent.Builder()
                .eventType(EventType.CREATE_USER_GROUP)
                .addField(EventFieldName.USER_GROUP_ID, userGroup.getIdentifier())
                .addField(EventFieldName.USER_GROUP_IDENTITY, userGroup.getIdentity())
                .addField(EventFieldName.USER, NiFiUserUtils.getNiFiUserIdentity())
                .build();
    }

    public static Event userGroupUpdated(final UserGroup userGroup) {
        return new StandardEvent.Builder()
                .eventType(EventType.UPDATE_USER_GROUP)
                .addField(EventFieldName.USER_GROUP_ID, userGroup.getIdentifier())
                .addField(EventFieldName.USER_GROUP_IDENTITY, userGroup.getIdentity())
                .addField(EventFieldName.USER, NiFiUserUtils.getNiFiUserIdentity())
                .build();
    }

    public static Event userGroupDeleted(final UserGroup userGroup) {
        return new StandardEvent.Builder()
                .eventType(EventType.DELETE_USER_GROUP)
                .addField(EventFieldName.USER_GROUP_ID, userGroup.getIdentifier())
                .addField(EventFieldName.USER_GROUP_IDENTITY, userGroup.getIdentity())
                .addField(EventFieldName.USER, NiFiUserUtils.getNiFiUserIdentity())
                .build();
    }
}

package com.upsight.android.analytics.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.upsight.android.Upsight;
import com.upsight.android.UpsightAnalyticsExtension;
import com.upsight.android.UpsightContext;
import com.upsight.android.analytics.internal.util.JacksonHelper.JSONObjectSerializer;
import com.upsight.android.persistence.annotation.UpsightStorableIdentifier;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;

public abstract class UpsightAnalyticsEvent<U, P> {
    @UpsightStorableIdentifier
    @JsonIgnore
    protected String id;
    @JsonProperty("ts")
    protected long mCreationTsMs;
    @JsonProperty("pub_data")
    protected P mPublisherData;
    @JsonProperty("seq_id")
    protected long mSequenceId;
    @JsonProperty("type")
    protected String mType;
    @JsonProperty("upsight_data")
    protected U mUpsightData;
    @JsonInclude(Include.NON_NULL)
    @JsonProperty("user_attributes")
    protected ObjectNode mUserAttributes;

    public static abstract class Builder<T extends UpsightAnalyticsEvent<U, P>, U, P> {
        protected abstract T build();

        public final T record(UpsightContext upsight) {
            T result = build();
            UpsightAnalyticsExtension extension = (UpsightAnalyticsExtension) upsight.getUpsightExtension(UpsightAnalyticsExtension.EXTENSION_NAME);
            if (extension != null) {
                extension.getApi().record(result);
            } else {
                upsight.getLogger().e(Upsight.LOG_TAG, "com.upsight.extension.analytics must be registered in your Android Manifest", new Object[0]);
            }
            return result;
        }
    }

    protected UpsightAnalyticsEvent(String type, U upsightData, P publisherData) {
        this.mCreationTsMs = TimeUnit.SECONDS.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        this.mType = type;
        this.mUpsightData = upsightData;
        this.mPublisherData = publisherData;
    }

    protected UpsightAnalyticsEvent() {
    }

    public long getCreationTimestampMs() {
        return this.mCreationTsMs;
    }

    public String getType() {
        return this.mType;
    }

    public String getId() {
        return this.id;
    }

    public P getPublisherData() {
        return this.mPublisherData;
    }

    protected U getUpsightData() {
        return this.mUpsightData;
    }

    public long getSequenceId() {
        return this.mSequenceId;
    }

    public JSONObject getUserAttributes() {
        return JSONObjectSerializer.fromObjectNode(this.mUserAttributes);
    }
}

package com.thd.mapserver.domain;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.thd.mapserver.domain.geom.SFAGeometry;

public class SFAFeature {

	private final String id;
	private final SFAGeometry SFAGeometry;
	private final Map<String, Object> properties;
	private final String featureType;

	public SFAFeature(String id, SFAGeometry SFAGeometry, Map<String, Object> properties) {
		this(id, SFAGeometry, properties, "");
	}

	public SFAFeature(String id, SFAGeometry SFAGeometry, String featureType) {
		this(id, SFAGeometry, Collections.emptyMap(), featureType);
	}

	public SFAFeature(String id, SFAGeometry SFAGeometry, Map<String, Object> properties, String featureType) {
		this.id = id;
		this.SFAGeometry = SFAGeometry;
		this.properties = properties;
		this.featureType = featureType;
	}

	public String getId() {
		return this.id;
	}

	public SFAGeometry getGeometry() {
		return this.SFAGeometry;
	}

	public Map<String, Object> getProperties() {
		return this.properties;
	}

	public String getFeatureType() {
		return this.featureType;
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}

		if (!(other instanceof SFAFeature)) {
			return false;
		}

		final var otherFeature = (SFAFeature) other;
		return id.equals(otherFeature.getId()) && SFAGeometry.equals(otherFeature.getGeometry())
				&& properties.equals(otherFeature.getProperties())
				&& StringUtils.equals(featureType, otherFeature.getFeatureType());
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).append(id).append(SFAGeometry).append(properties).append(featureType)
				.toHashCode();
	}

}
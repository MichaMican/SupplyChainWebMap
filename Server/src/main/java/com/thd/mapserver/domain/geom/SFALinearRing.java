package com.thd.mapserver.domain.geom;

import java.security.InvalidParameterException;
import java.util.List;

public class SFALinearRing extends SFALineString {
    public SFALinearRing(List<SFAPoint> SFAPoints) {
        super(SFAPoints);
        if(!isClosed()){
            throw new InvalidParameterException("LinearRing must be closed");
        }
    }
}

package com.thd.mapserver.domain.geom;

public abstract class SFACurve extends SFAGeometry {

    public SFACurve(int srid){
        super(srid);
    }
    public SFACurve(){
        super();
    }

    public abstract boolean isClosed();
    public abstract SFAPoint startPoint();
    public abstract SFAPoint endPoint();

}

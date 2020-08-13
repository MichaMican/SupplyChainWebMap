package com.thd.mapserver.domain.geom;

public abstract class Curve extends Geometry {
    public Curve(int srid){
        super(srid);
    }

    public Curve(){
        super();
    }

    public abstract boolean isClosed();
    public abstract Point startPoint();
    public abstract Point endPoint();

}

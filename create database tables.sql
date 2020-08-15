-- Create pois table

CREATE TABLE public.pois
(
    geometry geometry NOT NULL,
    descriptiontype text COLLATE pg_catalog."default" NOT NULL,
    id text COLLATE pg_catalog."default" NOT NULL DEFAULT uuid_generate_v4(),
    description text COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT "primaryKey" PRIMARY KEY (geometry, descriptiontype),
    CONSTRAINT "PoiDesc" FOREIGN KEY (descriptiontype)
        REFERENCES public.collections (typ) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)

TABLESPACE pg_default;

ALTER TABLE public.pois
    OWNER to postgres;

CREATE INDEX id
    ON public.pois USING gist
    (geometry)
    TABLESPACE pg_default;


-- Create collections table

CREATE TABLE public.collections
(
    typ text COLLATE pg_catalog."default" NOT NULL,
    description text COLLATE pg_catalog."default",
    title text COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT descriptions_pkey PRIMARY KEY (typ)
)

TABLESPACE pg_default;

ALTER TABLE public.collections
    OWNER to postgres;
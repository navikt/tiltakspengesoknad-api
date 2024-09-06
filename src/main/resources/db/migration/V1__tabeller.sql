DO
$$
    BEGIN
        IF
            EXISTS
                (SELECT 1 from pg_roles where rolname = 'cloudsqliamuser')
        THEN
            GRANT USAGE ON SCHEMA public TO cloudsqliamuser;
            GRANT
                SELECT
                ON ALL TABLES IN SCHEMA public TO cloudsqliamuser;
            ALTER
                DEFAULT PRIVILEGES IN SCHEMA public GRANT
                SELECT
                ON TABLES TO cloudsqliamuser;
        END IF;
    END
$$;

create table søknad
(
    id                   varchar primary key,
    versjon              varchar not null,
    søknad               jsonb not null,
    vedlegg              jsonb not null,
    fnr                  varchar not null,
    sendt_til_vedtak     boolean not null,
    journalført          boolean not null,
    opprettet            timestamp not null,
    sist_endret          timestamp not null
);

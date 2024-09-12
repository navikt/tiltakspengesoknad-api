do
$$
    begin
        if
            exists
                (select 1 from pg_roles where rolname = 'cloudsqliamuser')
        then
            grant usage on schema public to cloudsqliamuser;
            grant
                select
                on all tables in schema public to cloudsqliamuser;
            alter
                default privileges in schema public grant
                select
                on tables to cloudsqliamuser;
        end if;
    end
$$;

create table søknad
(
    id                   varchar primary key,
    versjon              varchar not null,
    søknad               jsonb null,
    søknadSpm            jsonb not null,
    vedlegg              jsonb not null,
    acr                  varchar not null,
    fnr                  varchar not null,
    fornavn              varchar null,
    etternavn            varchar null,
    sendt_til_vedtak     timestamp null,
    journalført          timestamp null,
    journalpostId        varchar null,
    opprettet            timestamp not null
);

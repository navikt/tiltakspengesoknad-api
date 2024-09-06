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
    søknad               jsonb not null,
    vedlegg              jsonb not null,
    fnr                  varchar not null,
    sendt_til_vedtak     boolean not null,
    journalført          boolean not null,
    opprettet            timestamp not null,
    sist_endret          timestamp not null
);

alter table søknad
  -- En søknad skal kun behandles av den gamle eller nye applikasjonen. Mulige valg: ['tp','arena']
  add column eier varchar not null default 'arena';
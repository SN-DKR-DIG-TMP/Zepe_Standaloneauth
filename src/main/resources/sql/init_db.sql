insert into roles values(1, 'ROLE_USER'),(2, 'ROLE_MODERATOR'),(3, 'ROLE_ADMIN');
insert into users values(1, 'admin.mediation@atos.net', '$2a$10$XDwtBVeZ9NklwB36KyJXm.D0GadPf.XdSQzM88.CqxsbvDB11pQ4m', 'admin_med', 'ACTIVATED', false);
insert into user_roles values(1, 3);
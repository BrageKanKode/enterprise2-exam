create sequence hibernate_sequence start with 1 increment by 1;

create table trip_copy
(
    id               bigint       not null,
    trip_id          varchar(255),
    number_of_copies integer      not null check (number_of_copies >= 0),
    user_user_id     varchar(255) not null,
    primary key (id)
);
create table user_data
(
    user_id    varchar(255) not null,
    active     boolean      not null,
    card_packs integer      not null check (card_packs >= 0),
    coins      integer      not null check (coins >= 0),
    people     integer      not null check (people >= 0),
    primary key (user_id)
);
alter table trip_copy
    add constraint FKk6qdqygscf4ckyjwp0xy72vxu foreign key (user_user_id) references user_data

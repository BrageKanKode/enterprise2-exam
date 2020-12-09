
create table trips (
    trip_id varchar(255) not null,
    place varchar(255) not null,
    duration integer not null check (duration >= 0),
    cost integer not null check (cost >= 0),
    primary key (trip_id));

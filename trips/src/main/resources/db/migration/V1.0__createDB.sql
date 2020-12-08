
create table user_trips (
    trip_id varchar(255) not null,
    place integer not null check (place >= 0),
    duration integer not null check (duration >= 0),
    score integer not null check (score >= 0),
    primary key (trip_id));

create table weather(
	id serial,
	location_id bigint not null,
	timestamp timestamp with time zone not null,
	temperature double precision not null,
	rain double precision not null,
	updated timestamp with time zone not null
);
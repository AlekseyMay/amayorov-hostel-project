create table category (
                          id bigint generated by default as identity,
                          category_name varchar(255),
                          short_description varchar(255),
                          primary key (id)
)

GO
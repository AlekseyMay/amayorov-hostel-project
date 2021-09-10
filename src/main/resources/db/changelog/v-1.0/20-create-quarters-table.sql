create table quarters (
                          id bigint generated by default as identity,
                          cleaning_date timestamp,
                          premises_number integer,
                          quarter_number integer,
                          category_id bigint,
                          primary key (id)
)

GO

alter table quarters
    add constraint FKhnjg8q98qldgrxd2v77wntl0i
        foreign key (category_id)
            references category

GO